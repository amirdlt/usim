package ahd.usim.engine.gui.swing;

import ahd.ulib.swingutils.ElementBasedPanel;
import ahd.usim.engine.internal.Engine;
import ahd.usim.engine.internal.Window;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GlfwPanel extends ElementBasedPanel {

    private final Window window;
    private final java.awt.Window owner;

    public GlfwPanel(java.awt.Window owner) {
        window = Engine.getEngine().getWindow();
        this.owner = owner;
        owner.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                repaint();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                repaint();
            }
        });
        setFocusable(false);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        window.setBounds(owner.getX() + x, owner.getY() + y, width, height);
        window.requestFocus();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        window.setBounds(owner.getX() + getX(), owner.getY() + getY() + 150, getWidth(), getHeight());
        
    }
}
