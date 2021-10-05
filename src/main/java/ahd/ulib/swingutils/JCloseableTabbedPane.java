package ahd.ulib.swingutils;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseEvent;

public class JCloseableTabbedPane extends JTabbedPane {
    private final JLayer<JTabbedPane> closeableTabbedPane;

    public JCloseableTabbedPane(Runnable closeTabActionListener) {
        closeableTabbedPane = new JLayer<>(this, new CloseableTabbedPaneLayerUI(closeTabActionListener));
    }

    public JCloseableTabbedPane() {
        this(() -> {});
    }

    @Override
    public Component add(String title, Component component) {
        var res = super.add(title, component);
        var comp = new JLabel(title, CENTER);
        comp.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        setTabComponentAt(getTabCount() - 1, comp);
        return res;
    }

    public JLayer<JTabbedPane> get() {
        return closeableTabbedPane;
    }
}

class CloseableTabbedPaneLayerUI extends LayerUI<JTabbedPane> {
    private final JPanel p = new JPanel();
    private final Point pt = new Point(-100, -100);
    private final Runnable closeTabActionListener;
    private final JButton button = new JButton("x") {
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(16, 16);
        }
    };

    protected CloseableTabbedPaneLayerUI(Runnable closeTabActionListener) {
        super();
        this.closeTabActionListener = closeTabActionListener;
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setRolloverEnabled(false);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        if (c instanceof JLayer) {
            //noinspection unchecked
            JLayer<JTabbedPane> jlayer = (JLayer<JTabbedPane>) c;
            JTabbedPane tabPane = jlayer.getView();
            for (int i = 0; i < tabPane.getTabCount(); i++) {
                Rectangle rect = tabPane.getBoundsAt(i);
                Dimension d = button.getPreferredSize();
                int x = rect.x + rect.width - d.width - 2;
                int y = rect.y + (rect.height - d.height) / 2;
                Rectangle r = new Rectangle(x, y, d.width, d.height);
                button.setForeground(r.contains(pt) ? Color.RED : Color.WHITE);
                SwingUtilities.paintComponent(g, button, p, r);
            }
        }
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        //noinspection unchecked
        ((JLayer<JTabbedPane>) c).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void uninstallUI(JComponent c) {
        //noinspection unchecked
        ((JLayer<JTabbedPane>) c).setLayerEventMask(0);
        super.uninstallUI(c);
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends JTabbedPane> l) {
        if (e.getID() == MouseEvent.MOUSE_CLICKED) {
            pt.setLocation(e.getPoint());
            JTabbedPane tabbedPane = l.getView();
            int index = tabbedPane.indexAtLocation(pt.x, pt.y);
            if (index >= 0) {
                Rectangle rect = tabbedPane.getBoundsAt(index);
                Dimension d = button.getPreferredSize();
                int x = rect.x + rect.width - d.width - 2;
                int y = rect.y + (rect.height - d.height) / 2;
                Rectangle r = new Rectangle(x, y, d.width, d.height);
                if (r.contains(pt)) {
                    tabbedPane.removeTabAt(index);
                }
            }
            closeTabActionListener.run();
            l.getView().repaint();
        }
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer<? extends JTabbedPane> l) {
        pt.setLocation(e.getPoint());
        JTabbedPane tabbedPane = l.getView();
        int index = tabbedPane.indexAtLocation(pt.x, pt.y);
        if (index >= 0) {
            tabbedPane.repaint(tabbedPane.getBoundsAt(index));
        } else {
            tabbedPane.repaint();
        }
    }
}
