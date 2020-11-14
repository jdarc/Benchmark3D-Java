package benchmark;

import benchmark.engine.Game;
import benchmark.engine.GameLoop;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

final class Window extends Dialog implements Game, AWTEventListener {
    private final GameLoop loop;
    private final Viewer viewer;

    Window() {
        super(null, false);
        setTitle("Java Benchmark");
        setLayout(new BorderLayout());
        viewer = new Viewer();
        setBackground(viewer.getBackground());
        add(viewer, "Center");
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);
        loop = new GameLoop(this);
    }

    void start() {
        setVisible(true);
        loop.start();
    }

    void stop() {
        loop.stop();
        setVisible(false);
        dispose();
        System.exit(0);
    }

    @Override
    public void update(double seconds) {
        viewer.update(seconds);
    }

    @Override
    public void render() {
        viewer.render();
        repaint();
    }

    public void eventDispatched(AWTEvent event) {
        if (event.getID() == WindowEvent.WINDOW_CLOSING) {
            stop();
        } else if (event instanceof KeyEvent) {
            var evt = (KeyEvent) event;
            if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                stop();
            }
        }
    }
}
