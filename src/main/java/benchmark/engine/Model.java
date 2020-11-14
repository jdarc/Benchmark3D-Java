package benchmark.engine;

import java.util.Map;

final class Model {
    private final Map<Material, float[]> parts;

    Model(Map<Material, float[]> parts) {
        this.parts = parts;
    }

    void render(Rasteriser rasteriser) {
        parts.forEach(rasteriser::draw);
    }
}
