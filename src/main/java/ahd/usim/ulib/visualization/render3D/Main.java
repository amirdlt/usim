package ahd.usim.ulib.visualization.render3D;//package visualization.render3D;
//
//import animation.canvaspanel.Graph3DPanel;
//import animation.shapes.shapes3d.*;
//import guitools.MainFrame;
//import utils.Utils;
//import utils.managers.TTManager;
//
//public class Main {
//    public static void main(String[] args) {
//        MainFrame frame = new MainFrame("AHD");
//        Graph3DPanel canvas = new Graph3DPanel();
//        canvas.setShowMousePos(true);
////        canvas.setFps(88);
////        var ls = new LightSource();
////        var shader = new Shader(ls);
//        var arr = Utils.doubleArray(-5, 0, 5);
//        new Thread(() -> {
//            for (int i = 0; i < 1; i++) {
////                var s = Area.sphere(canvas, Color.RED, new Point3D(0, 0, 0), 1);
//                var s = new Area(canvas, ".\\res\\obj\\MaleLow.obj");
////                var s = Area.cylinder(canvas, Color.YELLOW);
////                var s = new PointShape3D(canvas, Point3D.random(), Color.GREEN, 0.01, true);
////                var ss = new PointShape3D(canvas, new Point3D(0, -10, 0), Color.BLUE, 0.1, true);
////                var ss = Area.cube(canvas, new Point3D(arr[i], 0, arr[arr.length - 1 - i]), 1);
////                shader.shade(s);
////                s.addTicks(() -> System.out.println(s.getCenter()));
////                s.addTicks(new Runnable() {
////                    double counter = 0.015;
////
////                    @Override
////                    public void run() {
////                        s.rotate(new Point3D(), counter, counter, counter);
////                        s.rotate(counter, counter, counter);
////                        ss.rotate(new Point3D(), counter, counter, counter);
////                        ss.rotate(counter, counter, counter);
////                    }
////                });
////                s.setTick(() -> s.rotate(new Point3D(), 0.01, 0.01, 0.01));
////                var s = new Area(canvas, Color.RED, -PI/2, PI/2, -PI, PI, 0.05, 0.05,
////                        t -> new Point2D(sin(t-cos(t*t)), cos(t*t+sin(t))));
////                var s = new Area(canvas, Color.RED, -PI, PI, -PI, PI, 0.05, 0.05,
////                        t -> new Point2D(sin(t) + 1.2, cos(t)));
////                s.setTick(() -> s.rotate(0.002, 0.002, 0.002),
////                        () -> s.scalePoints(1.0001));
////                var s = new Area(canvas, Color.BLUE, -1, 1, -1, 1, 0.1, 0.1,
////                        (x, y) -> x*y - sin(x + y) + tan(x));
////            Polyhedra s = Polyhedra.cube(canvas, Point3D.random(-10, 10, -10, 10, -10, 10), 1);
////                var s = Polyhedra.cone(canvas, Color.RED, new Point3D(0, 0, 5), 10, 5);
////                var s = Polygon3D.flatSurface(canvas, Color.BLUE, -Math.PI, Math.PI, 0.1,
////                        Arc3D.circle(new Point3D(0, 0, 0), 20));
////                            var s = new Curve3D(canvas, Color.RED, 5, -20,
////                    20, 0.001, t -> new Point3D(t, Math.sin(t), Math.cos(t)));
////                var r = 1;
////                var rp = Point3D.random(-20, 20, -20, 20, -20, 20);
////                var s = new Area(canvas, -PI / 2, PI / 2, -PI, PI, 0.1, 0.1,
////                        (x, y) -> new Point3D(r * cos(x) * cos(y) + rp.x, r * cos(x) * sin(y) + rp.y, r * sin(x) + rp.z));
////                s.setDoTick(false);
////                var s = new Area(canvas, -1, 1, -1, 1, 0.1, 0.1,
////                        (x, y) -> new Point3D(x-y+cos(y*x)-1, y*y*sin(x), x*log(x*x+1)));
////            var s = new Polygon3D(canvas, Color.RED,
////                    new Point3D(0, 0, 1), new Point3D(1, 0, 1),
////                    new Point3D(1, 1, 1), new Point3D(0, 1, 1));
////            Line3D s = new Line3D(canvas, Point3D.random(-5, 5, -5, 5, -5, 5),
////                    Point3D.random(-5, 5, -5, 5, -5, 5), IconFactory.randomColor());
////            s.rotate(PI/4, PI/4, PI/4);
//
//
////                var s = new Curve3D(canvas, Color.RED, 2f, -PI*50, PI*50, 0.01, t -> new Point3D(t, sin(t*10), cos(t*10)));
////                s.setTick(() -> s.rotate(0.1, 0.01, 0.01)/*,
////                    () -> s.scalePoints(s.getCenter().z, s.getCenter().z, s.getCenter().z)*/);
////                s.scalePoints(s.getCenter().z, s.getCenter().z, s.getCenter().z);
////            for (int j = 0; j < 2; j++)
////                s.addComponents(new Line3D(canvas));
////            var s = new Polygon3D(canvas, IconFactory.randomColor(),
////                    Point3D.random(-5, 5, -5, 5, -5, 5),
////                    Point3D.random(-5, 5, -5, 5, -5, 5),
////                    Point3D.random(-5, 5, -5, 5, -5, 5));
////                canvas.addRender(s, ss);
//                canvas.addRender(s);
////                s.addTicks(
////                        () -> s.getShader().getLightSources().get(0).
////                        getPosition().set(
////                        20*Math.sin(TTManager.secondsAfterStart()),
////                        20*Math.cos(TTManager.secondsAfterStart()),
////                        20*Math.sin(TTManager.secondsAfterStart())
////                        ),
////                        () -> s.getShader().shade(s)/*,
////                        () -> s.getShader().getLightSources().get(0).setIntensity(Math.sin(TimeManager.secondsAfterStart())/2)*/);
//
////                s.addTicks(() -> s.getMover().rotateVelocityVector(new Point3D(), 0.01, 0.01, 0.01));
////                s.getMover().setVelocityVector(t -> new Point3D(t, t, t));
////                s.getMover().setShowTrace(true);
////                s.getMover().setTimeFactor(0.5);
////                s.getMover().setDelta(0.01);
////                s.setModelMoverActivation(true);
////                ss.getMover().setPosVector(s.getMover().getPosVector());
////                ss.getMover().setShowTrace(true);
////                s.addTicks(() -> s.rotate(0.01, 0.01, 0.01));
//            }
//        }).start();
//
//        frame.add(canvas);
//
//
////        frame.pack();
////        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//    }
//}
