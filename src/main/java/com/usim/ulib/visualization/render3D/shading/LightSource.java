package com.usim.ulib.visualization.render3D.shading;

import com.usim.ulib.jmath.datatypes.tuples.Point3D;

import java.awt.*;
import java.io.Serializable;

public class LightSource implements Serializable {
    private Point3D direction;
    private Point3D position;
    private Color color;
    private double intensity;
    private boolean isDotted;

    public LightSource(Point3D direction, Color color, double intensity) {
        this.direction = direction.normalize();
        this.color = color;
        this.intensity = intensity;
        position = new Point3D(20, 20, 20);
        isDotted = true;
    }

    public boolean isDotted() {
        return isDotted;
    }

    public void setDotted(boolean dotted) {
        isDotted = dotted;
    }

    public Point3D getPosition() {
        return position;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public Point3D getDirection() {
        return direction;
    }

    public void setDirection(Point3D direction) {
        this.direction = direction.normalize();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }
}
