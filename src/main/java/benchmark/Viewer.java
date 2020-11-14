package benchmark;

import benchmark.engine.FpsCounter;
import benchmark.engine.Visualiser;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

final class Viewer extends Canvas {
    private static final Dimension RENDER_SIZE = new Dimension(1440, 1800);
    private static final Font FONT = new Font("Space Mono", Font.BOLD, 15);

    private final FpsCounter fpsCounter = new FpsCounter();
    private final Visualiser visualiser = new Visualiser(RENDER_SIZE.width, RENDER_SIZE.height);

    Viewer() {
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        var height = (int) (screenSize.height * 90.0 / 100.0);
        var width = (int) (height * 720.0 / 900.0);

        setSize(new Dimension(width, height));
        setBackground(new Color(0x433649));
        setIgnoreRepaint(true);
    }

    void update(double seconds) {
        var tick = System.nanoTime();
        visualiser.renderFrame(seconds);
        fpsCounter.add(System.nanoTime() - tick);
    }

    void render() {
        var bufferStrategy = getBufferStrategy();
        if (bufferStrategy == null) {
            createBufferStrategy(2);
            return;
        }

        if (bufferStrategy.contentsLost()) {
            return;
        }

        var g = (Graphics2D) bufferStrategy.getDrawGraphics();
        visualiser.draw(g, 0, 0, getWidth(), getHeight());

        g.setColor(new Color(232, 232, 20));
        g.setFont(FONT);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.drawString(String.format("Frames/Second: %.2f", fpsCounter.average()), 16, 28);
        g.drawString(Info.VERSION, getWidth() - 16 - g.getFontMetrics(FONT).stringWidth(Info.VERSION), 28);
        g.drawImage(Info.LOGO, null, getWidth() - Info.LOGO.getWidth() - 8, getHeight() - Info.LOGO.getHeight() - 8);
        g.dispose();

        bufferStrategy.show();
    }
}
