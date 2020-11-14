package benchmark.engine;

final class Matrix4x4 {
    final static Matrix4x4 IDENTITY = new Matrix4x4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);

    final float m00;
    final float m01;
    final float m02;
    final float m03;
    final float m10;
    final float m11;
    final float m12;
    final float m13;
    final float m20;
    final float m21;
    final float m22;
    final float m23;
    final float m30;
    final float m31;
    final float m32;
    final float m33;

    Matrix4x4(double m00, double m01, double m02, double m03, double m10, double m11, double m12, double m13,
                     double m20, double m21, double m22, double m23, double m30, double m31, double m32, double m33) {
        this((float) m00, (float) m01, (float) m02, (float) m03,
             (float) m10, (float) m11, (float) m12, (float) m13,
             (float) m20, (float) m21, (float) m22, (float) m23,
             (float) m30, (float) m31, (float) m32, (float) m33);
    }

    Matrix4x4(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13,
                     float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    Matrix4x4 times(Matrix4x4 other) {
        var a = Math.fma(m00, other.m00, Math.fma(m01, other.m10, Math.fma(m02, other.m20, m03 * other.m30)));
        var b = Math.fma(m00, other.m01, Math.fma(m01, other.m11, Math.fma(m02, other.m21, m03 * other.m31)));
        var c = Math.fma(m00, other.m02, Math.fma(m01, other.m12, Math.fma(m02, other.m22, m03 * other.m32)));
        var d = Math.fma(m00, other.m03, Math.fma(m01, other.m13, Math.fma(m02, other.m23, m03 * other.m33)));
        var e = Math.fma(m10, other.m00, Math.fma(m11, other.m10, Math.fma(m12, other.m20, m13 * other.m30)));
        var f = Math.fma(m10, other.m01, Math.fma(m11, other.m11, Math.fma(m12, other.m21, m13 * other.m31)));
        var g = Math.fma(m10, other.m02, Math.fma(m11, other.m12, Math.fma(m12, other.m22, m13 * other.m32)));
        var h = Math.fma(m10, other.m03, Math.fma(m11, other.m13, Math.fma(m12, other.m23, m13 * other.m33)));
        var i = Math.fma(m20, other.m00, Math.fma(m21, other.m10, Math.fma(m22, other.m20, m23 * other.m30)));
        var j = Math.fma(m20, other.m01, Math.fma(m21, other.m11, Math.fma(m22, other.m21, m23 * other.m31)));
        var k = Math.fma(m20, other.m02, Math.fma(m21, other.m12, Math.fma(m22, other.m22, m23 * other.m32)));
        var l = Math.fma(m20, other.m03, Math.fma(m21, other.m13, Math.fma(m22, other.m23, m23 * other.m33)));
        var m = Math.fma(m30, other.m00, Math.fma(m31, other.m10, Math.fma(m32, other.m20, m33 * other.m30)));
        var n = Math.fma(m30, other.m01, Math.fma(m31, other.m11, Math.fma(m32, other.m21, m33 * other.m31)));
        var o = Math.fma(m30, other.m02, Math.fma(m31, other.m12, Math.fma(m32, other.m22, m33 * other.m32)));
        var p = Math.fma(m30, other.m03, Math.fma(m31, other.m13, Math.fma(m32, other.m23, m33 * other.m33)));
        return new Matrix4x4(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p);
    }

    static Matrix4x4 createRotation(float radians, Vector3 axis) {
        var c = Math.cos(-radians);
        var s = Math.sin(-radians);
        var t = 1.0 - c;
        var r00 = c + axis.x * axis.x * t;
        var r01 = axis.x * axis.y * t - axis.z * s;
        var r02 = axis.x * axis.z * t + axis.y * s;
        var r04 = axis.x * axis.y * t + axis.z * s;
        var r05 = c + axis.y * axis.y * t;
        var r06 = axis.y * axis.z * t - axis.x * s;
        var r08 = axis.x * axis.z * t - axis.y * s;
        var r09 = axis.y * axis.z * t + axis.x * s;
        var r10 = c + axis.z * axis.z * t;
        return new Matrix4x4(r00, r01, r02, 0, r04, r05, r06, 0, r08, r09, r10, 0, 0, 0, 0, 1);
    }

    static Matrix4x4 create(Vector3 position, Matrix4x4 rotation, Vector3 scale) {
        var m00 = rotation.m00 * scale.x;
        var m01 = rotation.m01 * scale.y;
        var m02 = rotation.m02 * scale.z;
        var m03 = rotation.m03;
        var m10 = rotation.m10 * scale.x;
        var m11 = rotation.m11 * scale.y;
        var m12 = rotation.m12 * scale.z;
        var m13 = rotation.m13;
        var m20 = rotation.m20 * scale.x;
        var m21 = rotation.m21 * scale.y;
        var m22 = rotation.m22 * scale.z;
        var m23 = rotation.m23;
        var m30 = rotation.m30 * scale.x + position.x;
        var m31 = rotation.m31 * scale.y + position.y;
        var m32 = rotation.m32 * scale.z + position.z;
        var m33 = rotation.m33;
        return new Matrix4x4(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    static Matrix4x4 createLookAt(Vector3 eye, Vector3 center, Vector3 up) {
        var zAxis = Vector3.normalize(eye.minus(center));
        var xAxis = Vector3.normalize(Vector3.cross(up, zAxis));
        var yAxis = Vector3.cross(zAxis, xAxis);
        return new Matrix4x4(xAxis.x, yAxis.x, zAxis.x, 0,
                             xAxis.y, yAxis.y, zAxis.y, 0,
                             xAxis.z, yAxis.z, zAxis.z, 0,
                             -Vector3.dot(xAxis, eye), -Vector3.dot(yAxis, eye), -Vector3.dot(zAxis, eye), 1);
    }

    static Matrix4x4 createPerspective(float fov, float aspectRatio, float nearDistance, float farDistance) {
        var yScale = 1.0 / Math.tan(fov * 0.5);
        var xScale = yScale / aspectRatio;
        var negFarRange = farDistance / (nearDistance - farDistance);
        return new Matrix4x4(xScale, 0, 0, 0, 0, yScale, 0, 0, 0, 0, negFarRange, -1, 0, 0, nearDistance * negFarRange, 0);
    }
}
