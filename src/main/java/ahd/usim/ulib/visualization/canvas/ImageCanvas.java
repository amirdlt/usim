package ahd.usim.ulib.visualization.canvas;

import java.awt.*;

public class ImageCanvas extends Graph3DCanvas {

    public ImageCanvas(Image image) {
        addRender(g2d -> {
            var w = (int) (image.getWidth(this) * xScale / 200);
            var h = (int) (image.getHeight(this) * yScale / 200);
            g2d.drawImage(image, -shiftX, -shiftY, w, h, null);
        });
    }

}
