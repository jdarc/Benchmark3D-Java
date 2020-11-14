package benchmark.engine;

import static java.lang.Math.fma;

final class Edge {
    int y;
    final int height;

    float x;
    private final float xStep;

    float z;
    private final float zStep;

    float _1OverZ;
    private final float _1OverZStep;

    float tuOverZ;
    private final float tuOverZStep;

    float tvOverZ;
    private final float tvOverZStep;

    Edge(Gradients g, Vertex a, Vertex b) {
        y = Math.max(0, (int) Math.ceil(a.y));
        var yPreStep = b.y;
        height = (int) Math.ceil(yPreStep) - y;

        if (height > 0) {
            yPreStep = (float) y - a.y;
            xStep = (b.x - a.x) / (b.y - a.y);
            x = fma(yPreStep, xStep, a.x);
            var xPreStep = x - a.x;

            z = fma(yPreStep, g._zOverZdY, fma(xPreStep, g._zOverZdX, a.z));
            zStep = fma(xStep, g._zOverZdX, g._zOverZdY);

            _1OverZ = fma(yPreStep, g._1OverZdY, fma(xPreStep, g._1OverZdX, a.w));
            _1OverZStep = fma(xStep, g._1OverZdX, g._1OverZdY);

            tuOverZ = fma(yPreStep, g.tuOverZdY, fma(xPreStep, g.tuOverZdX, a.u));
            tuOverZStep = fma(xStep, g.tuOverZdX, g.tuOverZdY);

            tvOverZ = fma(yPreStep, g.tvOverZdY, fma(xPreStep, g.tvOverZdX, a.v));
            tvOverZStep = fma(xStep, g.tvOverZdX, g.tvOverZdY);
        } else {
            xStep = zStep = _1OverZStep = tuOverZStep = tvOverZStep = 0;
        }
    }

    void step() {
        y++;
        x += xStep;
        z += zStep;
        _1OverZ += _1OverZStep;
        tuOverZ += tuOverZStep;
        tvOverZ += tvOverZStep;
    }

    void stepXOnly() {
        x += xStep;
    }
}
