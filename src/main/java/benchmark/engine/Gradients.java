package benchmark.engine;

final class Gradients {
    final float _1OverZdX;
    final float _1OverZdY;
    final float _zOverZdX;
    final float _zOverZdY;
    final float tuOverZdX;
    final float tuOverZdY;
    final float tvOverZdX;
    final float tvOverZdY;

    Gradients(Vertex a, Vertex c, Vertex b) {
        var acx = a.x - c.x;
        var bcx = b.x - c.x;
        var acy = a.y - c.y;
        var bcy = b.y - c.y;
        var oneOverDX = 1 / (bcx * acy - acx * bcy);

        var w0 = a.w - c.w;
        var w1 = b.w - c.w;
        _1OverZdX = oneOverDX * (w1 * acy - w0 * bcy);
        _1OverZdY = oneOverDX * (w0 * bcx - w1 * acx);

        var z0 = a.z - c.z;
        var z1 = b.z - c.z;
        _zOverZdX = oneOverDX * (z1 * acy - z0 * bcy);
        _zOverZdY = oneOverDX * (z0 * bcx - z1 * acx);

        var tu0 = a.u - c.u;
        var tu1 = b.u - c.u;
        tuOverZdX = oneOverDX * (tu1 * acy - tu0 * bcy);
        tuOverZdY = oneOverDX * (tu0 * bcx - tu1 * acx);

        var tv0 = a.v - c.v;
        var tv1 = b.v - c.v;
        tvOverZdX = oneOverDX * (tv1 * acy - tv0 * bcy);
        tvOverZdY = oneOverDX * (tv0 * bcx - tv1 * acx);
    }
}
