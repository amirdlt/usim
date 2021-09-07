package com.usim.engine.engine.swing;

import com.usim.ulib.jmath.datatypes.tuples.Point2D;
import com.usim.ulib.utils.Utils;
import com.usim.ulib.visualization.canvas.Graph2DCanvas;
import com.usim.ulib.visualization.canvas.Render;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.DoubleSupplier;

class MultiGraphPanelForSampling extends Graph2DCanvas {
    private final Map<String, Graph2d> graphs;
    private long updateCount;
    private int samplingCountInOneView;
    private boolean fixedScale;

    MultiGraphPanelForSampling(int width, int height) {
        super(true);
        graphs = new HashMap<>();
        setPreferredSize(new Dimension(width, height));
        setShiftX(width / 2);
        setShiftY(-height / 2);
        updateCount = 0;
        samplingCountInOneView = 20;
        fixedScale = true;
        init();
    }

    private void init() {
        setShowInfo(false);
        setShowMousePos(false);
        setShowAxis(false);
        setShowGrid(false);
        setLayout(null);

        add(new JButton("") {{
            setToolTipText("Clear");
            setBounds(8, 8, 15, 15);
            addActionListener(e -> {
                graphs.values().forEach(g -> g.points.clear());
                updateCount = 0;
            });
            MultiGraphPanelForSampling.this.repaint();
        }});
        add(new JButton("") {{
            setToolTipText("Show Stats");
            setBounds(8, 23, 15, 15);
            addActionListener(e -> {
                var pane = new JPanel(new GridLayout(0, 1)) {{
                    graphs.forEach((name, graph) -> add(new JLabel(
                            name + " | max value: " + Utils.round(maxY(name), 4) +
                                    " | mean value: " + Utils.round(meanY(name), 4)
                                    + " | min value: " + Utils.round(minY(name), 4)) {{
                        setForeground(graph.color);
                        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                    }}));
                    add(new JLabel("Sampling Count: " + updateCount) {{
                        setForeground(Color.WHITE);
                        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                    }});
                }};
                JOptionPane.showMessageDialog(MultiGraphPanelForSampling.this, pane, "Stats", JOptionPane.PLAIN_MESSAGE);
            });
        }});
        add(new JButton("") {{
            setToolTipText("Set Visibilities");
            setBounds(8, 38, 15, 15);
            addActionListener(e -> {
                var pane = new JPanel(new GridLayout(0, 1)) {{
                    graphs.forEach((name, graph) -> add(new JCheckBox(name) {{
                        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                        setForeground(graph.color);
                        setSelected(graph.visible.get());
                        addActionListener(e -> {
                            graph.visible.set(isSelected());
                            MultiGraphPanelForSampling.this.repaint();
                        });
                    }}));
                }};
                JOptionPane.showMessageDialog(MultiGraphPanelForSampling.this, pane, "Visibility", JOptionPane.PLAIN_MESSAGE);
            });
        }});
        add(new JButton("") {{
            setToolTipText("Settings");
            setBounds(8, 53, 15, 15);
            addActionListener(e -> {
                var pane = new JPanel(new GridLayout(0, 1)) {{
                    add(new JCheckBox("Fixed Scale") {{
                        setSelected(fixedScale);
                        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                        addActionListener(e -> fixedScale = isSelected());
                    }});
                    add(new JTextField(String.valueOf(samplingCountInOneView)) {{
                        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                        addActionListener(e -> {
                            try {
                                samplingCountInOneView = Integer.parseInt(getText());
                            } catch (Exception ignore) {}
                            setText(String.valueOf(samplingCountInOneView));
                        });
                    }});
                }};
                JOptionPane.showMessageDialog(MultiGraphPanelForSampling.this, pane, "Visibility", JOptionPane.PLAIN_MESSAGE);
            });
        }});
    }

    @Contract(pure = true)
    @Override
    protected @Nullable JPanel getSettingPanel() {
        return null;
    }

    public void addGraph(String name, float stroke, Color color, DoubleSupplier supplier) {
        var g = new Graph2d(supplier, new BasicStroke(stroke), color, new ArrayList<>(), this, new AtomicBoolean(true));
        graphs.put(name, g);
        addRender(g);
    }

    public void update() {
        setXYScale(fixedScale ? getWidth() : getWidth() / (double) samplingCountInOneView, getHeight());
        setShiftXY(fixedScale ? getWidth() / 2 : (int) (-getWidth() / 2 + (getWidth() / (double) samplingCountInOneView * updateCount)), -getHeight() / 2);
        graphs.values().forEach(Graph2d::addNewPoint);
        updateCount++;
        repaint();
    }

    @Override
    public void removeSettingPanel() {}

    public void removeGraph(String name) {
        getRenderManager().remove(graphs.get(name));
        graphs.remove(name);
    }

    private double maxX() {
        return updateCount;
    }

    private double maxY() {
        return graphs.values().stream().mapToDouble(g -> g.points.stream().mapToDouble(p -> p.y).filter(Double::isFinite).max().orElse(Double.NaN)).max()
                .orElse(Double.NaN);
    }

    private double maxY(String name) {
        return graphs.get(name).points.stream().mapToDouble(p -> p.y).filter(Double::isFinite).max().orElse(Double.NaN);
    }

    private double minY(String name) {
        return graphs.get(name).points.stream().mapToDouble(p -> p.y).filter(Double::isFinite).min().orElse(Double.NaN);
    }

    private double meanY(String name) {
        return graphs.get(name).points.stream().mapToDouble(p -> p.y).filter(Double::isFinite).average().orElse(Double.NaN);
    }

    private record Graph2d(DoubleSupplier supplier, Stroke stroke, Color color, List<Point2D> points, MultiGraphPanelForSampling panel, AtomicBoolean visible) implements Render {
        public void addNewPoint() {
            points.add(new Point2D(panel.updateCount, supplier.getAsDouble()));
        }

        @Override
        public void render(@NotNull Graphics2D g2d) {
            if (!visible.get())
                return;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setStroke(stroke);
            g2d.setColor(color);
            var maxX = panel.fixedScale ? panel.maxX() : 1;
            var maxY = panel.maxY();
            Graph2DCanvas.simplePlotter2D(points.stream().map(p -> new Point2D(p.x / maxX, p.y / maxY)).toList(), panel, g2d);
        }
    }
}
