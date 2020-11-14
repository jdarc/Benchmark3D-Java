package benchmark.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

final class Importer {
    private static final String NEW_MATERIAL_DIRECTIVE = "newmtl ";
    private static final String USE_MATERIAL_DIRECTIVE = "usemtl ";
    private static final String VERTEX_DIRECTIVE = "v ";
    private static final String TEXTURE_COORDINATE_DIRECTIVE = "vt ";
    private static final String FACE_DIRECTIVE = "f ";

    private final List<Vector3> vertices = new ArrayList<>();
    private final List<Vector2> uvs = new ArrayList<>();
    private final HashMap<String, Material> materials = new HashMap<>();
    private final HashMap<String, List<Float>> triangleBuckets = new HashMap<>();
    private List<Float> currentBucket;

    Model read(String path) throws IOException {
        for (var line : new String(unZip(path)).split("\n")) {
            if (line.startsWith(NEW_MATERIAL_DIRECTIVE)) {
                var parts = line.substring(NEW_MATERIAL_DIRECTIVE.length()).split(" ");
                materials.put(parts[0], Material.create(ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(parts[1])))));
                triangleBuckets.put(parts[0], new ArrayList<>(65536));
            } else if (line.startsWith(VERTEX_DIRECTIVE)) {
                var data = Arrays.stream(line.substring(VERTEX_DIRECTIVE.length()).split(" ")).mapToDouble(Double::parseDouble).toArray();
                vertices.add(new Vector3(data[0], data[1], data[2]));
            } else if (line.startsWith(TEXTURE_COORDINATE_DIRECTIVE)) {
                var directive = line.substring(TEXTURE_COORDINATE_DIRECTIVE.length()).split(" ");
                var data = Arrays.stream(directive).mapToDouble(Double::parseDouble).toArray();
                uvs.add(new Vector2(data[0], 1.0 - data[1]));
            } else if (line.startsWith(FACE_DIRECTIVE)) {
                var indices = Arrays.stream(line.substring(FACE_DIRECTIVE.length()).replace(" ", "/").split("/"))
                                    .mapToInt(i -> Integer.parseInt(i) - 1).toArray();
                var va = vertices.get(indices[0]);
                var vb = vertices.get(indices[2]);
                var vc = vertices.get(indices[4]);
                var ta = uvs.get(indices[1]);
                var tb = uvs.get(indices[3]);
                var tc = uvs.get(indices[5]);
                var packed = Arrays.asList(va.x, va.y, va.z, ta.x, ta.y, vb.x, vb.y, vb.z, tb.x, tb.y, vc.x, vc.y, vc.z, tc.x, tc.y);
                currentBucket.addAll(packed);
            } else if (line.startsWith(USE_MATERIAL_DIRECTIVE)) {
                currentBucket = triangleBuckets.get(line.substring(USE_MATERIAL_DIRECTIVE.length()));
            }
        }

        var thing = new HashMap<Material, float[]>();
        triangleBuckets.forEach((name, data) -> thing.put(materials.get(name), toArray(data)));
        return new Model(thing);

    }

    private float[] toArray(List<Float> elements) {
        var array = new float[elements.size()];
        for (var i = 0; i < array.length; ++i) {
            array[i] = elements.get(i);
        }
        return array;
    }

    private static byte[] unZip(String path) {
        var data = new ByteArrayOutputStream();
        try {
            var fis = Objects.requireNonNull(Importer.class.getClassLoader().getResource(path)).openStream();
            var zis = new ZipInputStream(fis);
            var ze = zis.getNextEntry();
            var buffer = new byte[8192];
            while (ze != null) {
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    data.write(buffer, 0, len);
                }
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data.toByteArray();
    }
}
