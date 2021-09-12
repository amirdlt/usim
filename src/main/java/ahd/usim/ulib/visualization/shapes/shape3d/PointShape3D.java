package ahd.usim.ulib.visualization.shapes.shape3d;

import ahd.usim.ulib.visualization.canvas.CoordinatedScreen;
import ahd.usim.ulib.jmath.datatypes.tuples.Point3D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class PointShape3D extends Shape3D {
    private Color color;
    private final Point3D pos;
    private double radius;
    private boolean isFilled;
//    private final Point3DMover mover;
    private boolean modelMoverActivated;

    @Deprecated(forRemoval = true)
    public PointShape3D(CoordinatedScreen canvas, Point3D pos, Color color, double radius, boolean isFilled) {
        super(canvas);
        points.add(pos);
        this.color = color;
        this.radius = radius;
        this.isFilled = isFilled;
        this.pos = new Point3D(pos);
        modelMoverActivated = false;
//        mover = new Point3DMover(this);
    }

    public boolean isModelMoverActivated() {
        return modelMoverActivated;
    }

    public void setModelMoverActivated(boolean modelMoverActivated) {
        this.modelMoverActivated = modelMoverActivated;
    }

    @Override
    public void rotate(Point3D center, double xAngle, double yAngle, double zAngle) {
        super.rotate(center, xAngle, yAngle, zAngle);
//        if (modelMoverActivated)
//            mover.rotateMover(center, xAngle, yAngle, zAngle);
    }

    @Override
    public void scalePoints(double xScale, double yScale, double zScale) {
        super.scalePoints(xScale, yScale, zScale);
//        if (modelMoverActivated)
//            mover.getTraceHolder().forEach(p -> {
//                p.affectOnX(x -> x * xScale);
//                p.affectOnY(x -> x * yScale);
//                p.affectOnZ(x -> x * zScale);
//            });
    }

//    public Point3DMover getMover() {
//        return mover;
//    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Point3D getPos() {
        return pos;
    }

    public void setPos(Point3D pos) {
        this.pos.set(pos);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    @Override
    public void setPos(double x, double y, double z) {
        pos.set(x, y, z);
    }

    @Override
    public void render(@NotNull Graphics2D g2d) {
        if (!isVisible || !inViewPort())
            return;
//        if (modelMoverActivated)
//            mover.render(g2d);
        g2d.setColor(color);
//        if (isFilled) {
//            g2d.fillOval(cs.screenX(pos.x - radius), cs.screenY(pos.y + radius),
//                    (int) (cs.getXScale() * 2 * radius), (int) (cs.getYScale() * 2 * radius));
//        } else {
//            g2d.drawOval(cs.screenX(pos.x - radius), cs.screenY(pos.y - radius),
//                    cs.screenX(2 * radius), cs.screenY(2 * radius));
//        }
        g2d.fillOval(cs.screenX(pos.x - radius), cs.screenY(pos.y + radius),
                        (int) (2 * radius), (int) (2 * radius));
    }

    @Override
    public void tick() {
        super.tick();
//        if (doTick && modelMoverActivated)
//            mover.tick();
    }
}
