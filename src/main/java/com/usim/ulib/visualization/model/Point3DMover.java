//package com.usim.ulib.visualization.model;
//
//import com.usim.ulib.jmath.datatypes.functions.Arc3D;
//import com.usim.ulib.jmath.datatypes.tuples.Point2D;
//import com.usim.ulib.jmath.datatypes.tuples.Point3D;
//import com.usim.ulib.visualization.canvas.CoordinatedCanvas;
//import com.usim.ulib.visualization.canvas.CoordinatedScreen;
//import com.usim.ulib.visualization.canvas.Graph3DCanvas;
//import com.usim.ulib.visualization.canvas.Render;
//import com.usim.ulib.visualization.shapes.shapes3d.Shape3D;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public final class Point3DMover implements Render {
//    private final Shape3D model;
//    private Arc3D velocityVector;
//    private Arc3D posVector;
//    private Arc3D accelerationVector;
//    private boolean showTrace;
//    private double timeFactor;
//    private CoordinatedScreen canvas;
//    private final ArrayList<Point3D> traceHolder;
//    private double time;
//    private double delta;
//    private boolean isPosDetermined;
//
//    public Point3DMover(Shape3D shape) {
////        this.canvas = shape.();
//        model = shape;
//        time = 0;
//        timeFactor = 1;
//        traceHolder = new ArrayList<>();
//        showTrace = true;
//        delta = 0.001;
//        velocityVector = t -> new Point3D(0, 0, 0);
//        posVector = t -> new Point3D(0, 0, 0);
//        accelerationVector = t -> new Point3D(0, 0, 0);
//        traceHolder.add(shape.getCenter());
//        isPosDetermined = true;
//    }
//
//    public double getDelta() {
//        return delta;
//    }
//
//    public void setDelta(double delta) {
//        this.delta = delta;
//    }
//
//    public Arc3D getVelocityVector() {
//        return velocityVector;
//    }
//
//    public Arc3D getPosVector() {
//        return posVector;
//    }
//
//    public void setPosVector(Arc3D posVector) {
//        this.posVector = posVector;
//        velocityVector = posVector.derivative(0.001, 1);
//        accelerationVector = velocityVector.derivative(0.001, 1);
//        isPosDetermined = true;
//    }
//
//    public void setVelocityVector(Arc3D velocityVector) {
//        this.velocityVector = velocityVector;
//        accelerationVector = velocityVector.derivative(0.01, 1);
//        var c = model.getCenter();
//        posVector = t -> new Point3D(
//                velocityVector.fx().integral(0, t, 0.01) + c.x,
//                velocityVector.fx().integral(0, t, 0.01) + c.y,
//                velocityVector.fx().integral(0, t, 0.01) + c.z
//        );
//        isPosDetermined = false;
//    }
//
//    public Arc3D getAccelerationVector() {
//        return accelerationVector;
//    }
//
//    public void setTime(double time) {
//        this.time = time;
//    }
//
//    public double getTime() {
//        return time;
//    }
//
//    public CoordinatedScreen getCanvas() {
//        return canvas;
//    }
//
//    public double getTimeFactor() {
//        return timeFactor;
//    }
//
//    public void setTimeFactor(double timeFactor) {
//        this.timeFactor = timeFactor;
//    }
//
//    public void setCanvas(CoordinatedScreen canvas) {
//        this.canvas = canvas;
//    }
//
//    public boolean isShowTrace() {
//        return showTrace;
//    }
//
//    public void setShowTrace(boolean showTrace) {
//        this.showTrace = showTrace;
//    }
//
//    public ArrayList<Point3D> getTraceHolder() {
//        return traceHolder;
//    }
//
//    public void rotateVelocityVector(Point3D center, double xAngle, double yAngle, double zAngle) {
//        velocityVector = velocityVector.rotate(center, xAngle, yAngle, zAngle);
//    }
//
//    public void rotateTraceHolder(Point3D center, double xAngle, double yAngle, double zAngle) {
//        traceHolder.forEach(e -> e.rotate(center, xAngle, yAngle, zAngle));
//    }
//
//    public void rotateMover(Point3D center, double xAngle, double yAngle, double zAngle) {
//        rotateVelocityVector(new Point3D(), xAngle, yAngle, zAngle);
//        rotateTraceHolder(center, xAngle, yAngle, zAngle);
//    }
//
//    @Override
//    public void tick() {
//        if (!canvas.isDynamic())
//            return;
//        var tChange = timeFactor / canvas.getRealFps();
//
//        var x = time;
//
//        time += tChange;
//
//        while ((x += delta) < time) {
//            if (isPosDetermined) {
//                model.setPos(posVector.valueAt(x));
//            } else {
//                var vv = velocityVector.valueAt(x);
//                var dx = vv.x * delta;
//                var dy = vv.y * delta;
//                var dz = vv.z * delta;
//                model.move(dx, dy, dz);
//            }
//
//            traceHolder.add(model.getCenter());
//        }
//
//        if (isPosDetermined) {
//            model.setPos(posVector.valueAt(time));
//        } else {
//            var vv = velocityVector.valueAt(time);
//            var dx = vv.x * delta;
//            var dy = vv.y * delta;
//            var dz = vv.z * delta;
//
//            model.move(dx, dy, dz);
//        }
//
//        traceHolder.add(model.getCenter());
//    }
//
//    @Override
//    public void render(Graphics2D g2d) {
//        if (!showTrace)
//            return;
//        g2d.setColor(Color.RED);
//        Graph3DCanvas.simplePlotter(traceHolder, canvas, g2d);
//    }
//}
