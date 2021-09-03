//package com.usim.ulib.visualization.render3D.camera;
//
//import animation.canvaspanel.CoordinatedCanvasPanel;
//import animation.canvaspanel.Render;
//import animation.shapes.shapes3d.Shape3D;
//import com.usim.ulib.jmath.datatypes.tuples.Point3D;
//import com.usim.ulib.utils.managers.KeyManager;
//
//import static java.awt.event.KeyEvent.*;
//import static com.usim.ulib.utils.managers.KeyManager.*;
//
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class Camera3D implements Render {
//    private final CoordinatedCanvasPanel canvas;
//    public final Point3D position;
//    private final KeyManager keyManager;
//    private Point3D speedVector;
//    private double forwardRange;
//
//    public Camera3D(CoordinatedCanvasPanel canvas, Point3D position) {
//        this.canvas = canvas;
//        this.position = position;
//        canvas.addRender(0, this);
//        keyManager = new KeyManager(canvas);
//        speedVector = new Point3D(1, 10, 0.01);
//        forwardRange = 50;
//    }
//
//    public Camera3D(CoordinatedCanvasPanel canvas) {
//        this(canvas, new Point3D());
//    }
//
//    @Override
//    public void render(Graphics2D g2d) {
//
//    }
//
//    @Override
//    public void tick() {
//        canvas.requestFocusInWindow();
//        List<Shape3D> shapes = new ArrayList<>();
//        canvas.getRenderManager().getRenderList().forEach(e -> {
//            if (e instanceof Shape3D)
//                shapes.add((Shape3D) e);
//        });
//        if (isKeyPressed(VK_A, VK_Q, VK_S, VK_D, VK_W, VK_E)) {
//            var distances = new ArrayList<Double>();
//            var scales = new ArrayList<Double>();
//            shapes.forEach(r -> distances.add(position.distanceFrom(r.getCenter())));
//            position.addVector(
//                    isKeyPressed(VK_A) ? -speedVector.x : isKeyPressed(VK_D) ? speedVector.x : 0,
//                    isKeyPressed(VK_Q) ? -speedVector.y : isKeyPressed(VK_E) ? speedVector.y : 0,
//                    isKeyPressed(VK_W) ? -speedVector.z : isKeyPressed(VK_S) ? speedVector.z : 0);
//            AtomicInteger counter = new AtomicInteger();
//            distances.forEach(d -> scales.add(
//                    1 - d / forwardRange + position.distanceFrom(shapes.get(counter.getAndIncrement()).getCenter()) / forwardRange));
//            counter.set(0);
//            shapes.forEach(r -> {
//                var scale = scales.get(counter.get());
//                r.setVisible(scale > 0 && distances.get(counter.getAndIncrement()) > 0.01);
//                if (scale > 0)
//                    canvas.setXYScale(canvas.getXScale() * scale, canvas.getXScale() * scale);
//                System.out.println(scale + "      " + distances.get(counter.get() - 1));
//                System.out.println(r.getCenter());
//                System.out.println("pos:" + position);
//            });
//        }
//    }
//
//
//}
