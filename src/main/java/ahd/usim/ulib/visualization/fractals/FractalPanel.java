package ahd.usim.ulib.visualization.fractals;

import ahd.usim.ulib.swingutils.MainFrame;
import ahd.usim.ulib.visualization.canvas.Graph3DCanvas;
import ahd.usim.ulib.jmath.datatypes.functions.Function3D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class FractalPanel extends Graph3DCanvas {

    private final ArrayList<Thread> threads;
    private int numOfThreads;
    private boolean inProgress;
    private int maxIteration;
    private ColorScheme cs;
    private int[] colorPalette;
    private Function3D conditionFunction;
    private Function3D realIterationFunction;
    private Function3D imaginaryIterationFunction;
    private double escapeRadius;

    public FractalPanel() {
        removeAllRenders();
        inProgress = false;
        numOfThreads = 15;
        threads = new ArrayList<>(numOfThreads);
        maxIteration = 1000;
        cs = new ColorScheme(maxIteration);
        colorPalette = new int[maxIteration];
        for (int i = 0; i < maxIteration; i++)
            colorPalette[i] = cs.getColor(i);
        escapeRadius = 100;
        conditionFunction = (x, y) -> x * x - y * y;
        realIterationFunction = (x, y) -> x * x - y * y;
        imaginaryIterationFunction = (x, y) -> 2 * x * y;
    }

    public Function3D getConditionFunction() {
        return conditionFunction;
    }

    public void setConditionFunction(Function3D conditionFunction) {
        this.conditionFunction = conditionFunction;
        repaint();
    }

    public Function3D getRealIterationFunction() {
        return realIterationFunction;
    }

    public void setRealIterationFunction(Function3D realIterationFunction) {
        this.realIterationFunction = realIterationFunction;
        repaint();
    }

    public Function3D getImaginaryIterationFunction() {
        return imaginaryIterationFunction;
    }

    public void setImaginaryIterationFunction(Function3D imaginaryIterationFunction) {
        this.imaginaryIterationFunction = imaginaryIterationFunction;
        repaint();
    }

    public double getEscapeRadius() {
        return escapeRadius;
    }

    public void setEscapeRadius(double escapeRadius) {
        this.escapeRadius = escapeRadius;
        repaint();
    }

    public int getMaxIteration() {
        return maxIteration;
    }

    public void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
        cs.setMaxIteration(maxIteration);
        colorPalette = new int[maxIteration];
        for (int i = 0; i < maxIteration; i++)
            colorPalette[i] = cs.getColor(i);
        repaint();
    }

    public int getNumOfThreads() {
        return numOfThreads;
    }

    public void setNumOfThreads(int numOfThreads) {
        this.numOfThreads = numOfThreads;
    }

    @Override
    public synchronized void paintComponent(Graphics g) {
        g.drawImage(frame(), 0, 0, null);
    }

    protected BufferedImage frame() {
        if (inProgress)
            return null;
        var canvas = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        var g2d = canvas.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(backGround);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        ColorScheme cs = new ColorScheme(maxIteration);
        int[] colorPallet = new int[maxIteration];
        for (int i = 0; i < maxIteration; i++)
            colorPallet[i] = cs.getColor(i);

        if (!inProgress) {
            inProgress = true;

            var width = canvas.getWidth();
            var height = canvas.getHeight();

//            System.out.println((System.currentTimeMillis() - tt) / 1000.0);
            inProgress = false;
        }

        return canvas;
    }

    public static void main(String[] args) {
        var fractalPanel = new FractalPanel();
        var frame = new MainFrame("Fractal Rendering");
        frame.add(fractalPanel);
        SwingUtilities.invokeLater(frame);
    }
}
