package com.usim.ulib.visualization.animatedmodels;

import com.usim.ulib.jmath.datatypes.functions.CFunction;
import com.usim.ulib.jmath.datatypes.functions.ComplexFunction;
import com.usim.ulib.utils.Utils;
import com.usim.ulib.visualization.canvas.CoordinatedCanvas;
import com.usim.ulib.visualization.canvas.Render;

import java.awt.*;

public class ComplexFunctionVisualization implements Render {
    private CFunction function;
    private CoordinatedCanvas cc;

    public ComplexFunctionVisualization(CoordinatedCanvas cc, CFunction f) {
        function = f;
        this.cc = cc;
    }

    public CoordinatedCanvas getCc() {
        return cc;
    }

    public void setCc(CoordinatedCanvas cc) {
        this.cc = cc;
    }

    public CFunction getFunction() {
        return function;
    }

    public void setFunction(ComplexFunction function) {
        this.function = function;
    }

    @Override
    public void render(Graphics2D g2d) {
        if (function == null)
            return;
        final var w = cc.getWidth();
        final var h = cc.getHeight();
        g2d.drawImage(Utils.createImage(w, h, i -> {
            var cn = function.valueAt(cc.coordinateX(i % w), cc.coordinateY(i / w));
            return hsl2rgb(cn.phase, 1, 2/Math.PI * Math.atan(cn.absoluteValue)).getRGB();
//            return new Color(
//                    (float) Math.abs(2/PI*atan(cn.realValue)),
//                    (float) Math.abs(2/PI*atan(cn.imaginaryValue)),
//                    0.5f
//            ).getRGB();
//            return new Color(
//                    (float) Math.abs(cn.phase % 1),
//                    0.5f,
//                    (float) Math.abs(cn.absoluteValue % 1),
//                    0.5f
//            ).getRGB();

        }, 40), 0, 0, null);

//        g2d.drawImage(Utils.createImage(), 0, 0, null);
    }

    private static Color hsl2rgb(double h, double s, double l) {
        double v;
        double r,g,b;
        r = l;   // default to gra
        g = l;
        b = l;
        v = (l <= 0.5f) ? (l * (1.0f + s)) : (l + s - l * s);
        if (v > 0) {
            double m;
            double sv;
            int sextant;
            double fract, vsf, mid1, mid2;
            m = l + l - v;
            sv = (v - m ) / v;
            h *= 6.0;
            sextant = (int)h;
            fract = h - sextant;
            vsf = v * sv * fract;
            mid1 = m + vsf;
            mid2 = v - vsf;
            switch (sextant) {
                case 0 -> {
                    r = v;
                    g = mid1;
                    b = m;
                }
                case 1 -> {
                    r = mid2;
                    g = v;
                    b = m;
                }
                case 2 -> {
                    r = m;
                    g = v;
                    b = mid1;
                }
                case 3 -> {
                    r = m;
                    g = mid2;
                    b = v;
                }
                case 4 -> {
                    r = mid1;
                    g = m;
                    b = v;
                }
                case 5 -> {
                    r = v;
                    g = m;
                    b = mid2;
                }
            }
        }
        return new Color((float) r, (float) Math.abs(g) , (float) b);
    }

    @Override
    public void tick() {

    }
}
