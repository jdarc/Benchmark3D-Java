package benchmark.engine;

import java.util.Arrays;
import java.util.stream.IntStream;

public final class FpsCounter {
    private final double[] samples = IntStream.range(0, 256).mapToDouble(x -> 16000000.0).toArray();
    private int index;

    public void add(double sample) {
        samples[255 & index++] = sample;
    }

    public double average() {
        return 1000000000.0 / Math.max(Arrays.stream(samples).average().orElse(0), Double.MIN_VALUE);
    }
}
