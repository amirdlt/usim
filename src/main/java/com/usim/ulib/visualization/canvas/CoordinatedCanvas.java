package com.usim.ulib.visualization.canvas;

import com.usim.ulib.jmath.datatypes.tuples.Point2D;
import com.usim.ulib.jmath.datatypes.tuples.Point3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.usim.ulib.utils.Utils.round;

@SuppressWarnings("unused")
public class CoordinatedCanvas extends Canvas implements CoordinatedScreen {
    protected static final Color DARK = Color.DARK_GRAY.darker().darker();
    protected static final Color LIGHT = Color.WHITE.darker();

    private boolean showGrid;
    private boolean showAxis;
    private boolean showMousePos;
    protected boolean isDark;

    protected double xScale;
    protected double yScale;
    protected int shiftX;
    protected int shiftY;

    private Point mousePoint;

    public CoordinatedCanvas(boolean setMouseListener, boolean setKeyListener) {
        xScale = 50;
        yScale = 50;
        showAxis = true;
        showGrid = true;
        setDark(true);
        showMousePos = true;
        if (setMouseListener)
            handleMouseListener();
        if (setKeyListener)
            handleKeyListener();
        addRender(this::drawGrid, this::drawAxis);
        camera.setCs(this);
    }

    public CoordinatedCanvas() {
        this(true, true);
    }

    private void handleMouseListener() {
        shiftX = 0;
        shiftY = 0;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePoint = e.getPoint();
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 &&
                        !e.isAltDown() && !e.isControlDown() && !e.isShiftDown()) {
                    shiftY = 0;
                    shiftX = 0;
                    xScale = 100;
                    yScale = 100;
                    camera.setPos(0, 0, 0);
                    camera.setAngle(0, 0, 0);
                    repaint();
                }

                if (!showMousePos || e.getButton() != MouseEvent.BUTTON1 ||
                        e.isAltDown() || e.isControlDown() || e.isShiftDown())
                    return;
                var mp = getMousePosition();
                var g = (Graphics2D) getGraphics();
                g.setColor(isDark ? LIGHT.darker() : DARK.brighter());
                g.setStroke(new BasicStroke(0.8f));
                try {
                    g.drawString("x = " + coordinateX(mp.x), mp.x, mp.y + 30);
                    g.drawString("y = " + coordinateY(mp.y), mp.x, mp.y + 45);
                } catch (NullPointerException ignore) {}
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                repaint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!e.isAltDown() && !e.isControlDown() && !e.isShiftDown()) {
                    var dx = mousePoint.x - e.getX();
                    var dy = mousePoint.y - e.getY();
                    moveOnPlane(dx, dy);
                }
                mousePoint = e.getPoint();
            }
        });
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double changeFactor = 1 - e.getPreciseWheelRotation() * 0.1;
                var mp = new Point2D(coordinateX(e.getX()), coordinateY(e.getY()));
                if (e.isControlDown()) {
                    camera.move(0, 0, (1-changeFactor) * 3);
                    repaint();
                } else {
                    xScale *= changeFactor;
                    yScale *= changeFactor;
                    var dx = screenX(mp.x) - e.getX();
                    var dy = screenY(mp.y) - e.getY();
                    xScale = Math.max(xScale, Double.MIN_VALUE);
                    yScale = Math.max(yScale, Double.MIN_VALUE);
                    moveOnPlane(dx, dy);
                }
            }
        });
    }

    private void handleKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W -> {camera.move(0, 0, -0.2); repaint();}
                    case KeyEvent.VK_S -> {camera.move(0, 0, 0.2); repaint();}
                    case KeyEvent.VK_A -> moveOnPlane(-5, 0);
                    case KeyEvent.VK_D -> moveOnPlane(5, 0);
                    case KeyEvent.VK_SPACE -> moveOnPlane(0, 5);
                    case KeyEvent.VK_SHIFT -> moveOnPlane(0, -5);
                    case KeyEvent.VK_CONTROL + KeyEvent.VK_PLUS -> zoom(1.1);
                    case KeyEvent.VK_CONTROL + KeyEvent.VK_MINUS -> zoom(0.9);
                }
                if (e.isControlDown())
                    zoom(1 + (e.getKeyCode() == KeyEvent.VK_EQUALS ? 0.1 : e.getKeyCode() == KeyEvent.VK_MINUS ? -0.1 : 0));
            }
        });
