package benchmark.engine;

import javax.swing.Timer;

public final class GameLoop {
    private final Timer timer;
    private long tock;

    public GameLoop(Game game) {
        timer = new Timer(16, it -> {
            var elapsed = (System.nanoTime() - tock) / 1000000000.0;
            tock = System.nanoTime();
            game.update(elapsed);
            game.render();
        });
    }

    public void start() {
        if (!timer.isRunning()) {
            tock = System.nanoTime();
            timer.restart();
        }
    }

    public void stop() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }
}
