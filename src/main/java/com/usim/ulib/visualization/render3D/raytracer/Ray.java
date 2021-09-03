package com.usim.ulib.visualization.render3D.raytracer;

import com.usim.ulib.jmath.datatypes.functions.Arc3D;
import com.usim.ulib.jmath.datatypes.tuples.Point3D;
import com.usim.ulib.swingutils.MainFrame;
import com.usim.ulib.utils.Utils;
import com.usim.ulib.visualization.canvas.Graph3DCanvas;

import javax.swing.*;
import java.awt.*;

public class Ray implements Arc3D {
    private final Point3D origin;
    private final Point3D direction;

    public Ray(Point3D origin, Point3D direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

    public Point3D getOrigin() {
        return origin;
    }

    public Point3D getDirection() {
        return direction;
    }

    @Override
    public Point3D valueAt(double t) {
        return new Point3D(origin.x + t * direction.x, origin.y + t * direction.y, origin.z + t * direction.z);
    }

    public double hitSphere(Point3D center, double radius) {
        var oc = Point3D.sub(origin, center);
        var dot = oc.dotProduct(direction);
        return dot * dot - oc.squareOfDistanceFromOrigin() + radius * radius;
    }

    public static void main(String[] args) {
        var f = new MainFrame();
        var gp = new Graph3DCanvas();
        f.add(gp);
        gp.addRender(g2d -> g2d.drawImage(Utils.createImage(gp.getWidth(), gp.getHeight(), (i, j) -> {
            var ray = new Ray(gp.camera().getPos(), Point3D.sub(gp.camera().getPos(), new Point3D(gp.coordinateX(i), gp.coordinateY(j), -2)));
            var factor = ray.hitSphere(new Point3D(0, 0, -3), 2);
            if (factor > 0)
                return new Color((float) factor % 1, (float) factor % 1, (float) factor % 1).getRGB();
            var t = 0.5f * ((float) ray.getDirection().y + 1);
            return new Color(1-0.5f*t,1-0.3f*t,1f).getRGB();
        }, 2), 0, 0, null));
        SwingUtilities.invokeLater(f);
    }
}
