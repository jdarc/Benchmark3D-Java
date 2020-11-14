package benchmark.engine;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;

final class Parallel {

    final static int CPUS = Runtime.getRuntime().availableProcessors();

    static void partition(int total, BiConsumer<Integer, Integer> callback) {
        try {
            var size = (int) Math.ceil(total / (float) CPUS);
            var latch = new CountDownLatch((int) Math.ceil(total / (float) size));
            for (var i = 0; i < total; i += size) {
                var offset = i;
                ForkJoinPool.commonPool().execute(() -> {
                    callback.accept(offset, Math.min(offset + size, total));
                    latch.countDown();
                });
            }
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

