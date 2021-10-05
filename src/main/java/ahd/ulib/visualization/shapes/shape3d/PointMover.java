package ahd.ulib.visualization.shapes.shape3d;

import ahd.ulib.jmath.datatypes.functions.Arc3D;
import ahd.ulib.jmath.datatypes.tuples.Point3D;
import ahd.ulib.swingutils.MainFrame;
import ahd.ulib.visualization.canvas.CoordinatedScreen;
import ahd.ulib.visualization.canvas.Graph3DCanvas;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

@SuppressWarnings("unused")
public class PointMover extends Shape3D {
    private final CoordinatedScreen cs;

    private boolean showHead;
    private boolean showPath;
    private double start;
    private BasicStroke pathStroke;
    private double delta;
    private int pointRadius;

    private Arc3D positionFunction;
    private Color pathColor;
    private Color pointColor;

    public PointMover(CoordinatedScreen cs, double start) {
        this.cs = cs;
        this.start = start;
        pathColor = Color.RED;
        pointColor = Color.GREEN;
        positionFunction = t -> new Point3D(t, sin(t), cos(t));
        points.add(positionFunction.valueAt(start));
        delta = 0.05;
        showPath = true;
        showHead = true;
        pathStroke = new BasicStroke(1f);
        pointRadius = 2;
    }

    public PointMover(CoordinatedScreen cs) {
        this(cs, 0);
    }

    public void move() {
        points.add(positionFunction.valueAt(start += delta));
    }

    public boolean isShowHead() {
        return showHead;
    }

    public void setShowHead(boolean showHead) {
        this.showHead = showHead;
    }

    public boolean isShowPath() {
        return showPath;
    }

    public void setShowPath(boolean showPath) {
        this.showPath = showPath;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public BasicStroke getPathStroke() {
        return pathStroke;
    }

    public void setPathStroke(BasicStroke pathStroke) {
        this.pathStroke = pathStroke;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public int getPointRadius() {
        return pointRadius;
    }

    public void setPointRadius(int pointRadius) {
        this.pointRadius = pointRadius;
    }

    public Arc3D getPositionFunction() {
        return positionFunction;
    }

    public void setPositionFunction(Arc3D positionFunction) {
        this.positionFunction = positionFunction;
    }

    public Color getPathColor() {
        return pathColor;
    }

    public void setPathColor(Color pathColor) {
        this.pathColor = pathColor;
    }

    public Color getPointColor() {
        return pointColor;
    }

    public void setPointColor(Color pointColor) {
        this.pointColor = pointColor;
    }

    @Override
    public void render(@NotNull Graphics2D g2d) {
        if (!showPath && !showHead)
            return;
        var oldColor = g2d.getColor();
        var oldRenderingHints = g2d.getRenderingHints();
        var angles = cs.camera().getAngles();
        var head = Point3D.rotateImmutably(points.get(points.size() - 1), angles);
        if (showHead) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(pointColor);
            g2d.fillOval(cs.screenX(head.x) - pointRadius, cs.screenY(head.y) - pointRadius, pointRadius * 2,
                    pointRadius * 2);
        }
        if (!showPath) {
            g2d.setColor(oldColor);
            g2d.setRenderingHints(oldRenderingHints);
            return;
        }
        var oldStroke = g2d.getStroke();
        g2d.setStroke(pathStroke);
        g2d.setColor(pathColor);
        Graph3DCanvas
                .simplePlotter(points.stream().map(e -> Point3D.rotateImmutably(e, angles)).toList(),
                        cs, g2d);
        g2d.setStroke(oldStroke);
        g2d.setColor(oldColor);
    }

    @Override
    public boolean inViewPort() {
        return true;
    }

    public static void main(String[] args) {
        var f = new MainFrame();
        var gp = new Graph3DCanvas();
        var mover = new PointMover(gp);

        f.add(gp);

        gp.addRender(mover);

        SwingUtilities.invokeLater(f);

        new Timer(20, e -> {
            mover.move();
            gp.repaint();
        }).start();
    }
}
