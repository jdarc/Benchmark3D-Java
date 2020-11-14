package benchmark.engine;

final class Vector2 {
    final float x;
    final float y;

    Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    Vector2(double x, double y) {
        this((float) x, (float) y);
    }
}
