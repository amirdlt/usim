package ahd.ulib.visualization.shapes.shape3d;

import ahd.ulib.jmath.datatypes.functions.Arc2D;
import ahd.ulib.jmath.datatypes.functions.Function3D;
import ahd.ulib.jmath.datatypes.functions.Mapper3D;
import ahd.ulib.jmath.datatypes.functions.Surface;
import ahd.ulib.jmath.datatypes.tuples.Point2D;
import ahd.ulib.jmath.datatypes.tuples.Point3D;
import ahd.ulib.jmath.functions.utils.Sampling;
import ahd.ulib.utils.Utils;
import ahd.ulib.visualization.canvas.CoordinatedScreen;
import ahd.ulib.visualization.canvas.Graph3DCanvas;
import ahd.ulib.visualization.model.OBJHandler;
import ahd.ulib.visualization.render3D.shading.LightSource;
import ahd.ulib.visualization.render3D.shading.Shader;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;

@SuppressWarnings({ "unused", "SuspiciousNameCombination" })
public class Area extends Shape3D {
    private Color color;
    private final int numOfSides;
    private Point3D xBound;
    private Point3D yBound;
    private Surface[] surfaces;
    private Shader shader;

    public Area(CoordinatedScreen canvas, Color color, boolean isFill, float thickness,
                double xL, double xU, double yL, double yU, double deltaX, double deltaY, Surface... surfaces) {
        super(canvas);
        this.surfaces = surfaces;
        this.color = color;
        var xSample = Sampling.sample(xL, xU, deltaX);
        var ySample = Sampling.sample(yL, yU, deltaY);
        var domain = Sampling.sampleOf2DRectangularRegion(xSample, ySample);
        for (var s : surfaces)
            domain.forEach(p -> points.add(s.valueAt(p)));
        int numberOfCols = xSample.size();
        int numberOfRows = ySample.size();
        numOfSides = numberOfCols * numberOfRows;
        var maxZ = points.stream().mapToDouble(p -> p.z < 0 ? -p.z : p.z).max().orElse(1);
        maxZ = maxZ == 0 ? 1 : maxZ;
        xU = Math.max(Math.abs(xL), Math.abs(xU));
        yU = Math.max(Math.abs(yL), Math.abs(yU));
        for (int i = 0; i < points.size(); i++)
            if (
                    i + numberOfCols + 1 < points.size() &&
                    i % numberOfCols < numberOfCols - 1 &&
                    i / numberOfCols < numberOfRows - 1
            ) {
                var p = points.get(i);
                components.add(new FlatSurface(canvas,
                        new Color(
                                Utils.checkBounds(Math.abs((int) (Math.atan2(p.y, p.x) * 256)), 35, 255),
                                Utils.checkBounds(Math.abs((int) (p.x/xU * 256)), 35, 255),
                                Utils.checkBounds(Math.abs((int) (p.y/yU * 256)), 35, 255),
                                Utils.checkBounds(Math.abs((int) (p.z/maxZ * 256)), 35, 255)
                        ),
                        isFill, thickness,
                        points.get(i), points.get(i + 1), points.get(i + numberOfCols + 1), points.get(i + numberOfCols)));
            }
        shader = new Shader(
                new LightSource(new Point3D(1, 1, 1), Color.RED, 0.1)
//                new LightSource(new Point3D(-1, -1, 1), Color.GREEN, 0.1)
//                new LightSource(new Point3D(-5, -10, 5), Color.BLUE, 0.2)
        );
        shader.shade(this);
        xBound = new Point3D(xL, xU, deltaX);
        yBound = new Point3D(yL, yU, deltaY);
    }

    public Area(CoordinatedScreen cs, ColorSetter colorSetter, double xL, double xU, double yL, double yU, double deltaX, double deltaY, Surface... surfaces) {
        this(cs, Utils.randomColor(), true, 1f, xL, xU, yL, yU, deltaX, deltaY, surfaces);
        components.stream().filter(FlatSurface.class::isInstance).forEach(e -> ((FlatSurface) e).setColor(colorSetter.colorOf((FlatSurface) e)));
    }

    public Area(List<Point3D> points, FlatSurface... squares) {
        super(squares[0].getCs());
        components.addAll(Arrays.asList(squares));
        this.points.addAll(points);
        numOfSides = squares.length;
        shader = new Shader(
                new LightSource(new Point3D(1, 1, 1), Color.RED, 0.1),
                new LightSource(new Point3D(-1, -1, 1), Color.GREEN, 0.1),
                new LightSource(new Point3D(-5, -10, 5), Color.BLUE, 0.2)
        );
        shader.shade(this);
    }

