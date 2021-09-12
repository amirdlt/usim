package ahd.usim.ulib.visualization.model;

import ahd.usim.ulib.visualization.canvas.CoordinatedScreen;
import ahd.usim.ulib.jmath.datatypes.tuples.Point3D;
import ahd.usim.ulib.jmath.datatypes.tuples.Point4D;
import ahd.usim.ulib.visualization.shapes.shape3d.FlatSurface;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class OBJHandler {
    @SafeVarargs
    public static List<FlatSurface> getSurfaces(String absPath, CoordinatedScreen canvas, List<Point3D>... pointsS) {
        List<Point3D> vertexes = new ArrayList<>();
        List<Point4D> faces = new ArrayList<>();
        try (Scanner s = new Scanner(new File(absPath))) {
            while (s.hasNext()) {
                var n = s.next();
                if (n.equals("v")) {
                    vertexes.add(new Point3D(Double.parseDouble(s.next()),
                            Double.parseDouble(s.next()),
                            Double.parseDouble(s.next())));
                } else if (n.equals("f")) {
                    var ss = s.nextLine().split(" ");
                    faces.add(new Point4D(Integer.parseInt(ss[1].split("/")[0]),
                            Integer.parseInt(ss[2].split("/")[0]),
                            Integer.parseInt(ss[3].split("/")[0]),
                            ss.length == 5 ? Integer.parseInt(ss[4].split("/")[0]) : 0));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<FlatSurface> res = new ArrayList<>(faces.size());
        faces.forEach(e -> {
            while (e.x < 0)
                e.x += vertexes.size() + 1;
            while (e.y < 0)
                e.y += vertexes.size() + 1;
            while (e.z < 0)
                e.z += vertexes.size() + 1;
            while (e.w < 0)
                e.w += vertexes.size() + 1;
            if (e.w == 0) {
                res.add(new FlatSurface(canvas, Color.GRAY,
                        vertexes.get((int) e.x - 1), vertexes.get((int) e.y - 1), vertexes.get((int) e.z - 1)));
            } else {
                res.add(new FlatSurface(canvas, Color.GRAY,
                        vertexes.get((int) e.x - 1), vertexes.get((int) e.y - 1), vertexes.get((int) e.z - 1), vertexes.get((int) e.w - 1)));
            }
        });

        if (pointsS.length != 0)
            for (var ps : pointsS)
                ps.addAll(vertexes);
        return res;
    }

    public static void main(String[] args) {

    }
}
