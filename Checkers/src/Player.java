import java.util.ArrayList;

public abstract class Player {

    final String name;

    Player(String name) {
        this.name = name;
    }

    abstract AIBoard move(AIBoard aIAIBoard, boolean top);

    static ArrayList<AIBoard> getPossibleMoves(AIBoard AIBoard, boolean top) {
        ArrayList<AIBoard> jumps = new ArrayList<AIBoard>();
        ArrayList<AIBoard> nonjumps = new ArrayList<AIBoard>();
        for (int piece = 0; piece < 32; piece++)
            if ((top && (AIBoard.top & (1 << piece)) != 0) ^ (!top && (AIBoard.bot & (1 << piece)) != 0)) {
                jumps.addAll(getJumps(AIBoard, piece, false, top));
                if (jumps.isEmpty()) nonjumps.addAll(getNonJumps(AIBoard, piece, top));
            }
        return jumps.isEmpty() ? nonjumps : jumps;
    }

    private static ArrayList<AIBoard> getJumps(AIBoard AIBoard, int piece, boolean addThis, boolean top) {
        ArrayList<AIBoard> jumps = new ArrayList<AIBoard>();
        int pieces = AIBoard.top | AIBoard.bot;
        int opp = top ? AIBoard.bot : AIBoard.top;
        int pos = piece % 8;
        if (piece < 24 && (top ^ ((1 << piece) & AIBoard.bot & AIBoard.kings) != 0)) {
            if (((1 << piece + 9) & pieces) == 0)
                if (((1 << piece + 4) & opp) != 0 && pos > 3 && pos < 7) jumps.addAll(getJumps(jump(AIBoard, piece, piece + 4, piece + 9, top), piece + 9, true, top));
                else if (((1 << piece + 5) & opp) != 0 && pos < 3) jumps.addAll(getJumps(jump(AIBoard, piece, piece + 5, piece + 9, top), piece + 9, true, top));
            if (((1 << piece + 7) & pieces) == 0)
                if (((1 << piece + 4) & opp) != 0 && pos > 0 && pos < 4) jumps.addAll(getJumps(jump(AIBoard, piece, piece + 4, piece + 7, top), piece + 7, true, top));
                else if (((1 << piece + 3) & opp) != 0 && pos > 4) jumps.addAll(getJumps(jump(AIBoard, piece, piece + 3, piece + 7, top), piece + 7, true, top));
        }
        if (piece > 7 && (!top ^ ((1 << piece) & AIBoard.top & AIBoard.kings) != 0)) {
            if (((1 << piece - 9) & pieces) == 0)
                if (((1 << piece - 4) & opp) != 0 && pos > 0 && pos < 4) jumps.addAll(getJumps(jump(AIBoard, piece, piece - 4, piece - 9, top), piece - 9, true, top));
                else if (((1 << piece - 5) & opp) != 0 && pos > 4) jumps.addAll(getJumps(jump(AIBoard, piece, piece - 5, piece - 9, top), piece - 9, true, top));
            if (((1 << piece - 7) & pieces) == 0)
                if (((1 << piece - 4) & opp) != 0 && pos > 3 && pos < 7) jumps.addAll(getJumps(jump(AIBoard, piece, piece - 4, piece - 7, top), piece - 7, true, top));
                else if (((1 << piece - 3) & opp) != 0 && pos < 3) jumps.addAll(getJumps(jump(AIBoard, piece, piece - 3, piece - 7, top), piece - 7, true, top));
        }
        if (addThis && jumps.isEmpty()) jumps.add(AIBoard);
        return jumps;
    }

    private static AIBoard jump(AIBoard AIBoard, int piece, int jumpedPiece, int newPiece, boolean top) {
        int newBot = !top ? (AIBoard.bot | (1 << newPiece)) & ~(1 << piece) : AIBoard.bot & ~(1 << jumpedPiece);
        int newTop = top ? (AIBoard.top | (1 << newPiece)) & ~(1 << piece) : AIBoard.top & ~(1 << jumpedPiece);
        int newKings = AIBoard.kings;
        if ((newKings & (1 << piece)) != 0) newKings = (newKings | (1 << newPiece)) & ~(1 << piece);
        if ((newKings & (1 << jumpedPiece)) != 0) newKings = (newKings & ~(1 << jumpedPiece));
        if ((top && (((1 << piece) & AIBoard.top & AIBoard.kings) != 0 || newPiece >= 28))
                ^ (!top && (((1 << piece) & AIBoard.bot & AIBoard.kings) != 0 || newPiece <= 3)))
            newKings = ((newKings | (1 << newPiece)) & ~(1 << piece));
        assert ((newBot & newTop) == 0);
        if (newKings != 0) assert ((newBot & newKings) != 0 || (newTop & newKings) != 0);
        return new AIBoard(newBot, newTop, newKings);
    }

    private static ArrayList<AIBoard> getNonJumps(AIBoard AIBoard, int piece, boolean top) {
        ArrayList<AIBoard> nonjumps = new ArrayList<AIBoard>();
        int pieces = AIBoard.top | AIBoard.bot;
        int pos = piece % 8;
        if (piece < 28 && (top ^ ((1 << piece) & AIBoard.bot & AIBoard.kings) != 0)) { // if piece isn't on king row and is either top player xor has a king
            if (((1 << piece + 4) & pieces) == 0) nonjumps.add(shift(AIBoard, piece, piece + 4, top)); // if one diagonal is unoccupied, go there
            if (((1 << piece + 5) & pieces) == 0 && pos < 3) nonjumps.add(shift(AIBoard, piece, piece + 5, top)); // depending on the row, go to the other as well
            else if (((1 << piece + 3) & pieces) == 0 && pos > 4) nonjumps.add(shift(AIBoard, piece, piece + 3, top)); // unless it's on one of the walls
        }
        if (piece > 3 && (!top ^ ((1 << piece) & AIBoard.top & AIBoard.kings) != 0)) {
            if (((1 << piece - 4) & pieces) == 0) nonjumps.add(shift(AIBoard, piece, piece - 4, top));
            if (((1 << piece - 5) & pieces) == 0 && pos > 4) nonjumps.add(shift(AIBoard, piece, piece - 5, top));
            else if (((1 << piece - 3) & pieces) == 0 && pos < 3) nonjumps.add(shift(AIBoard, piece, piece - 3, top));
        }
        return nonjumps;
    }

    private static AIBoard shift(AIBoard AIBoard, int piece, int newPiece, boolean top) {
        int newBot = !top ? (AIBoard.bot | (1 << newPiece)) & ~(1 << piece) : AIBoard.bot;
        int newTop = top ? (AIBoard.top | (1 << newPiece)) & ~(1 << piece) : AIBoard.top;
        int newKings = (top && (((1 << piece) & AIBoard.top & AIBoard.kings) != 0 || newPiece >= 28))
                ^ (!top && (((1 << piece) & AIBoard.bot & AIBoard.kings) != 0 || newPiece <= 3))
                ? (AIBoard.kings | (1 << newPiece)) & ~(1 << piece) : AIBoard.kings;
        assert ((newBot & newTop) == 0);
        if (newKings != 0) assert ((newBot & newKings) != 0 || (newTop & newKings) != 0);
        return new AIBoard(newBot, newTop, newKings);
    }
}