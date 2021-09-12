package ahd.usim.ulib.visualization.model;

import ahd.usim.ulib.jmath.datatypes.tuples.Point3D;

import java.awt.*;
import java.util.List;

public class Model3D implements Model<Point3D> {
    private List<Point3D> vertexes;

    public Model3D() {

    }

    @Override
    public List<Point3D> getVertexes() {
        return null;
    }

    @Override
    public void render(Graphics2D g2d) {

    }
}
