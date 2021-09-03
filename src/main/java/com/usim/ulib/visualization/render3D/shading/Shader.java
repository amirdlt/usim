package com.usim.ulib.visualization.render3D.shading;

import com.usim.ulib.jmath.datatypes.tuples.Point3D;
import com.usim.ulib.visualization.shapes.shape3d.FlatSurface;
import com.usim.ulib.visualization.shapes.shape3d.Shape3D;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Shader implements Serializable {
    private final List<LightSource> lightSources;

    public Shader(List<LightSource> lightSources) {
        this.lightSources = new ArrayList<>();
        this.lightSources.addAll(lightSources);
    }

    public Shader(LightSource... lightSources) {
        this(Arrays.asList(lightSources));
    }

    public Shader(Point3D direction, Color lightningColor, double intensity) {
        this(new LightSource(direction, lightningColor, intensity));
    }

    public void shade(Shape3D... shapes) {
        try {
            for (var s : shapes)
                for (var c : s.getComponents())
                    if (c instanceof FlatSurface)
                        ((FlatSurface) c).setColor(colorUnderSource((FlatSurface) c));
        } catch (Exception e) {
//            ExceptionHandler.handle(e, ExceptionHandler.IGNORE);
        }
    }

    public Color colorUnderSource(FlatSurface surface) {
        if (surface.getPoints().size() < 3)
            return surface.getFixedColor();
        double factor = 0;
        Point3D meanColor = new Point3D();
        for (var ls : lightSources) {
            var d = Point3D.crossProduct(
                    Point3D.sub(surface.getPoints().get(0), surface.getPoints().get(1)),
                    Point3D.sub(surface.getPoints().get(1), surface.getPoints().get(2))).normalize().
                    dotProduct(ls.isDotted() ? Point3D.sub(surface.getCenter(), ls.getPosition()).normalize() : ls.getDirection());
            var tmp = Math.abs((d * d + 1) * (1 - ls.getIntensity()) / 2 + ls.getIntensity());
            factor += tmp;
            meanColor.addVector(
                    ls.getColor().getRed() * ls.getIntensity() * tmp,
                    ls.getColor().getGreen() * ls.getIntensity() * tmp,
                    ls.getColor().getBlue() * ls.getIntensity() * tmp
            );
        }
        meanColor.affectOnXYZ(x -> x / lightSources.size());
        return factorizedColor(surface.getFixedColor(), meanColor, factor / lightSources.size());
    }

    private Color factorizedColor(Color pre, Point3D meanColor, double factor) {
        var newR = (int) (pre.getRed() * factor + meanColor.x);
        var newG = (int) (pre.getGreen() * factor + meanColor.y);
        var newB = (int) (pre.getBlue() * factor + meanColor.z);
        newR = newR < 0 ? 0 : Math.min(newR, 255);
        newG = newG < 0 ? 0 : Math.min(newG, 255);
        newB = newB < 0 ? 0 : Math.min(newB, 255);
        return new Color(newR, newG, newB);
    }

    public List<LightSource> getLightSources() {
        return lightSources;
    }
}
