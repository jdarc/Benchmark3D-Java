package benchmark.engine;

import java.util.Arrays;

import static java.lang.Math.fma;
import static java.lang.Math.max;
import static java.lang.Math.min;

final class Rasteriser {
    final int width;
    final int height;

    Matrix4x4 world = Matrix4x4.IDENTITY;
    Matrix4x4 view = Matrix4x4.IDENTITY;
    Matrix4x4 projection = Matrix4x4.IDENTITY;

    private final int[] colorBuffer;
    private final float[] depthBuffer;

    Rasteriser(int width, int height, int[] colorBuffer) {
        this.width = width;
        this.height = height;
        this.colorBuffer = colorBuffer;
        depthBuffer = new float[this.width * this.height];
    }

    void clear(int color) {
        Arrays.fill(colorBuffer, color);
        Arrays.fill(depthBuffer, 1);
    }

    void draw(Material material, float[] buffer) {
        var transform = world.times(view).times(projection);
        Parallel.partition(buffer.length / 15, (from, to) -> {
            for (var p = from * 15; p < to * 15; p += 15) {
                var p0 = new Vertex(p + 0x0, buffer).transform(transform);
                var p1 = new Vertex(p + 0x5, buffer).transform(transform);
                var p2 = new Vertex(p + 0xa, buffer).transform(transform);
                if (backFacing(p0, p1, p2)) {
                    continue;
                }

                p0.toScreen(width, height);
                p1.toScreen(width, height);
                p2.toScreen(width, height);
                if (max(p0.x, max(p1.x, p2.x)) < 0 || min(p0.x, min(p1.x, p2.x)) >= width) {
                    continue;
                }

                Vertex a;
                Vertex b;
                Vertex c;
                var leftIsMiddle = true;
                if (p0.y < p1.y) {
                    if (p2.y < p0.y) {
                        a = p2;
                        b = p0;
                        c = p1;
                    } else if (p1.y < p2.y) {
                        a = p0;
                        b = p1;
                        c = p2;
                    } else {
                        a = p0;
                        b = p2;
                        c = p1;
                        leftIsMiddle = false;
                    }
                } else {
                    if (p2.y < p1.y) {
                        a = p2;
                        b = p1;
                        c = p0;
                        leftIsMiddle = false;
                    } else if (p0.y < p2.y) {
                        a = p1;
                        b = p0;
                        c = p2;
                        leftIsMiddle = false;
                    } else {
                        a = p1;
                        b = p2;
                        c = p0;
                    }
                }

                if (c.y >= 0 && a.y < height) {
                    var g = new Gradients(p0, p1, p2);
                    var ttb = new Edge(g, a, c);
                    if (ttb.height > 0) {
                        var ttm = new Edge(g, a, b);
                        if (ttm.height > 0 && ttm.y < height) {
                            if (leftIsMiddle) {
                                scanConvert(material, g, ttm, ttb, ttm.height);
                            } else {
                                scanConvert(material, g, ttb, ttm, ttm.height);
                            }
                        }
                        var mtb = new Edge(g, b, c);
                        if (mtb.height > 0 && mtb.y < height) {
                            if (leftIsMiddle) {
                                scanConvert(material, g, mtb, ttb, mtb.height);
                            } else {
                                scanConvert(material, g, ttb, mtb, mtb.height);
                            }
                        }
                    }
                }
            }
        });
    }

    private void scanConvert(Material material, Gradients gradients, Edge left, Edge right, int total) {
        for (var y = 0; y < total; ++y) {
            var offset = left.y * width;

            var x1 = (int) Math.max(0, Math.ceil(left.x));
            var x2 = (int) Math.min(width, Math.ceil(right.x));

            var preStepX = x1 - left.x;
            var z = fma(preStepX, gradients._zOverZdX, left.z);
            var _1OverZ = fma(preStepX, gradients._1OverZdX, left._1OverZ);
            var tuOverZ = fma(preStepX, gradients.tuOverZdX, left.tuOverZ);
            var tvOverZ = fma(preStepX, gradients.tvOverZdX, left.tvOverZ);

            rasterise(material, gradients, offset + x1, offset + x2, z, _1OverZ, tuOverZ, tvOverZ);

            left.step();
            right.stepXOnly();
        }
    }

    private void rasterise(Material mat, Gradients grad, int x1, int x2, float z, float _1OverZ, float tuOverZ, float tvOverZ) {
        for (var mem = x1; mem < x2; ++mem) {
            if (z < depthBuffer[mem]) {
                depthBuffer[mem] = z;
                colorBuffer[mem] = mat.sample(tuOverZ / _1OverZ, tvOverZ / _1OverZ);
            }

            z += grad._zOverZdX;
            _1OverZ += grad._1OverZdX;
            tuOverZ += grad.tuOverZdX;
            tvOverZ += grad.tvOverZdX;
        }
    }

    private static boolean backFacing(Vertex a, Vertex b, Vertex c) {
        var bvw = 1 / b.w;
        var cvw = 1 / c.w;
        var avy = a.y / a.w;
        var avx = a.x / a.w;
        return (avy - b.y * bvw) * (c.x * cvw - avx) <= (avy - c.y * cvw) * (b.x * bvw - avx);
    }
}
