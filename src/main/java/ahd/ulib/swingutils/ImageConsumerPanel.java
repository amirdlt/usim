package ahd.ulib.swingutils;

import ahd.ulib.utils.supplier.ImageSupplier;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ImageConsumerPanel extends ElementBasedPanel {
    private @Nullable ImageSupplier bgImageSupplier;
    private boolean scaled;
    private int xOffset;
    private int yOffset;

    public ImageConsumerPanel() {
        this(null);
    }

    public ImageConsumerPanel(@Nullable ImageSupplier bgImageSupplier) {
        this.bgImageSupplier = bgImageSupplier;
        scaled = true;
        xOffset = 0;
        yOffset = 0;
    }

    public void setBgImageSupplier(@Nullable ImageSupplier bgImageSupplier) {
        this.bgImageSupplier = bgImageSupplier;
    }

    public @Nullable ImageSupplier getBgImageSupplier() {
        return bgImageSupplier;
    }

    public boolean isScaled() {
        return scaled;
    }

    public void setScaled(boolean scaled) {
        this.scaled = scaled;
    }

    public int getxOffset() {
        return xOffset;
    }

    public void setxOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public int getyOffset() {
        return yOffset;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Image image;
        if (bgImageSupplier != null && (image = bgImageSupplier.getImage()) != null)
            if (scaled) {
                g.drawImage(image, xOffset, yOffset, getWidth() - xOffset, getHeight() - yOffset, null);
            } else {
                g.drawImage(image, xOffset, yOffset, null);
            }
    }
}