//        Toolkit.getDefaultToolkit().addAWTEventListener(e -> getKeyListeners()[0].keyTyped((KeyEvent) e), AWTEvent.KEY_EVENT_MASK);
    }

    protected Color getAxisColor() {
        return isDark ? LIGHT.darker() : DARK.brighter();
    }

    protected void drawAxis(Graphics2D g2d) {
        if (!showAxis)
            return;
        g2d.setColor(getAxisColor());
        g2d.setStroke(new BasicStroke(1.8f));
        g2d.drawLine(0, getHeight() / 2 - shiftY, getWidth(), getHeight() / 2 - shiftY);
        g2d.drawLine(getWidth() / 2 - shiftX, 0, getWidth() / 2 - shiftX, getHeight());
        g2d.fillPolygon(new int[]{getWidth() - 20, getWidth() - 20, getWidth()},
                new int[]{getHeight() / 2 - 5 - shiftY, getHeight() / 2 + 5 - shiftY, getHeight() / 2 - shiftY}, 3);
        g2d.fillPolygon(new int[]{getWidth() / 2 - 5 - shiftX, getWidth() / 2 + 5 - shiftX, getWidth() / 2 - shiftX},
                new int[]{20, 20, 0}, 3);
    }

    public boolean isShowMousePos() {
        return showMousePos;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public boolean isShowAxis() {
        return showAxis;
    }

    protected void drawGrid(Graphics2D g2d) {
        if (!showGrid)
            return;
        int xCenter = getWidth() / 2;
        int yCenter = getHeight() / 2;

        double gridXScale = Math.max(xScale / 5.0, 1);
        double gridYScale = Math.max(yScale / 5.0, 1);

        g2d.setColor(isDark ? LIGHT.darker().darker() : DARK.brighter().brighter());
        for (int i  = 0; i < getHeight() / (gridYScale * 2) + 1 + Math.abs(shiftY); i++) {
            g2d.setStroke(new BasicStroke(i % 5 == 0 ? 0.5f : 0.1f));
            var dd = (int) (i * gridYScale);
            g2d.drawLine(0, yCenter - dd - shiftY, getWidth(), yCenter - dd - shiftY);
            g2d.drawLine(0, yCenter + dd - shiftY, getWidth(), yCenter + dd - shiftY);
        }
        for (int i = 0; i < getWidth() / (gridXScale * 2) + 1 + Math.abs(shiftX); i++) {
            g2d.setStroke(new BasicStroke(i % 5 == 0 ? 0.5f : 0.1f));
            var dd = (int) (i * gridXScale);
            g2d.drawLine(xCenter - dd - shiftX, 0,  xCenter - dd - shiftX, getHeight());
            g2d.drawLine(xCenter + dd - shiftX, 0,  xCenter + dd - shiftX, getHeight());
        }
    }

    @Override
    protected JPanel getSettingPanel() {
        var settingPanel = new JPanel();
        settingPanel.setLayout(new BoxLayout(settingPanel, BoxLayout.Y_AXIS));

        var sp = new JPanel(new GridLayout(0, 2));
        ////////////
        var setX = new JButton("Set X");
        var setY = new JButton("Set Y");
        var zoomIn = new JButton("Zoom In");
        var zoomOut = new JButton("Zoom Out");
        var scaleX = new JButton("Scale X");
        var scaleY = new JButton("Scale Y");
        var moveX = new JButton("x++");
        var moveRX = new JButton("x--");
        var moveY = new JButton("y++");
        var moveRY = new JButton("y--");
        var moveXY = new JButton("x++, y++");
        var moveRXY = new JButton("x--, y++");
        var moveXRY = new JButton("x++, y--");
        var moveRXRY = new JButton("x--, y--");
        var setMoveLen = new JButton("SetMoveLen");
        var reset = new JButton("Reset");
        var showMousePos = new JCheckBox("Show Mouse Pos", isShowMousePos());
        var drawGrid = new JCheckBox("Draw Grid", showGrid);
        var drawAxis = new JCheckBox("Draw Axis", showAxis);
        var dark = new JCheckBox("Dark", isDark);

        sp.add(setX);
        sp.add(setY);
        sp.add(zoomIn);
        sp.add(zoomOut);
        sp.add(scaleX);
        sp.add(scaleY);
        sp.add(showMousePos);
        sp.add(drawAxis);
        sp.add(drawGrid);
        sp.add(dark);
        sp.add(moveX);
        sp.add(moveRX);
        sp.add(moveY);
        sp.add(moveRY);
        sp.add(moveXY);
        sp.add(moveRXY);
        sp.add(moveRXRY);
        sp.add(moveXRY);
        sp.add(setMoveLen);
        sp.add(reset);

        AtomicInteger moveLen = new AtomicInteger(10);

        setX.addActionListener(e -> setShiftX(Double.parseDouble(JOptionPane.showInputDialog(CoordinatedCanvas.this, "Enter X coordinate: (If any exception occurred nothing will change)"))));
        setY.addActionListener(e -> setShiftY(Double.parseDouble(JOptionPane.showInputDialog(CoordinatedCanvas.this, "Enter Y coordinate: (If any exception occurred nothing will change)"))));
        zoomIn.addActionListener(e -> zoom(1.05));
        zoomOut.addActionListener(e -> zoom(0.95));
        scaleX.addActionListener(e -> setXScale(Double.parseDouble(JOptionPane.showInputDialog(CoordinatedCanvas.this, "Enter Scale X: (If any exception occurred nothing will change)"))));
        scaleY.addActionListener(e -> setYScale(Double.parseDouble(JOptionPane.showInputDialog(CoordinatedCanvas.this, "Enter Scale Y: (If any exception occurred nothing will change)"))));
        setMoveLen.addActionListener(e -> moveLen.set(Integer.parseInt(JOptionPane.showInputDialog(CoordinatedCanvas.this, "Enter move len in pixel unit: (If any exception occurred nothing will change)"))));
        showMousePos.addActionListener(e -> setShowMousePos(showMousePos.isSelected()));
        drawAxis.addActionListener(e -> setShowAxis(drawAxis.isSelected()));
        drawGrid.addActionListener(e -> setShowGrid(drawGrid.isSelected()));
        dark.addActionListener(e -> setDark(dark.isSelected()));
        moveX.addActionListener(e -> moveOnPlane(coordinateXLen(moveLen.get()), 0));
        moveY.addActionListener(e -> moveOnPlane(0, -coordinateYLen(moveLen.get())));
        moveXY.addActionListener(e -> moveOnPlane(coordinateXLen(moveLen.get()), -coordinateYLen(moveLen.get())));
        moveRXY.addActionListener(e -> moveOnPlane(-coordinateXLen(moveLen.get()), -coordinateYLen(moveLen.get())));
        moveXRY.addActionListener(e -> moveOnPlane(coordinateXLen(moveLen.get()), coordinateYLen(moveLen.get())));
        moveRXRY.addActionListener(e -> moveOnPlane(-coordinateXLen(moveLen.get()), coordinateYLen(moveLen.get())));
        moveRX.addActionListener(e -> moveOnPlane(-coordinateXLen(moveLen.get()), 0));
        moveRY.addActionListener(e -> moveOnPlane(0, coordinateYLen(moveLen.get())));
        moveX.addActionListener(e -> moveOnPlane(coordinateXLen(moveLen.get()), 0));
        reset.addActionListener(e -> setShiftXY(0, 0));
        reset.addActionListener(e -> setXYScale(100, 100));

        var bl = new MouseAdapter() {
            private boolean isPressed = false;

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                new Thread(() -> {
                    while (isPressed) {
                        ((JButton) e.getSource()).getActionListeners()[0].actionPerformed(null);
                        try {
                            Thread.sleep(150);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
            }
        };
        moveX.addMouseListener(bl);
        moveRX.addMouseListener(bl);
        moveXY.addMouseListener(bl);
        moveXRY.addMouseListener(bl);
        moveRXRY.addMouseListener(bl);
        moveRXY.addMouseListener(bl);
        moveY.addMouseListener(bl);
        moveRY.addMouseListener(bl);
        zoomIn.addMouseListener(bl);
        zoomOut.addMouseListener(bl);

        sp.setBorder(BorderFactory.createTitledBorder("Coordinated Canvas"));
        ////////////
        settingPanel.add(sp);
        settingPanel.add(super.getSettingPanel());

        return settingPanel;
    }

    @Override
    public final double coordinateX(int screenX) {
        return (screenX + shiftX - getWidth() / 2.0) / xScale;
    }

    @Override
    public final double coordinateY(int screenY) {
        return -(screenY + shiftY - getHeight() / 2.0) / yScale;
    }

    @Override
    public final int screenX(double value) {
        return (int) (getWidth() / 2.0 + value * xScale - shiftX);
    }

    @Override
    public final int screenY(double value) {
        return (int) (getHeight() / 2.0 - value * yScale - shiftY);
    }

    @Override
    public Point screen(Point2D p) {
        return new Point(screenX(p.x), screenY(p.y));
    }

    @Override
    public Point screen(Point3D p) {
        double dist = Math.sqrt(p.x*p.x + p.y*p.y) * Math.abs(10/(10+camera.getZ()-p.z));
        double theta = Math.atan2(p.y, p.x);
        return new Point(screenX(dist * Math.cos(theta)), screenY(dist * Math.sin(theta)));
    }

    public final void zoom(double factor) {
        xScale *= factor;
        yScale *= factor;
        repaint();
    }

    public void removeListeners() {
        for (var l : getKeyListeners())
            removeKeyListener(l);
        for (var l : getMouseWheelListeners())
            removeMouseWheelListener(l);
        for (var l : getMouseMotionListeners())
            removeMouseMotionListener(l);
        for (var l : getMouseListeners())
            removeMouseListener(l);
    }

    public double getXScale() {
        return xScale;
    }

    public void setXScale(double xScale) {
        this.xScale = Math.max(Math.abs(xScale), Double.MIN_VALUE);
        repaint();
    }

    public void setYScale(double yScale) {
        this.yScale = Math.max(Math.abs(yScale), Double.MIN_VALUE);
        repaint();
    }

    public double getYScale() {
        return yScale;
    }

    public final void setXYScale(double xScale, double yScale) {
        setXScale(xScale);
        setYScale(yScale);
    }

    public int getShiftX() {
        return shiftX;
    }

    public void setShiftX(int shiftX) {
        this.shiftX = shiftX;
        camera.setX(coordinateX(shiftX));
        repaint();
    }

    public int getShiftY() {
        return shiftY;
    }

    public void setShiftXY(int shiftX, int shiftY) {
        setShiftX(shiftX);
        setShiftY(shiftY);
    }

    public void setShiftY(int shiftY) {
        this.shiftY = shiftY;
        camera.setY(coordinateY(shiftY));
        repaint();
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
        repaint();
    }

    public void setShowMousePos(boolean showMousePos) {
        this.showMousePos = showMousePos;
        repaint();
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setDark(boolean isDark) {
        this.isDark = isDark;
        backGround = isDark ? DARK : LIGHT;
        repaint();
    }

    public void moveOnPlane(double dx, double dy) {
        shiftX += dx * xScale;
        shiftY -= dy * yScale;
        camera.move(dx, dy, 0);
        repaint();
    }

    public void moveOnPlane(int dx, int dy) {
        shiftX += dx;
        shiftY += dy;
        camera.move(dx / xScale, dy / yScale, 0);
        repaint();
    }

    public void setShiftX(double shiftX) {
        setShiftX(screenX(shiftX));
        camera.setX(shiftX);
        repaint();
    }

    public void setShiftY(double shiftY) {
        setShiftY(screenY(shiftY));
        camera.setY(shiftY);
        repaint();
    }

    public void setShiftXY(double shiftX, double shiftY) {
        setShiftXY(screenX(shiftX), screenY(shiftY));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!isShowInfo())
            return;
        g.drawString("ScaleX: " + round(xScale, 2) + ", ScaleY: " + round(yScale, 2), 0, (int) (infoFont.getSize() * 2.1));
        if (mousePoint != null)
            g.drawString("MouseClicked(Pixels): (" + mousePoint.x + ", " + mousePoint.y +
                    "), MouseClicked(Coordinated): (" + round(coordinateX(mousePoint.x), 2) + ", " + round(coordinateY(mousePoint.y), 2) + "), Z: " + round(camera.getZ(), 2), 0, (int) (infoFont.getSize() * 3.2));
        if (!isRunning())
            return;
        var mp = getMousePosition();
        try {
            g.drawString(
                    "MousePos(Pixels): (" + mp.x + ", " + mp.y + ")" +
                            ", MousePos(Coordinated): (" + round(coordinateX(mp.x), 2) + ", " + round(coordinateY(mp.y), 2) + ")"
                    , 0, (int) (infoFont.getSize() * 4.5));
        } catch (NullPointerException ignore) {}
    }
}
