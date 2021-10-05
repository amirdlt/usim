package ahd.ulib.visualization.fractals;//package visualization.fractals;
//
//import animation.canvaspanel.Graph2DPanel;
//import guitools.MainFrame;
//import jmath.datatypes.tuples.Point2D;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public final class FractalFactory {
//
//    public static ArrayList<Point2D> getList(Point2D start, int numOfPoints) {
//        var res = new Point2D[numOfPoints];
//        int counter = 0;
//        var begin = start.getCopy();
//        while (counter < numOfPoints) {
//            res[counter++] = start.addVector(counter % 2 == 0 ? 0.2 : 0.1, counter % 2 == 0 ? -0.1 : 0.15).getCopy();
//        }
//
//
//        return new ArrayList<>(Arrays.asList(res));
//    }
//
//
//    public static void main(String[] args) {
//        var frame = new MainFrame();
//        var g2dp = new Graph2DPanel();
//        frame.contentPanel.add(g2dp);
//        var l = getList(new Point2D(), 2000);
//        g2dp.addRender(g2d -> {
//            g2d.setColor(Color.RED);
//            Graph2DPanel.simplePlotter2D(l, g2dp, g2d);
//        });
//        SwingUtilities.invokeLater(frame);
//    }
//
//
//}
