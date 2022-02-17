package ahd.usim.physics;

import ahd.ulib.jmath.datatypes.tuples.Point3D;
import ahd.ulib.swingutils.MainFrame;
import ahd.ulib.utils.Utils;
import ahd.ulib.visualization.canvas.Graph3DCanvas;
import org.apache.regexp.RE;

import javax.swing.*;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Main {
    static double x = .0;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MainFrame() {{
            add(new Graph3DCanvas() {{
                var reference = new SimpleReference();
                reference.addParticle(new Point3D(), new Point3D(), 10e11);
                var i = 0;
//                while (i++ < 10)
//                    reference.addParticle(Point3D.random(-10, 10, -10, 10, -10, 10), new Point3D(10, 10, 10), 100);
                reference.addParticle(new Point3D(1, 1, 0), new Point3D(10, 10, 0), 100);
                addRender(g -> {
                    g.setColor(Color.RED);
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    reference.getParticles().forEach((k, v) -> {
                        var p = screen(v.position.getCopy().rotate(camera.getAngles().x, camera.getAngles().y, camera.getAngles().z));
                        g.drawString(v.position.toString(), p.x, p.y);
//                        g.drawString(v.velocity.toString(), p.x, p.y - 20);
                        g.fillOval(p.x, p.y, 5, 5);
                        v.forces.values().forEach(f -> {
                            var _pp = f.getCopy().normalize().asPoint2D();
                            var pp = new Point((int) _pp.x, (int) _pp.y);
                            g.drawLine(p.x, p.y, pp.x + p.x, pp.y + p.y);
                            System.out.println(v.id + " > " + f + "---" + pp);
                        });
                        if (k.equals("r0.p0"))
                            g.drawString("Massive", p.x, p.y);
                    });
                });
                Utils.unsafeExecutor.scheduleAtFixedRate(this::repaint, 1, 1, TimeUnit.MILLISECONDS);
                Utils.unsafeExecutor.scheduleAtFixedRate(reference::updatePositions, 50, 50, TimeUnit.MILLISECONDS);
            }});
        }});
    }
}
