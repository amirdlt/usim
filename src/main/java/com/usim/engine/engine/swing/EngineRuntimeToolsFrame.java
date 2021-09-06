package com.usim.engine.engine.swing;

import com.usim.engine.engine.Constants;
import com.usim.engine.engine.internal.Engine;
import com.usim.ulib.jmath.datatypes.tuples.Point2D;
import com.usim.ulib.swingutils.MainFrame;
import com.usim.ulib.utils.Utils;
import com.usim.ulib.visualization.canvas.Graph2DCanvas;
import jdk.jshell.execution.Util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EngineRuntimeToolsFrame extends MainFrame {
    private final Engine engine;
    private Timer updater;

    public EngineRuntimeToolsFrame() {
        super("AHD:: Engine Runtime Tools");
        engine = Engine.get();
        init();
    }

    private void init() {
        setSize(400, 650);
        setTrayIconPath(Constants.DEFAULT_SWING_ICON_PATH);
        setIconImage(new ImageIcon(Constants.DEFAULT_SWING_ICON_PATH).getImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        var pointMap = new HashMap<String, List<Point2D>>() {{
            put("cpuUsage-points", new ArrayList<>());
        }};
        updater = new Timer(125, e -> {
            updateElements();
            pointMap.get("cpuUsage-points").add(new Point2D(pointMap.get("cpuUsage-points").size() + 1, Utils.cpuUsageByJVM()));
        });
        element("main", new JPanel() {{
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                add(new JLabel("FPS: "));
                add(element("fps-textField", new JTextField(String.valueOf(engine.getTargetFps())) {{
                    setPreferredSize(new Dimension(60, 30));
                }}));
                add(new JLabel("UPS: "));
                add(element("ups-textField", new JTextField(String.valueOf(engine.getTargetUps())) {{
                    setPreferredSize(new Dimension(60, 30));
                }}));
                add(new JLabel("IPS: "));
                add(element("ips-textField", new JTextField(String.valueOf(engine.getTargetIps())) {{
                    setPreferredSize(new Dimension(60, 30));
                }}));
                add(element("apply-button", new JButton("Apply") {{
                    addActionListener(e -> {
                        try {
                            engine.setTargetUps(Integer.parseInt(textFieldE("ups-textField").getText()));
                            engine.setTargetFps(Integer.parseInt(textFieldE("fps-textField").getText()));
                            engine.setTargetIps(Integer.parseInt(textFieldE("ips-textField").getText()));
                        } catch (Exception ex) { ex.printStackTrace(); }
                        textFieldE("fps-textField").setText(String.valueOf(engine.getTargetFps()));
                        textFieldE("ups-textField").setText(String.valueOf(engine.getTargetUps()));
                        textFieldE("ips-textField").setText(String.valueOf(engine.getTargetIps()));
                        updateElements();
                    });
                }}));
            }});
            add(new JSeparator());
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                add(element("fps-label", new JLabel("FPS: " + Utils.round(engine.getFps(), 2))));
                add(element("ups-label", new JLabel("| UPS: " + Utils.round(engine.getUps(), 2))));
                add(element("ips-label", new JLabel("| IPS: " + Utils.round(engine.getIps(), 2))));
            }});
            add(new JSeparator());
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                add(new JLabel("Latencies: "));
                add(element("renderTime-label", new JLabel("Render: " + Utils.round(engine.getRenderTime(), 2) + " ms")));
                add(element("updateTime-label", new JLabel("| Update: " + Utils.round(engine.getUpdateTime(), 2) + " ms")));
                add(element("inputTime-label", new JLabel("| Input: " + Utils.round(engine.getInputTime(), 2) + " ms")));
            }});
            add(new JSeparator());
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                add(element("frameLoss-label", new JLabel("Frame Loss: " + engine.getFrameLoss())));
                add(element("cpuUsage-label", new JLabel("CPU Usage: " + Utils.round(Utils.cpuUsageByJVM() * 100, 2) + "%")));
                add(element("heapUsage-label",
                        new JLabel("Heap Usage: " + Utils.round(Utils.usedHeapSize() / (float) Constants.MEGA, 2))));
            }});
            add(element("cpuUsage-graph2d", new Graph2DCanvas() {{
                setShowGrid(false);
                setPreferredSize(new Dimension(350, 120));
                setMaximumSize(new Dimension(350, 120));
                setMinimumSize(new Dimension(350, 120));
                setShowAxis(false);
                setShowInfo(false);
                setShowMousePos(false);
                addRender(g2d -> {
                    g2d.setColor(Color.RED);
                    var points = pointMap.get("cpuUsage-points");
                    var max = points.stream().mapToDouble(p -> p.x).max().orElse(1);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Graph2DCanvas.simplePlotter2D(points.stream().map(p -> new Point2D(p.x / max, p.y)).toList(), graph2DCanvasE("cpuUsage-graph2d"), g2d);
                });
            }}));
            add(new JSeparator());
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                add(new JLabel("Counts: "));
                add(element("renderCount-label", new JLabel("Render: " + engine.getRenderCount())));
                add(element("updateCount-label", new JLabel("| Update: " + engine.getRenderCount())));
                add(element("inputCount-label", new JLabel("| Input: " + engine.getRenderCount())));
            }});
        }}).setLayout(new BoxLayout(element("main"), BoxLayout.Y_AXIS));
        add(new JScrollPane(element("main")));
        updater.start();
    }

    private void updateElements() {
        labelE("fps-label").setText("FPS: " + Utils.round(engine.getFps(), 2));
        labelE("ups-label").setText("| UPS: " + Utils.round(engine.getUps(), 2));
        labelE("ips-label").setText("| IPS: " + Utils.round(engine.getIps(), 2));

        labelE("renderTime-label").setText("Render: " + Utils.round(engine.getRenderTime(), 2) + " ms");
        labelE("updateTime-label").setText("| Update: " + Utils.round(engine.getUpdateTime(), 2) + " ms");
        labelE("inputTime-label").setText("| Input: " + Utils.round(engine.getInputTime(), 2) + " ms");

        labelE("frameLoss-label").setText("Frame Loss: " + engine.getFrameLoss());
        labelE("cpuUsage-label").setText("| CPU Usage: " + Utils.round(Utils.cpuUsageByJVM() * 100, 2) + "%");
        labelE("heapUsage-label").setText("| Heap Usage: " + Utils.round(Utils.usedHeapSize() / (float) Constants.MEGA, 2) + " MB");

        labelE("renderCount-label").setText("Render: " + engine.getRenderCount());
        labelE("updateCount-label").setText("| Update: " + engine.getRenderCount());
        labelE("inputCount-label").setText("| Input: " + engine.getRenderCount());
    }

    @Override
    protected void closeAction() {
        updater.stop();
        gotoSystemTray();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) updater.start(); else updater.stop();
    }
}
