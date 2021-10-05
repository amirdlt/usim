package ahd.ulib.visualization.shapes.shape3d;

import ahd.ulib.jmath.datatypes.functions.Arc3D;
import ahd.ulib.jmath.datatypes.tuples.Point3D;
import ahd.ulib.jmath.functions.utils.Sampling;
import ahd.ulib.utils.Utils;
import ahd.ulib.visualization.canvas.CoordinatedScreen;
import ahd.ulib.visualization.canvas.Graph3DCanvas;

import java.awt.*;

@SuppressWarnings("SuspiciousNameCombination")
public final class Curve3D extends Shape3D {
    private Color color;
    private float thickness;
    private final Point3D xBound;
    private final Arc3D[] arcs;

    public Curve3D(CoordinatedScreen canvas, Color color, float thickness, double l, double u, double delta, Arc3D... arcs) {
        super(canvas);
        this.thickness = thickness;
        this.color = color;
        var domain = Sampling.sample(l, u, delta);
        for (var a : arcs)
            domain.forEach(t -> points.add(a.valueAt(t)));
        xBound = new Point3D(l, u, delta);
        this.arcs = arcs;
    }

    public Curve3D(CoordinatedScreen canvas, double l, double u, double delta, Arc3D... arcs) {
        this(canvas, Utils.randomColor(), 1f, l, u, delta, arcs);
    }

    public double getLowBoundX() {
        return xBound.x;
    }

    public double getUpBoundX() {
        return xBound.y;
    }

    public double getDeltaX() {
        return xBound.z;
    }

    public void setLowBoundX(double xL) {
        xBound.x = xL;
        reset();
    }

    public void setUpBoundX(double xU) {
        xBound.y = xU;
        reset();
    }

    public void setDeltaX(double deltaX) {
        xBound.z = deltaX;
        reset();
    }

    private void reset() {
        points.clear();
        components.clear();
        points.addAll(new Curve3D(cs, getLowBoundX(), getUpBoundX(), getDeltaX(), arcs).getPoints());
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    @Override
    public boolean inViewPort() {
        return true;
    }

    @Override
    public void render(Graphics2D g2d) { //AHD:: CRITICAL-CHANGE
        g2d.setStroke(new BasicStroke(thickness));
        g2d.setColor(color);
        Graph3DCanvas
                .simplePlotter(points.stream().map(e -> Point3D.rotateImmutably(e, cs.camera().getAngles())).toList(),
                        cs, g2d);
        super.render(g2d);
    }
}
