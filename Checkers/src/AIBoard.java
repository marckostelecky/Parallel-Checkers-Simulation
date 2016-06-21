import java.io.Serializable;

public class AIBoard implements Serializable {

    final int bot, top, kings, score;
    int id = -1;

    AIBoard(int bot, int top, int kings) {
        this.bot = bot; // black
        this.top = top; // red
        this.kings = kings;
        this.score = 0;
    }

    AIBoard(AIBoard AIBoard, int score) {
        this.bot = AIBoard.bot; // black
        this.top = AIBoard.top; // red
        this.kings = AIBoard.kings;
        this.score = score;
        id = AIBoard.id;
    }
    
    AIBoard(AIBoard AIBoard, int score, int id) {
        this.bot = AIBoard.bot; // black
        this.top = AIBoard.top; // red
        this.kings = AIBoard.kings;
        this.score = score;
        this.id = id;
    }

    AIBoard() {
        this(0xFFF00000, 0x00000FFF, 0);
    }

    @Override
    public String toString() {
        return padZeros(this.bot) + " " + padZeros(this.top) + " " + padZeros(this.kings);
    }

    static String padZeros(int i) {
        String r = Integer.toBinaryString(i);
        while (r.length() < 32) r = "0" + r;
        return r;
    }

    public static int value(AIBoard AIBoard, boolean top) {
        if ((top && AIBoard.bot == 0) ^ (!top && AIBoard.top == 0)) return Integer.MAX_VALUE;
        if ((top && AIBoard.top == 0) ^ (!top && AIBoard.bot == 0)) return Integer.MIN_VALUE;
        return (top ? 1 : -1) * ((Integer.bitCount(AIBoard.top) + Integer.bitCount(AIBoard.top & AIBoard.kings)) - (Integer.bitCount(AIBoard.bot) + Integer.bitCount(AIBoard.bot & AIBoard.kings)));
    }
}

