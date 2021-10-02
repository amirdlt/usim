package ahd.usim.engine.gui.swing;

import ahd.usim.ulib.swingutils.ElementBasedPanel;
import org.lwjgl.opengl.GL46C;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.util.Arrays;

public class GLPanel extends ElementBasedPanel {
    private int width;
    private int height;
    private BufferedImage context;
    private float[] colors;
    private PanelResizeListener panelResizeListener;

    public GLPanel() {
        width = height = -1;
        context = null;
        colors = null;
        panelResizeListener = (width, height) -> GL46C.glViewport(0, 0, width, height);
    }

    private void rebuildImage(int width, int height) {
        if (width == this.width && height == this.height)
            return;
        if (panelResizeListener != null)
            panelResizeListener.resized(this.width = width, this.height = height);
        var colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), true, false, Transparency.TRANSLUCENT,
                DataBuffer.TYPE_FLOAT);
        var buffer = new DataBufferFloat(width * height * 4);
        context = new BufferedImage(colorModel, Raster.createWritableRaster(
                new PixelInterleavedSampleModel(DataBuffer.TYPE_FLOAT, width, height, 4, width * 4, new int[] { 0, 1, 2, 3 }),
                buffer, null), colorModel.isAlphaPremultiplied(), null);
        colors = buffer.getData();
    }

    public void update() {
        var width = getWidth();
        var height = getHeight();
        if (width <= 0 || height <= 0)
            return;
        rebuildImage(width, height);
        GL46C.glReadPixels(0, 0, width, height, GL46C.GL_RGBA, GL46C.GL_FLOAT, colors);
        repaint();
    }

    public void clear() {
        Arrays.fill(colors, 0);
    }

    public PanelResizeListener getPanelResizeListener() {
        return panelResizeListener;
    }

    public void setPanelResizeListener(PanelResizeListener panelResizeListener) {
        this.panelResizeListener = panelResizeListener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(context, 0, 0, null);
    }

    @FunctionalInterface
    public interface PanelResizeListener {
        void resized(int width, int height);
    }
}
