package ahd.usim.ulib.visualization.shapes.shape3d;

import ahd.usim.ulib.visualization.canvas.CoordinatedScreen;
import ahd.usim.ulib.jmath.datatypes.functions.Arc3D;
import ahd.usim.ulib.jmath.datatypes.tuples.Point3D;
import ahd.usim.ulib.jmath.functions.utils.Sampling;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public final class FlatSurface extends Shape3D {
    private Color color;
    private Color fixedColor;
    private boolean isFilled;
    private float thickness;

    public FlatSurface(CoordinatedScreen canvas, Color color, boolean isFilled, float thickness, Point3D... points) {
        super(canvas);
        this.points.addAll(Arrays.asList(points));
        this.thickness = thickness;
        this.fixedColor = color;
        this.isFilled = isFilled;
        this.color = color;
    }

    public FlatSurface(CoordinatedScreen canvas, Color color, Point3D... points) {
        this(canvas, color, true, 2, points);
    }

    public FlatSurface(CoordinatedScreen canvas, boolean isFilled, Color color, Point3D... points) {
        this(canvas, color, isFilled, 2, points);
    }

    public FlatSurface(CoordinatedScreen canvas, Color color, boolean isFilled, float thickness, List<Point3D> points) {
        this(canvas, color, true, 2, points.toArray(new Point3D[] {}));
    }

    public void setFixedColor(Color fixedColor) {
        this.fixedColor = fixedColor;
    }

    public Color getFixedColor() {
        return fixedColor;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlatSurface flatSurface)) return false;
        return Objects.equals(getPoints(), flatSurface.getPoints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPoints());
    }

    @Override
    public void render(@NotNull Graphics2D g2d) {
        Polygon poly = new Polygon();
        for (var p : points) {
            var pp = cs.screen(Point3D.rotateImmutably(p, cs.camera().getAngles())); //AHD:: CRITICAL-CHANGE
            poly.addPoint(pp.x, pp.y);
        }
        g2d.setColor(color);
        if (isFilled) {
            g2d.fillPolygon(poly);
        } else {
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawPolygon(poly);
        }
        super.render(g2d);
    }

    public static FlatSurface flatSurface(CoordinatedScreen canvas, Color color, double l, double u, double delta, Arc3D arc) {
        final var numOfPoints = (int) ((u - l) / delta) + 1;
        var ps =  new Point3D[numOfPoints];
        var sample = Sampling.multiThreadSampling(arc, l, u, delta, 10);
        AtomicInteger counter = new AtomicInteger();
        sample.forEach(e -> ps[counter.getAndIncrement()] = e);
        return new FlatSurface(canvas, color, ps);
    }
}
