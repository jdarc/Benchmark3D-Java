package benchmark.engine;

final class Vertex {
    float x;
    float y;
    float z;
    float w;
    float u;
    float v;

    Vertex(int offset, float[] src) {
        x = src[offset + 0];
        y = src[offset + 1];
        z = src[offset + 2];
        u = src[offset + 3];
        v = src[offset + 4];
    }

    Vertex transform(Matrix4x4 transform) {
        var nx = Math.fma(x, transform.m00, Math.fma(y, transform.m10, Math.fma(z, transform.m20, transform.m30)));
        var ny = Math.fma(x, transform.m01, Math.fma(y, transform.m11, Math.fma(z, transform.m21, transform.m31)));
        var nz = Math.fma(x, transform.m02, Math.fma(y, transform.m12, Math.fma(z, transform.m22, transform.m32)));
        var nw = Math.fma(x, transform.m03, Math.fma(y, transform.m13, Math.fma(z, transform.m23, transform.m33)));
        this.x = nx;
        this.y = ny;
        this.z = nz;
        this.w = nw;
        return this;
    }

    void toScreen(int width, int height) {
        w = 1 / w;
        x = 0.5f * Math.fma(+x, w, 1) * width;
        y = 0.5f * Math.fma(-y, w, 1) * height;
        z = 0.5f * Math.fma(+z, w, 1);
        u *= w;
        v *= w;
    }
}
