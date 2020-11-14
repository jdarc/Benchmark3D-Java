package benchmark.engine;

final class Vector3 {
    final static Vector3 ZERO = new Vector3(0, 0, 0);
    final static Vector3 UP = new Vector3(0, 1, 0);

    final float x;
    final float y;
    final float z;

    Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Vector3(double x, double y, double z) {
        this((float) x, (float) y, (float) z);
    }

    Vector3 minus(Vector3 v) {
        return new Vector3(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    static float dot(Vector3 v1, Vector3 v2) {
        return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z * v2.z);
    }

    static Vector3 cross(Vector3 v1, Vector3 v2) {
        return new Vector3((v1.y * v2.z) - (v1.z * v2.y), (v1.z * v2.x) - (v1.x * v2.z), (v1.x * v2.y) - (v1.y * v2.x));
    }

    static Vector3 normalize(Vector3 v) {
        var length = (float) (1.0 / Math.sqrt((v.x * v.x) + (v.y * v.y) + (v.z * v.z)));
        return new Vector3(v.x * length, v.y * length, v.z * length);
    }
}
