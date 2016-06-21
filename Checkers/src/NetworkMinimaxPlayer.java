

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class NetworkMinimaxPlayer extends ParallelMinimaxPlayer {

    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    Random rng = new Random();

    NetworkMinimaxPlayer(String name, int difficulty, String host, int port) {
        super(name, difficulty);
        try {
            Socket socket = new Socket(host, port);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    AIBoard move(AIBoard AIBoard, boolean top) {
        NetworkMinimaxTask m = new NetworkMinimaxTask(AIBoard, difficulty, top);
        pool.invoke(m);
        return m.join();
    }

    private class NetworkMinimaxTask extends MinimaxTask {

        NetworkMinimaxTask(AIBoard AIBoard, int depth, boolean top) {
            super(AIBoard, depth, top);
        }

        @Override
        protected AIBoard compute() {
            AIBoard best = new AIBoard(AIBoard, max ? Integer.MIN_VALUE : Integer.MAX_VALUE);
            if (AIBoard.top != 0 && AIBoard.bot != 0) {
                ArrayList<MinimaxTask> tasks = null;
                ArrayList<AIBoard> sent = null;
                for (AIBoard move : getPossibleMoves(AIBoard, max ? top : !top))
                    if (difficulty <= 0) {
                        int value = AIBoard.value(move, top);
                        if (max ? (value >= best.score) : (value <= best.score)) best = new AIBoard(move, value);
                    } else {
                        if (tasks == null || rng.nextBoolean()) {
                            if (tasks == null) tasks = new ArrayList<MinimaxTask>();
                            MinimaxTask task = new MinimaxTask(move, difficulty - 1, top, !max, -1);
                            tasks.add(task);
                        } else {
                           
                            if (sent == null) sent = new ArrayList<AIBoard>();
                            move.id = sent.size();
                            sent.add(move);
                             System.out.println("Sending AIBoard " + move + " with id " + move.id);
                            MinimaxTask task = new MinimaxTask(move, difficulty - 1, top, !max, move.id);
                            try {
                                oos.writeObject(task);
                            } catch (Exception e) {
                                System.err.println(e);
                                e.printStackTrace();
                                System.exit(1);
                            }
                        }
                    }
                if (tasks != null) {
                    invokeAll(tasks);
                    for (MinimaxTask m : tasks) {
                        AIBoard b = m.join();
                        if (max ? (b.score >= best.score) : (b.score <= best.score)) best = new AIBoard(m.AIBoard, b.score);
                    }
                }
                if (sent != null)
                    for (int i = 0; i < sent.size(); i++) {
                        AIBoard b = null;
                        try {
                            b = (AIBoard) ois.readObject();
                            System.out.println("b's id is " + b.id);
                        } catch (Exception e) {
                            System.err.println(e);
                            e.printStackTrace();
                            System.exit(1);
                        }
                        if (b != null && (max ? (b.score >= best.score) : (b.score <= best.score))) best = new AIBoard(sent.get(b.id), b.score);
                    }
            }
            return best;
        }
    }
}
