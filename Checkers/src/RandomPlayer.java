

import java.util.ArrayList;
import java.util.Random;

public class RandomPlayer extends Player {

    final ThreadLocal<Random> random = new ThreadLocal<Random>();

    RandomPlayer(String name) {
        super(name);
        random.set(new Random());
    }

    @Override
    AIBoard move(AIBoard AIBoard, boolean top) {
        ArrayList<AIBoard> moves = getPossibleMoves(AIBoard, top);
        return (moves.isEmpty()) ? AIBoard : moves.get(random.get().nextInt(moves.size()));
    }
}
