package ahd.usim.ulib.visualization.render3D.raytracer;

import ahd.usim.ulib.swingutils.MainFrame;
import ahd.usim.ulib.utils.Utils;
import ahd.usim.ulib.visualization.canvas.Graph3DCanvas;

import javax.swing.*;
import java.awt.*;

public final class ColorUtil {

    public static int gradiantColor(int i, int j, int nx, int ny) {
        return new Color(
                Math.abs((float) (i % nx)) / nx,
                Math.abs((float) (j % ny)) / ny,
                (float) Math.abs(nx * ny - i * j) / nx / ny,
                0.5f * (float) Math.abs(i * j) / nx / ny + 0.5f
        ).getRGB();
    }

    public static int gradiantColor(float i, float j) {
        return new Color(
                Math.abs(i % 1),
                Math.abs(j % 1),
                0.2f/*0.75f*Math.abs(i*j)+0.25f*/
//                0.18f*(i+j)+0.5f
        ).getRGB();
    }

    public static int gradiantArcTangentColor(float i, float j) {
        return new Color(
                (float) Math.abs(1/Math.PI*(Math.PI/2+Math.atan(i))),
                (float) Math.abs(1/Math.PI*(Math.PI/2+Math.atan(j))),
                (float) Math.abs(1/Math.PI*(Math.PI/2+Math.atan(i+j))),
                0.5f * Math.abs(i * j % 1) + 0.5f
        ).getRGB();
    }

    public static void main(String[] args) {
        var frame = new MainFrame();
        var gp = new Graph3DCanvas();
        gp.addRender(g2d -> g2d.drawImage(Utils.createImage(gp.getWidth(), gp.getHeight(), i ->
                gradiantColor(i % gp.getWidth() / (float) gp.getWidth(), (float) i / gp.getWidth() / gp.getHeight()), 0), 0, 0, null));
        frame.add(gp);
        SwingUtilities.invokeLater(frame);
    }
}
