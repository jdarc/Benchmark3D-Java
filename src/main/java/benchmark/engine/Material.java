package benchmark.engine;

import java.awt.image.BufferedImage;

final class Material {
    private final int mask;
    private final int width;
    private final int height;
    private final int[] buffer;

    private Material(int width, int height, int[] buffer) {
        this.width = width;
        this.height = height;
        mask = width * height - 1;
        this.buffer = buffer;
    }

    int sample(float u, float v) {
        return buffer[mask & (int) (v * height) * width + (int) (u * width)];
    }

    static Material create(BufferedImage image) throws IllegalArgumentException {
        var rgb = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getHeight());
        return new Material(image.getWidth(), image.getHeight(), rgb);
    }
}
