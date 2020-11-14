package benchmark;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

final class Info {
    public static final String VERSION;
    public static final BufferedImage LOGO;

    static {
        VERSION = "Runtime: " + Runtime.version().version().stream().limit(3).map(Object::toString).collect(Collectors.joining("."));
        var decoded = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        try (var input = Objects.requireNonNull(Info.class.getClassLoader().getResource("Logo.png")).openStream()) {
            decoded = ImageIO.read(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGO = decoded;
    }
}
