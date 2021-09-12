package ahd.usim.ulib.visualization.animatedmodels;

import ahd.usim.ulib.visualization.canvas.CoordinatedScreen;
import ahd.usim.ulib.visualization.canvas.Render;
import ahd.usim.ulib.jmath.datatypes.tuples.Point2D;

import java.awt.*;

public class VectorField2D implements Render {
    private boolean isVisible;
    private double width;
    private double height;
    private double vectorLen;
    private final Point2D direction;
    private final CoordinatedScreen cs;

    public VectorField2D(CoordinatedScreen cs, double width, double height) {
        this.height = height;
        this.width = width;
        this.cs = cs;
        direction = new Point2D();
        vectorLen = 0.1;
        isVisible = true;
    }

    public CoordinatedScreen getCs() {
        return cs;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getVectorLen() {
        return vectorLen;
    }

    public void setVectorLen(double vectorLen) {
        this.vectorLen = vectorLen;
    }

    @Override
    public void render(Graphics2D g2d) {
        for (double x = vectorLen/2; x + vectorLen/2 <= width; x += vectorLen * 1.1)
            for (double y = vectorLen/2; y + vectorLen/2 <= height; y += vectorLen * 1.1) {
                g2d.setColor(new Point2D(x, y).distanceFrom(direction) < vectorLen*5 ? Color.GREEN : Color.RED);
                var angel = Math.atan2(y - direction.y, x - direction.x);
                var x1 = x - vectorLen * Math.cos(angel) / 2;
                var y1 = y - vectorLen * Math.sin(angel) / 2;
                var x2 = x + vectorLen * Math.cos(angel) / 2;
                var y2 = y + vectorLen * Math.sin(angel) / 2;
                g2d.drawLine(cs.screenX(x1),
                        cs.screenY(y1), cs.screenX(x2), cs.screenY(y2));
            }
    }

    private double t = 0;
    private int sign = 1;
    @Override
    public void tick() {
        if (t > width || t < 0)
            sign *= -1;
        direction.set(t += sign * 0.01, Math.sin(t) * 6 + 6);
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }
}
