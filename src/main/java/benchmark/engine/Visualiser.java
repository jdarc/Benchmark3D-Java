package benchmark.engine;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

public class Visualiser {
    private static final int Opaque = 0xFF << 24;

    private final Rasteriser rasteriser;
    private final BufferedImage bitmap;
    private Model model;
    private double angle;
    private boolean loaded;

    public Visualiser(int width, int height) {
        bitmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        rasteriser = new Rasteriser(bitmap.getWidth(), bitmap.getHeight(), ((DataBufferInt) bitmap.getRaster().getDataBuffer()).getData());
    }

    public void renderFrame(double seconds) {
        if (seconds <= 0) {
            return;
        }
        angle += seconds;

        var aspectRatio = rasteriser.width / (float) rasteriser.height;
        rasteriser.clear(Opaque | 0x433649);
        rasteriser.view = Matrix4x4.createLookAt(new Vector3(40, 30, 60), new Vector3(0, 20, 0), Vector3.UP);
        rasteriser.projection = Matrix4x4.createPerspective((float) (Math.PI / 4.0), aspectRatio, 1, 500);
        rasteriser.world = Matrix4x4.create(Vector3.ZERO, Matrix4x4.createRotation((float) angle, Vector3.UP), new Vector3(10, 10, 10));

        if (!loaded) {
            loaded = true;
            loadModel();
        }
        if (model != null) {
            model.render(rasteriser);
        }
    }

    public void draw(Graphics2D g, int x, int y, int width, int height) {
        var sx = width / (double) bitmap.getWidth();
        var sy = height / (double) bitmap.getHeight();
        var transform = AffineTransform.getScaleInstance(sx, sy);
        g.drawImage(bitmap, new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR), x, y);
    }

    private void loadModel() {
        new Thread(() -> {
            try {
                model = new Importer().read("./Model.zip");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
