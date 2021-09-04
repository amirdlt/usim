package com.usim.ulib.visualization.canvas;

import com.usim.ulib.utils.Utils;
import com.usim.ulib.jmath.datatypes.tuples.Point3D;
import com.usim.ulib.jmath.parser.Function4DParser;
import com.usim.ulib.visualization.shapes.shape3d.Area;
import com.usim.ulib.visualization.shapes.shape3d.Curve3D;
import com.usim.ulib.visualization.shapes.shape3d.Shape3D;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Graph3DCanvas extends Graph2DCanvas {

    private final Point3D rotationAroundCenter;

    public Graph3DCanvas() {
        setShowGrid(false);
        setShowAxis(false);
        setBackground(Color.BLACK);
        setShowMousePos(false);
        handleRotationByMouse();

        rotationAroundCenter = new Point3D();
    }

    private void handleRotationByMouse() {
        final var button = new int[1];
        final var mousePoint = new Point();
        final var cameraFocused = new boolean[] {false};
        final var mouseSensitivity = 350.0;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button[0] = e.getButton();
                mousePoint.setLocation(e.getPoint());
                if (e.isControlDown() && e.getClickCount() == 2)
                    cameraFocused[0] = !cameraFocused[0];
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @SuppressWarnings("SuspiciousNameCombination")
            @Override
            public void mouseDragged(MouseEvent e) {
                if (cameraFocused[0])
                    return;
                var xDif = (mousePoint.x - e.getX()) / mouseSensitivity;
                var yDif = (mousePoint.y - e.getY()) / mouseSensitivity;
                if (e.isControlDown() || e.isAltDown()) {
                    if (button[0] == MouseEvent.BUTTON1) {
                        if (e.isControlDown())
                            camera.rotate(0, xDif, yDif);
                        if (e.isAltDown()) {
//                            getRenderManager().getShape3d().forEach(shape -> shape.rotate(new Point3D(), -yDif, xDif, 0));
                            rotationAroundCenter.addVector(-yDif, xDif, 0);
                        }
                    } else if (button[0] == MouseEvent.BUTTON3) {
                        if (e.isControlDown())
                            camera.rotate(0, 0, yDif + xDif);
                        if (e.isAltDown()) {
//                            camera.getShape3d().forEach(shape -> shape.rotate(new Point3D(), 0, 0, yDif + xDif));
                            rotationAroundCenter.addVector(0, 0, xDif + yDif);
                        }
                    }
                }
                mousePoint.setLocation(e.getPoint());
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!cameraFocused[0])
                    return;
                var xDif = (mousePoint.x - e.getX()) / mouseSensitivity / 20;
                var yDif = (mousePoint.y - e.getY()) / mouseSensitivity / 20;
                camera.rotate(-yDif, xDif, yDif + xDif);
                repaint();
            }
        });
    }

    public void addFunction3DToDraw(String f) {
        var func = Function4DParser.parser(f).f3D(0);
        var area = new Area(this, Utils.randomColor(),
                coordinateX(0) * 0.7, coordinateX(getWidth()) * 0.7,
                coordinateY(getHeight()) * 0.7, coordinateY(0) * 0.7,
                0.5, 0.5, func);
        area.setFill(true);
        area.setThickness(1.5f);
        addRender(area);
        area.rotate(new Point3D(), rotationAroundCenter.x, rotationAroundCenter.y, rotationAroundCenter.z);
        stringBaseMap.put(f, area);
        repaint();
    }
    
    public void addArc3DToDraw(String arc3d) {
        var arc = Function4DParser.parser(arc3d).f().asArc3D(0, 0);
        var curve = new Curve3D(this, coordinateX(0) * 0.7, coordinateX(getWidth()) * 0.7, 0.1, arc);
        stringBaseMap.put(arc3d, curve);
        addRender(curve);
    }

    public static void simplePlotter(List<Point3D> points, CoordinatedScreen cs, Graphics2D g2d) {
        var vps = new ArrayList<>(points);
        vps.removeIf(e -> !Double.isFinite(e.x) || !Double.isFinite(e.y) || !Double.isFinite(e.z));
        var xa = new int[vps.size()];
        var ya = new int[vps.size()];
        int counter = 0;
        for (var p : vps) {
            xa[counter] = cs.screenX(p.x);
            ya[counter++] = cs.screenY(p.y);
        }
        var oldRenderingHints = g2d.getRenderingHints();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawPolyline(xa, ya, xa.length);
        g2d.setRenderingHints(oldRenderingHints);
    }

    public Point3D getRotationAroundCenter() {
        return rotationAroundCenter;
    }

    @Override
    protected JPanel getSettingPanel() {
        var settingPanel = new JPanel();
        settingPanel.setLayout(new BoxLayout(settingPanel, BoxLayout.Y_AXIS));
        var sp = new JPanel();
        sp.setLayout(new BoxLayout(sp, BoxLayout.Y_AXIS));

        var addFunc = new JButton("Add function3D");
        var addArc = new JButton("Add arc3D");

        sp.add(addFunc);
        var wrapper = new JPanel(new GridLayout(1, 0));
        wrapper.add(addFunc);
        wrapper.add(addArc);
        sp.add(wrapper);
        sp.add(getFunction3DList());

        addFunc.addActionListener(e -> {
            addFunction3DToDraw(JOptionPane.showInputDialog(""));
            sp.remove(1);
            var list = getFunction3DList();
            list.setPreferredSize(new Dimension(250, 0));
            sp.add(list);
            sp.revalidate();
            sp.repaint();
        });
        addArc.addActionListener(e -> addArc3DToDraw(JOptionPane.showInputDialog("")));

        sp.setBorder(BorderFactory.createTitledBorder("Graph3D Canvas"));
        settingPanel.add(sp);
        settingPanel.add(super.getSettingPanel());
        return settingPanel;
    }

    private JPanel getFunction3DList() {
        var list  = new JTable(new DefaultTableModel(new Object[][]{}, new String[] {"No.", "Function3D Expression"})) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        list.setRowHeight(30);
        list.getColumnModel().getColumn(0).setMaxWidth(40);
        var model = (DefaultTableModel) list.getModel();
        int counter = 0;
        for (var kv : stringBaseMap.entrySet())
            if (kv.getValue() instanceof Shape3D)
                model.addRow(new Object[] {++counter, kv.getKey()});
        var listPanel = new JPanel(new GridLayout(0, 1));
        listPanel.add(new JScrollPane(list));

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (listPanel.getComponentCount() == 2)
                    listPanel.remove(1);
                JPanel pp = null;
                if (stringBaseMap.get(model.getValueAt(list.getSelectedRow(), 1).toString()) instanceof Curve3D) {
                    pp = getArc3DPropertiesPanel((Curve3D) stringBaseMap.get(model.getValueAt(list.getSelectedRow(), 1).toString()));
                } else if (stringBaseMap.get(model.getValueAt(list.getSelectedRow(), 1).toString()) instanceof Area) {
                    pp = getFunction3DPropertiesPanel((Area) stringBaseMap.get(model.getValueAt(list.getSelectedRow(), 1).toString()));
                }
                var delete = new JButton("Delete");
                delete.addActionListener(ev -> {
                    var selected = list.getSelectedRow();
                    if (selected == -1) {
                        JOptionPane.showMessageDialog(Graph3DCanvas.this, "you should select a function first", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    var s = model.getValueAt(selected, 1).toString();
                    getRenderManager().remove(stringBaseMap.get(s));
                    stringBaseMap.remove(s);
                    listPanel.remove(1);
                    model.removeRow(selected);
                    repaint();
                    revalidate();
                });
                if (pp == null)
                    return;
                pp.add(delete);
                pp.setPreferredSize(new Dimension(220, 250));
                listPanel.add(new JScrollPane(pp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
                listPanel.repaint();
                listPanel.revalidate();
            }
        });
        listPanel.setPreferredSize(new Dimension(250, 0));
        return listPanel;
    }

    private JPanel getFunction3DPropertiesPanel(Area area) {
        var panel = new JPanel(new GridLayout(0, 2));
        var color = new JButton("Color");
        var precisionX = new JSlider(1, 3000, (int) (area.getDeltaX() * 1000));
        var precisionXLabel = new JLabel("DeltaX: " + Utils.round(area.getDeltaX(), 4));
        var precisionXY = new JSlider(1, 3000, (int) (area.getDeltaX() * 1000));
        var precisionXYLabel = new JLabel("DeltaXY: " + Utils.round(area.getDeltaX(), 4));
        var precisionY = new JSlider(1, 3000, (int) (area.getDeltaY() * 1000));
        var precisionYLabel = new JLabel("DeltaY: " + Utils.round(area.getDeltaY(), 4));
        var thicknessLabel = new JLabel("Thickness: " + area.getThickness());
        var thickness = new JSlider(0, 1000, (int) (area.getThickness() * 10));
        var upX = new JButton("UpBoundX");
        var lowX = new JButton("LowBoundX");
        var upY = new JButton("UpBoundY");
        var lowY = new JButton("LowBoundY");
        var visible = new JCheckBox("Visible", area.isVisible());
        var filled = new JCheckBox("Filled", area.isFilled());


        panel.add(color);
        panel.add(filled);
        panel.add(precisionXLabel);
        panel.add(precisionX);
        panel.add(precisionYLabel);
        panel.add(precisionY);
        panel.add(precisionXYLabel);
        panel.add(precisionXY);
        panel.add(thicknessLabel);
        panel.add(thickness);
        panel.add(upX);
        panel.add(lowX);
        panel.add(upY);
        panel.add(lowY);
        panel.add(visible);

        color.addActionListener(e -> {
            area.setColor(JColorChooser.showDialog(Graph3DCanvas.this, "ChooseFunctionColor", area.getColor()));
            repaint();
            revalidate();
        });
        precisionX.addChangeListener(e -> {
            area.setDeltaX(precisionX.getValue() / 1000d);
            precisionXLabel.setText("DeltaX: " + Utils.round(area.getDeltaX(), 4));
            repaint();
            revalidate();
        });
        precisionY.addChangeListener(e -> {
            area.setDeltaY(precisionY.getValue() / 1000d);
            precisionYLabel.setText("DeltaY: " + Utils.round(area.getDeltaY(), 4));
            repaint();
            revalidate();
        });
        thickness.addChangeListener(e -> {
            area.setThickness(thickness.getValue() / 10f);
            thicknessLabel.setText("Thickness: " + thickness.getValue() / 10f);
            repaint();
            revalidate();
        });
        visible.addActionListener(e -> {
            area.setVisible(visible.isSelected());
            repaint();
            revalidate();
        });
        upX.addActionListener(e -> {
            area.setUpBoundX(Double.parseDouble(JOptionPane.showInputDialog(Graph3DCanvas.this,
                    "Enter Up BoundX: (any exception won't change anything)", area.getUpBoundX())));
            repaint();
            revalidate();
        });
        lowX.addActionListener(e -> {
            area.setLowBoundX(Double.parseDouble(JOptionPane.showInputDialog(Graph3DCanvas.this,
                    "Enter Low BoundX: (any exception won't change anything)", area.getLowBoundX())));
            repaint();
            revalidate();
        });
        upY.addActionListener(e -> {
            area.setUpBoundY(Double.parseDouble(JOptionPane.showInputDialog(Graph3DCanvas.this,
                    "Enter Up BoundY: (any exception won't change anything)", area.getUpBoundY())));
            repaint();
            revalidate();
        });
        lowY.addActionListener(e -> {
            area.setLowBoundY(Double.parseDouble(JOptionPane.showInputDialog(Graph3DCanvas.this,
                    "Enter Low BoundY: (any exception won't change anything)", area.getLowBoundY())));
            repaint();
            revalidate();
        });
        filled.addActionListener(e -> {
            area.setFill(filled.isSelected());
            repaint();
            revalidate();
        });
        precisionXY.addChangeListener(e -> {
            area.setDeltaX(precisionXY.getValue() / 1000d);
            area.setDeltaY(precisionXY.getValue() / 1000d);
            precisionXLabel.setText("DeltaX: " + Utils.round(area.getDeltaX(), 4));
            precisionYLabel.setText("DeltaY: " + Utils.round(area.getDeltaY(), 4));
            precisionXYLabel.setText("DeltaXY: " + Utils.round(area.getDeltaY(), 4));
            precisionX.setValue(precisionXY.getValue());
            precisionY.setValue(precisionXY.getValue());
        });

        return panel;
    }

    private JPanel getArc3DPropertiesPanel(Curve3D curve3D) {
        var panel = new JPanel(new GridLayout(0, 2));
        var color = new JButton("Color");
        var precisionX = new JSlider(1, 3000, (int) (curve3D.getDeltaX() * 1000));
        var precisionXLabel = new JLabel("DeltaX: " + Utils.round(curve3D.getDeltaX(), 4));
        var thicknessLabel = new JLabel("Thickness: " + curve3D.getThickness());
        var thickness = new JSlider(0, 1000, (int) (curve3D.getThickness() * 10));
        var upX = new JButton("UpBoundX");
        var lowX = new JButton("LowBoundX");
        var visible = new JCheckBox("Visible", curve3D.isVisible());

        panel.add(color);
        panel.add(precisionXLabel);
        panel.add(precisionX);
        panel.add(thicknessLabel);
        panel.add(thickness);
        panel.add(upX);
        panel.add(lowX);
        panel.add(visible);

        color.addActionListener(e -> {
            curve3D.setColor(JColorChooser.showDialog(Graph3DCanvas.this, "ChooseFunctionColor", curve3D.getColor()));
            repaint();
            revalidate();
        });
        precisionX.addChangeListener(e -> {
            curve3D.setDeltaX(precisionX.getValue() / 1000d);
            precisionXLabel.setText("DeltaX: " + Utils.round(curve3D.getDeltaX(), 4));
            repaint();
            revalidate();
        });
        thickness.addChangeListener(e -> {
            curve3D.setThickness(thickness.getValue() / 10f);
            thicknessLabel.setText("Thickness: " + thickness.getValue() / 10f);
            repaint();
            revalidate();
        });
        visible.addActionListener(e -> {
            curve3D.setVisible(visible.isSelected());
            repaint();
            revalidate();
        });
        upX.addActionListener(e -> {
            curve3D.setUpBoundX(Double.parseDouble(JOptionPane.showInputDialog(Graph3DCanvas.this,
                    "Enter Up BoundX: (any exception won't change anything)", curve3D.getUpBoundX())));
            repaint();
            revalidate();
        });
        lowX.addActionListener(e -> {
            curve3D.setLowBoundX(Double.parseDouble(JOptionPane.showInputDialog(Graph3DCanvas.this,
                    "Enter Low BoundX: (any exception won't change anything)", curve3D.getLowBoundX())));
            repaint();
            revalidate();
        });

        return panel;
    }

    public void rotateShapes(Point3D center, double xAngle, double yAngle, double zAngle) {
        getRenderManager().getShape3d().forEach(shape -> shape.rotate(center, xAngle, yAngle, zAngle));
    }

    public void rotateShapes(double xAngle, double yAngle, double zAngle) {
        getRenderManager().getShape3d().forEach(shape -> shape.rotate(xAngle, yAngle, zAngle));
    }

    @Override
    protected void drawAxis(Graphics2D g2d) {
    }
}
