

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;

public class Checkers extends JFrame {

    private static final int size = 500;
    private static final int squareSize = size / 8;
    private static final int offset = 10;
    private static final int maxMovesWithoutJump = 50;
    private int movesSinceLastJump = 0;
    private final Color buff = Color.BLACK;
    private final Color red = Color.RED;
    private final Color white = Color.WHITE;
    private final Color darkRed = Color.decode("#c00007");
    private AIBoard AIBoard = new AIBoard();
    private AIBoard move;
    private final Canvas canvas = new Canvas() {

        @Override
        public void paint(Graphics g) {
            drawAIBoard(g);
            getToolkit().sync();
        }
    };

   // public static void main(String[] args) {
        //new Checkers(9, "127.0.0.1", 2597);
       // new Checkers(8);
//        if (args.length == 0) new Checkers(8);
//        else if (args.length == 1) new Checkers(Integer.parseInt(args[0]));
//        else if (args.length == 2) new Checkers(8, args[1], Integer.parseInt(args[2]));
//        else new Checkers(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2]));
   // }
    
    public Checkers(int depth, String host, int port) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setSize(size, size);
        add(canvas);
        pack();
        setVisible(true);
        makeMoves(new RandomPlayer("Top"), new NetworkMinimaxPlayer("Bottom", depth, host, port));
        //playGames(8, 1000);
    }

    public Checkers(int depth) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setSize(size, size);
        add(canvas);
        pack();
        setVisible(true);
        makeMoves(new ParallelMinimaxPlayer("Top", depth), new ParallelMinimaxPlayer("Bottom", depth));
        //playGames(8, 1000);
    }

    private void drawAIBoard(Graphics g) {
        g.setColor(buff);
        g.fillRect(0, 0, size, size);
        g.setColor(red);
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if ((i + j) % 2 == 0)
                    g.fillRect(i * squareSize, j * squareSize, squareSize, squareSize);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, size - 1, size - 1);
        placePieces(AIBoard.top, darkRed, g, false);
        placePieces(AIBoard.bot, white, g, false);
        placePieces(AIBoard.kings, buff, g, true);
    }

    private void updateAIBoard(Graphics g, AIBoard oldAIBoard, AIBoard newAIBoard) {
        placePieces(((oldAIBoard.top ^ newAIBoard.top) & ~newAIBoard.top) | ((oldAIBoard.bot ^ newAIBoard.bot) & ~newAIBoard.bot), buff, g, false);
        placePieces((newAIBoard.top ^ oldAIBoard.top) & ~oldAIBoard.top, darkRed, g, false);
        placePieces((newAIBoard.bot ^ oldAIBoard.bot) & ~oldAIBoard.bot, white, g, false);
        placePieces((newAIBoard.kings ^ oldAIBoard.kings) & ~oldAIBoard.kings, buff, g, true);
    }

    private void placePieces(int pieces, Color c, Graphics g, boolean king) {
        g.setColor(c);
        for (int piece = 0; piece < 32; piece++)
            if ((pieces & (1 << piece)) != 0)
                g.fillOval((king ? 4 : 1) * offset / 2 + squareSize * ((piece / 4) % 2 == 0 ? 2 * (piece % 4) + 1 : 2 * (piece % 4)),
                        (king ? 4 : 1) * offset / 2 + squareSize * (piece / 4),
                        squareSize - offset * (king ? 4 : 1),
                        squareSize - offset * (king ? 4 : 1));
    }

    private void playGames(int maxDifficulty, int games) {
        Player p1 = new RandomPlayer("");
        for (int diff = 1; diff < maxDifficulty; diff++) {
            Player p2 = new ParallelMinimaxPlayer("", diff);
            int p1wins = 0;
            int p2wins = 0;
            int ties = 0;
            for (int i = 0; i < games; i++) {
                AIBoard = new AIBoard();
                for (boolean turn = false; !Thread.interrupted(); turn = !turn) {
                    move = turn ? p1.move(AIBoard, turn) : p2.move(AIBoard, turn);
                    if (AIBoard != move) {
                        if (Integer.bitCount(AIBoard.bot | AIBoard.top) != Integer.bitCount(move.bot | move.top)) movesSinceLastJump = 0;
                        else if (++movesSinceLastJump >= maxMovesWithoutJump) {
                            ties++;
                            movesSinceLastJump = 0;
                            break;
                        }
                        AIBoard = move;
                    } else {
                        if (turn) p2wins++;
                        else p1wins++;
                        System.out.println(AIBoard);
                        break;
                    }
                }
            }
            System.out.println(p1wins + " " + ties + " " + p2wins);
        }
    }

    private void makeMoves(Player p1, Player p2) {
        for (boolean turn = false; !Thread.interrupted(); turn = !turn) {
            move = turn ? p1.move(AIBoard, turn) : p2.move(AIBoard, turn);
            if (AIBoard != move) {
                canvas.repaint();
                if (Integer.bitCount(AIBoard.bot | AIBoard.top) != Integer.bitCount(move.bot | move.top))
                    movesSinceLastJump = 0;
                else if (++movesSinceLastJump >= maxMovesWithoutJump) {
                    win(null);
                    break;
                }
                AIBoard = move;
            } else {
                win((turn) ? p2 : p1);
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void win(Player p) {
        System.out.println((p == null) ? "It's a tie!" : (p.name + " wins!"));
    }
}
