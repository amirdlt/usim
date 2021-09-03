package com.usim.ulib.visualization.shapes.shape2d.grid2d;

import com.usim.ulib.jmath.datatypes.tuples.Point2D;
import com.usim.ulib.utils.supplier.BooleanSupplier;
import com.usim.ulib.utils.supplier.ColorSupplier;
import com.usim.ulib.utils.supplier.StringSupplier;
import com.usim.ulib.utils.Utils;
import com.usim.ulib.visualization.canvas.CoordinatedScreen;
import com.usim.ulib.visualization.canvas.Render;

import java.awt.*;

public class Tile2D implements Render {
    private BooleanSupplier isVisible;
    private double width;
    private double height;
    private ColorSupplier colorFunc;
    private StringSupplier textFunction;
    private Point2D pos;
    private boolean filled;
    private CoordinatedScreen cs;
    private float thickness;

    public Tile2D(CoordinatedScreen cs, Point2D pos, double width, double height, ColorSupplier colorFunc, boolean filled, StringSupplier textFunction) {
        this.cs = cs;
        this.textFunction = textFunction;
        this.width = width;
        this.height = height;
        this.colorFunc = colorFunc;
        this.pos = pos;
        this.filled = filled;
        isVisible = () -> true;
        thickness = 2.5f;
    }

    public Tile2D(CoordinatedScreen cs, Point2D pos, double width, double height, ColorSupplier colorFunc, boolean filled) {
        this(cs, pos, width, height, colorFunc, filled, () -> null);
    }

    public Tile2D(CoordinatedScreen cs, Point2D pos, double width, double height, ColorSupplier colorFunc) {
        this(cs, pos, width, height, colorFunc, true, () -> null);
    }

    public Tile2D(CoordinatedScreen cs, Point2D pos, ColorSupplier colorFunc) {
        this(cs, pos, 1, 1, colorFunc, true, () -> null);
    }

    public Tile2D(CoordinatedScreen cs, ColorSupplier colorFunc) {
        this(cs, new Point2D(), 1, 1, colorFunc, true, () -> null);
    }

    public Tile2D(CoordinatedScreen cs) {
        this(cs, new Point2D(), 1, 1, Utils::randomColor, true, () -> null);
    }

    public Tile2D(CoordinatedScreen cs, Point2D pos, double width, double height, ColorSupplier colorFunc, float thickness, StringSupplier textFunction) {
        this(cs, pos, width, height, colorFunc, false, textFunction);
        this.thickness = thickness;
    }

    public void setVisible(BooleanSupplier visible) {
        isVisible = visible;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public ColorSupplier getColorFunc() {
        return colorFunc;
    }

    public void setColorFunc(ColorSupplier colorFunc) {
        this.colorFunc = colorFunc;
    }

    public StringSupplier getTextFunction() {
        return textFunction;
    }

    public void setTextFunction(StringSupplier textFunction) {
        this.textFunction = textFunction;
    }

    public Point2D getPos() {
        return pos;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public CoordinatedScreen getCs() {
        return cs;
    }

    public void setCs(CoordinatedScreen cs) {
        this.cs = cs;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public void setPos(double x, double y) {
        pos.set(x, y);
    }

    public void setPos(Point2D pos) {
        this.pos = pos;
    }

    public void move(double dx, double dy) {
        pos.addVector(dx, dy);
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(colorFunc.getColor());
        if (filled) {
            g2d.fillRect(cs.screenX(pos.x), cs.screenY(pos.y), cs.screenXLen(width), cs.screenYLen(height));
        } else {
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRect(cs.screenX(pos.x), cs.screenY(pos.y), cs.screenXLen(width), cs.screenYLen(height));
        }
        var label = textFunction.getText();
        if (label == null || label.isEmpty())
            return;
        g2d.setColor(textFunction.getColor());
        g2d.setFont(textFunction.getFont());
        g2d.drawString(label, cs.screenX(pos.x + width/2) - g2d.getFontMetrics().stringWidth(label)/2, cs.screenY(pos.y - height/2));
    }

    @Override
    public boolean isVisible() {
        return isVisible.is();
    }
}
