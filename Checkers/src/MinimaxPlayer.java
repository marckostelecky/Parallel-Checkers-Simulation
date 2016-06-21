
public class MinimaxPlayer extends Player {

    final int difficulty;

    MinimaxPlayer(String name, int difficulty) {
        super(name);
        this.difficulty = difficulty;
    }

    @Override
    AIBoard move(AIBoard AIBoard, boolean top) {
        return maxMove(AIBoard, difficulty, top);
    }

    private AIBoard maxMove(AIBoard AIBoard, int depth, boolean top) {
        AIBoard best = new AIBoard(AIBoard, Integer.MIN_VALUE);
        if (AIBoard.top != 0 && AIBoard.bot != 0)
            for (AIBoard move : getPossibleMoves(AIBoard, top)) {
                AIBoard newAIBoard = depth == 0 ? new AIBoard(move, AIBoard.value(move, top)) : minMove(move, depth - 1, top);
                if (newAIBoard.score >= best.score) best = new AIBoard(move, newAIBoard.score);
            }
        return best;
    }

    private AIBoard minMove(AIBoard AIBoard, int depth, boolean top) {
        AIBoard best = new AIBoard(AIBoard, Integer.MAX_VALUE);
        if (AIBoard.top != 0 && AIBoard.bot != 0)
            for (AIBoard move : getPossibleMoves(AIBoard, !top)) {
                AIBoard newAIBoard = depth == 0 ? new AIBoard(move, AIBoard.value(move, top)) : maxMove(move, depth - 1, top);
                if (newAIBoard.score <= best.score) best = new AIBoard(move, newAIBoard.score);
            }
        return best;
    }
}
