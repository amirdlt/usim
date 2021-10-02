package ahd.usim.ulib.swingutils;

import ahd.usim.ulib.utils.Utils;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL46C;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Arrays;

public class FixedImagePanel extends ElementBasedPanel {
    private BufferedImage image;
    private int width;
    private int height;
    private boolean scaled;
    private int[] colors;
    private ImageUpdater updater;
    private PanelSizeListener panelSizeListener;

    public FixedImagePanel() {
        this(null);
    }

    public FixedImagePanel(ImageUpdater updater) {
        width = height = -1;
        scaled = true;
        this.updater = updater;
        panelSizeListener = null;
    }

    public synchronized void update() {
        if (updater == null)
            return;
        var width = getWidth();
        var height = getHeight();
        if (width <= 0 || height <= 0)
            return;
        rebuildImage(width, height);
        if (colors == null)
            return;
        updater.update(width, height, colors);
        repaint();
    }

    private void rebuildImage(int width, int height) {
        if (width == this.width && height == this.height)
            return;
        if (panelSizeListener != null)
            panelSizeListener.resized(width, height);
        image = new BufferedImage(this.width = width, this.height = height, BufferedImage.TYPE_INT_ARGB);
        colors = Utils.getIntColorArrayOfImage(image);
    }

    public PanelSizeListener getPanelSizeListener() {
        return panelSizeListener;
    }

    public void setPanelSizeListener(PanelSizeListener panelSizeListener) {
        this.panelSizeListener = panelSizeListener;
    }

    public boolean isScaled() {
        return scaled;
    }

    public void setScaled(boolean scaled) {
        this.scaled = scaled;
    }

    public ImageUpdater getUpdater() {
        return updater;
    }

    public void setUpdater(ImageUpdater updater) {
        this.updater = updater;
    }

    public void clear() {
        Arrays.fill(colors, 0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (scaled) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.drawImage(image, 0, 0, null);
        }
    }

    @FunctionalInterface
    public interface ImageUpdater {
        void update(int width, int height, int[] colors);
    }

    @FunctionalInterface
    public interface PanelSizeListener {
        void resized(int width, int height);
    }

    public static void main(String[] args) {
        var f = new MainFrame();

        var fp = (FixedImagePanel)

        f.add(new FixedImagePanel((width, height, colors) -> {
            GL46C.glReadPixels(0, 0, width, height, GL11C.GL_UNSIGNED_INT, GL11C.GL_RGBA, colors);
        }) {{
            setPanelSizeListener((width, height) -> GL46C.glViewport(0, 0, width, height));
        }});

        SwingUtilities.invokeLater(f);

        int i = 0;
        while (i++ < 10000000) {
            var t = System.currentTimeMillis();
            fp.update();
            Utils.sleep(30);
            System.out.println((System.currentTimeMillis() - t));
        }
    }
}
