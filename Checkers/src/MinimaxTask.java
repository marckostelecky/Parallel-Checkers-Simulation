
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

public class MinimaxTask extends RecursiveTask<AIBoard> implements Serializable {

        final AIBoard AIBoard;
        final int depth;
        final boolean top;
        final boolean max;
        final int id;

        MinimaxTask(AIBoard AIBoard, int depth, boolean top) {
            this(AIBoard, depth, top, true, -1);
        }

        MinimaxTask(AIBoard AIBoard, int depth, boolean top, boolean max, int id) {
            this.AIBoard = AIBoard;
            this.depth = depth;
            this.top = top;
            this.max = max;
            this.id = id;
        }

        @Override
        protected AIBoard compute() {
            AIBoard best = new AIBoard(AIBoard, max ? Integer.MIN_VALUE : Integer.MAX_VALUE);
            if (AIBoard.top != 0 && AIBoard.bot != 0) {
                ArrayList<MinimaxTask> tasks = depth > 0 ? new ArrayList<MinimaxTask>() : null;
                for (AIBoard move : Player.getPossibleMoves(AIBoard, max ? top : !top))
                    if (depth <= 0) {
                        int value = AIBoard.value(move, top);
                        if (max ? (value >= best.score) : (value <= best.score)) best = new AIBoard(move, value);
                    } else tasks.add(new MinimaxTask(move, depth - 1, top, !max, id));
                if (tasks != null) {
                    invokeAll(tasks);
                    for (MinimaxTask m : tasks) {
                        AIBoard b = m.join();
                        if (max ? (b.score >= best.score) : (b.score <= best.score)) best = new AIBoard(m.AIBoard, b.score);
                    }
                }
            }
            best.id = id;
            return best;
        }
    }