    public Area(CoordinatedScreen canvas, String pathOfModel) {
        super(canvas);
        List<FlatSurface> surfaces;
        components.addAll(surfaces = OBJHandler.getSurfaces(pathOfModel, canvas, getPoints()));
        numOfSides = surfaces.size();
        shader = new Shader(
                new LightSource(new Point3D(1, 1, 1), Color.RED, 0.1)
//                new LightSource(new Point3D(-1, -1, 1), Color.GREEN, 0.1)
//                new LightSource(new Point3D(-5, -10, 5), Color.BLUE, 0.2)
        );
        shader.shade(this);
    }

    public Area(CoordinatedScreen canvas, double xL, double xU, double yL, double yU, double deltaX, double deltaY, Surface... surfaces) {
        this(canvas, Utils.randomColor(), true, 2, xL, xU, yL, yU, deltaX, deltaY, surfaces);
    }

    public Area(CoordinatedScreen canvas, Color color, double xL, double xU, double yL, double yU, double deltaX, double deltaY, Function3D... fs) {
        this(canvas, color, true, 2, xL, xU, yL, yU, deltaX, deltaY, functionsToSurfaces(fs));
    }

    public Area(CoordinatedScreen canvas, Color color, double xL, double xU, double yL, double yU, double deltaX, double deltaY, Arc2D arc) {
        this(canvas, color, true, 2, xL, xU, yL, yU, deltaX, deltaY, Surface.surfaceOfRevolution(arc));
    }

