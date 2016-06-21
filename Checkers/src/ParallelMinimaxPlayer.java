

import java.util.concurrent.ForkJoinPool;

public class ParallelMinimaxPlayer extends Player {

    final int difficulty;
    static final ForkJoinPool pool = new ForkJoinPool();

    ParallelMinimaxPlayer(String name, int difficulty) {
        super(name);
        this.difficulty = difficulty;
    }

    @Override
    AIBoard move(AIBoard AIBoard, boolean top) {
        MinimaxTask m = new MinimaxTask(AIBoard, difficulty, top);
        pool.invoke(m);
        return m.join();
    }
}