    public Area(CoordinatedScreen cs) {
        this(cs, Color.RED, true, 2f, 0, 0, 0, 0, 0, 0);
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    private static Surface[] functionsToSurfaces(Function3D... fs) {
        Surface[] surfaces = new Surface[fs.length];
        int counter = 0;
        for (var f : fs)
            surfaces[counter++] = (x, y) -> new Point3D(x, y, f.valueAt(x, y));
        return surfaces;
    }

    public Color getColor() {
        return color;
    }

    public void setFill(boolean isFill) {
        components.stream().filter(e -> e instanceof FlatSurface).forEach(e -> ((FlatSurface) e).setFilled(isFill));
    }

    private FlatSurface getFirstFlatSurface() {
        for (var c : components)
            if (c instanceof FlatSurface)
                return (FlatSurface) c;
        return null;
    }

    public void setColor(Color color) {
        this.color = color;
        components.stream().filter(e -> e instanceof FlatSurface).forEach(e -> ((FlatSurface) e).setFixedColor(color));
        shader.shade(this);
    }

    public void setThickness(float thickness) {
        components.stream().filter(e -> e instanceof FlatSurface).forEach(e -> ((FlatSurface) e).setThickness(thickness));
    }

    public float getThickness() {
        try {
            return getFirstFlatSurface().getThickness();
        } catch (NullPointerException e) {
            return 1.5f;
        }
    }

    public boolean isFilled() {
        try {
            return getFirstFlatSurface().isFilled();
        } catch (NullPointerException e) {
            return true;
        }
    }

    public double getLowBoundX() {
        return xBound.x;
    }

    public double getLowBoundY() {
        return yBound.x;
    }

    public double getUpBoundX() {
        return xBound.y;
    }

    public double getUpBoundY() {
        return yBound.y;
    }

    public double getDeltaX() {
        return xBound.z;
    }

    public double getDeltaY() {
        return yBound.z;
    }

    public void setLowBoundX(double xL) {
        xBound.x = xL;
        reset();
    }

    public void setUpBoundX(double xU) {
        xBound.y = xU;
        reset();
    }

    public void setDeltaX(double deltaX) {
        xBound.z = deltaX;
        reset();
    }

    public void setLowBoundY(double yL) {
        yBound.x = yL;
        reset();
    }

    private void reset() {
        components.clear();
        var newArea = new Area(cs, getColor(), isFilled(), getThickness(), getLowBoundX(), getUpBoundX(), getLowBoundY(), getUpBoundY(), getDeltaX(), getDeltaY(), surfaces);
        components.addAll(newArea.getComponents());
        points.clear();
        points.addAll(newArea.getPoints());
        if (cs instanceof Graph3DCanvas gp) {
            var rac = gp.getRotationAroundCenter();
            rotate(new Point3D(), rac.x, rac.y, rac.z);
        }
    }

    public void setUpBoundY(double yU) {
        yBound.y = yU;
        reset();
    }

    public void setDeltaY(double deltaY) {
        yBound.z = deltaY;
        reset();
    }

    public void colorSet(ColorSetter colorSetter) {
        components.stream().filter(FlatSurface.class::isInstance).forEach(e -> ((FlatSurface) e).setColor(colorSetter.colorOf((FlatSurface) e)));
    }

    public static Area cube(CoordinatedScreen canvas, Point3D center, double sideLen) {
        var hsl = new double[] {-sideLen * sqrt(3) / 2, sideLen * sqrt(3) / 2}; // half of digLen
        var xc = center.x;
        var yc = center.y;
        var zc = center.z;

        var ps = new Point3D[8];
        int counter = 0;
        for (var hs1 : hsl)
            for (var hs2 : hsl)
                for (var hs3 : hsl)
                    ps[counter++] = new Point3D(xc + hs1, yc + hs2, zc + hs3);

                var color = Utils.randomColor();
        return new Area(new ArrayList<>(Arrays.asList(ps)),
                new FlatSurface(canvas, color, ps[4], ps[5], ps[7], ps[6]),
                new FlatSurface(canvas, color.darker(), ps[0], ps[2], ps[6], ps[4]),
                new FlatSurface(canvas, color.brighter(), ps[1], ps[3], ps[7], ps[5]),
                new FlatSurface(canvas, color, ps[1], ps[5], ps[4], ps[0]),
                new FlatSurface(canvas, color.darker(), ps[3], ps[7], ps[6], ps[2]),
                new FlatSurface(canvas, color.brighter(), ps[1], ps[3], ps[2], ps[0])
        );
    }

    public static Area cubeBorder(CoordinatedScreen cs, Point3D center, Color color, double sideLen) {
        var hsl = new double[] {-sideLen * sqrt(3) / 2, sideLen * sqrt(3) / 2}; // half of digLen
        var xc = center.x;
        var yc = center.y;
        var zc = center.z;

        var ps = new Point3D[8];
        int counter = 0;
        for (var hs1 : hsl)
            for (var hs2 : hsl)
                for (var hs3 : hsl)
                    ps[counter++] = new Point3D(xc + hs1, yc + hs2, zc + hs3);

        return new Area(new ArrayList<>(Arrays.asList(ps)),
                new FlatSurface(cs, false, color, ps[4], ps[5], ps[7], ps[6]),
                new FlatSurface(cs, false, color, ps[0], ps[2], ps[6], ps[4]),
                new FlatSurface(cs, false, color, ps[1], ps[3], ps[7], ps[5]),
                new FlatSurface(cs, false, color, ps[1], ps[5], ps[4], ps[0]),
                new FlatSurface(cs, false, color, ps[3], ps[7], ps[6], ps[2]),
                new FlatSurface(cs, false, color, ps[1], ps[3], ps[2], ps[0])
        ) {
            @Override
            public Point3D getCenter() {
                return new Point3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            }
        };
    }

    public static Area sphere(CoordinatedScreen canvas, Color color, Point3D center, double radius) {
        return new Area(canvas, color, true, 2f, -PI / 2, PI / 2, -PI, PI, 0.1, 0.1,
                        (x, y) -> new Point3D(radius * cos(x) * cos(y) + center.x, radius * cos(x) * sin(y) +
                                center.y, radius * sin(x) + center.z));
    }

    public static Area cylinder(CoordinatedScreen canvas, Color color, double radius, double height) {
        return new Area(canvas, color, true, 2f, -PI, PI, -PI, PI, 0.5, 0.5,
                Surface.surfaceOfRevolution(t -> new Point2D(radius, t)));
    }

    public static Area cylinder(CoordinatedScreen canvas, Color color) {
        return new Area(canvas, color, true, 2f, -PI/2, PI/2, -PI, PI, 0.1, 0.1,
                Surface.surfaceOfRevolution(t -> new Point2D(sin(t) * t, cos(t*t) * t)));
    }

    @Override
    public void rotate(Point3D center, double xAngle, double yAngle, double zAngle) {
        super.rotate(center, xAngle, yAngle, zAngle);
        shader.shade(this);
    }

    @Override
    public void affectMapper(Mapper3D... mappers) {
        super.affectMapper(mappers);
        shader.shade(this);
    }

    @Override
    public void move(double xChange, double yChange, double zChange) {
        super.move(xChange, yChange, zChange);
        shader.shade(this);
    }

    @FunctionalInterface
    private interface ColorSetter {
        Color colorOf(FlatSurface f);
    }
}

