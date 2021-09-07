package com.usim.engine.engine.swing;

import com.usim.engine.engine.Constants;
import com.usim.engine.engine.internal.Engine;
import com.usim.ulib.swingutils.MainFrame;
import com.usim.ulib.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EngineRuntimeToolsFrame extends MainFrame {
    private final Engine engine;
    private final ScheduledThreadPoolExecutor updater;
    private ScheduledFuture<?> updateFuture;
    private int updateRate;
    private float updateLatency;

    public EngineRuntimeToolsFrame() {
        super("AHD:: Engine Runtime Tools");
        engine = Engine.get();
        updater = new ScheduledThreadPoolExecutor(1);
        updateFuture = null;
        updateRate = 500;
        updateLatency = Float.NaN;
        init();
    }

    private void init() {
        setSize(520, 650);
        setTrayIconPath(Constants.DEFAULT_SWING_ICON_PATH);
        setIconImage(new ImageIcon(Constants.DEFAULT_SWING_ICON_PATH).getImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        element("main", new JPanel() {{
            setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                add(new JLabel("FPS: "));
                add(element("fps-textField", new JTextField(String.valueOf(engine.getTargetFps())) {{
                    setPreferredSize(new Dimension(80, 30));
                    addActionListener(e -> {
                        try {
                            engine.setTargetFps(Integer.parseInt(getText()));
                        } catch (NumberFormatException ignore) {}
                        setText(String.valueOf(engine.getTargetFps()));
                    });
                }}));
                add(new JLabel("UPS: "));
                add(element("ups-textField", new JTextField(String.valueOf(engine.getTargetUps())) {{
                    setPreferredSize(new Dimension(80, 30));
                    addActionListener(e -> {
                        try {
                            engine.setTargetUps(Integer.parseInt(getText()));
                        } catch (NumberFormatException ignore) {}
                        setText(String.valueOf(engine.getTargetUps()));
                    });
                }}));
                add(new JLabel("IPS: "));
                add(element("ips-textField", new JTextField(String.valueOf(engine.getTargetIps())) {{
                    setPreferredSize(new Dimension(80, 30));
                    addActionListener(e -> {
                        try {
                            engine.setTargetIps(Integer.parseInt(getText()));
                        } catch (NumberFormatException ignore) {}
                        setText(String.valueOf(engine.getTargetIps()));
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
                add(element("renderTime-label", new JLabel("Render: " + Utils.round(engine.getRenderTime(), 2) + " ms") {{
                    setForeground(Color.RED);
                }}));
                add(element("updateTime-label", new JLabel("| Update: " + Utils.round(engine.getUpdateTime(), 2) + " ms") {{
                    setForeground(Color.GREEN);
                }}));
                add(element("inputTime-label", new JLabel("| Input: " + Utils.round(engine.getInputTime(), 2) + " ms") {{
                    setForeground(Color.BLUE);
                }}));
            }});
            add(element("latencies-graph", new MultiGraphPanelForSampling(350, 120) {{
                addGraph("renderTime", 2, Color.RED, engine::getRenderTime);
                addGraph("updateTime", 2, Color.GREEN, engine::getUpdateTime);
                addGraph("inputTime", 2, Color.BLUE, engine::getInputTime);
            }}));
            add(new JSeparator());
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                add(element("frameLoss-label", new JLabel("Frame Loss: " + engine.getFrameLoss())));
                add(element("cpuUsage-label", new JLabel("CPU Usage: " + Utils.round(Utils.cpuUsageByJVM() * 100, 2) + "%") {{
                    setForeground(Color.RED);
                }}));
                add(element("heapUsage-label",
                        new JLabel("Heap Usage: " + Utils.round(Utils.usedHeapSize() / (float) Constants.MEGA, 2))));
            }});
            add(element("resourceUsage-graph", new MultiGraphPanelForSampling(350, 120) {{
                addGraph("cpuUsage", 2, Color.RED, Utils::cpuUsageByJVM);
            }}));
            add(new JSeparator());
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                add(new JLabel("Counts: "));
                add(element("renderCount-label", new JLabel("Render: " + engine.getRenderCount())));
                add(element("updateCount-label", new JLabel("| Update: " + engine.getUpdateCount())));
                add(element("inputCount-label", new JLabel("| Input: " + engine.getInputCount())));
            }});
            add(new JSeparator());
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                add(element("totalFrameLoss-label", new JLabel("Total Frame Loss: " + engine.getTotalFrameLoss())));
                add(element("pureTotalFrameLoss-label", new JLabel("| Total Pure Frame Loss: " + engine.getPureTotalFrameLoss())));
            }});
            add(new JSeparator());
            add(new JPanel(new GridLayout(0, 1)) {{
                setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                add(element("cameraPosition-label", new JLabel("Camera Position: " + engine.getCamera().getPosition())));
                add(element("cameraRotation-label", new JLabel("Camera Rotation: " + engine.getCamera().getRotation())));
            }});
            add(new JSeparator());
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                add(new JLabel("Update Sampling Rate (ms): "));
                add(new JTextField(String.valueOf(updateRate)) {{
                    setPreferredSize(new Dimension(60, 30));
                    addActionListener(e -> {
                        try {
                            setUpdateRate(Integer.parseInt(getText()));
                        } catch (NumberFormatException ignore) {}
                        setText(String.valueOf(updateRate));
                    });
                }});
                add(element("updateLatency-label", new JLabel("Latency: " + updateLatency + " ms")));
                add(new JButton("Pause Monitoring") {{
                    setForeground(Color.GREEN);
                    setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
                    addActionListener(e -> {
                        var running = getForeground().equals(Color.GREEN);
                        if (getForeground().equals(Color.GREEN)) stopUpdater(); else startUpdater();
                        setForeground(running ? Color.RED : Color.GREEN);
                        setText(running ? "Resume Monitoring" : "Pause Monitoring");
                    });
                }});
            }});
            add(new JSeparator());
            add(new JPanel(new GridLayout(0, 1)) {{
                add(element("engineTimer-label", new JLabel("Engine Timer: " + engine.getTimer()) {{
                    setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                    setHorizontalTextPosition(SwingConstants.RIGHT);
                }}));
                add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                    add(new JButton("Pause Engine") {{
                        setForeground(Color.GREEN);
                        addActionListener(e -> {
                            var pause = getForeground().equals(Color.GREEN);
                            setText(pause ? "Resume Engine" : "Pause Engine");
                            setForeground(pause ? Color.RED : Color.GREEN);
                            if (pause) engine.stop(); else engine.start();
                        });
                    }});
                    add(new JButton("Pause Render") {{
                        setForeground(Color.GREEN);
                        addActionListener(e -> {
                            var pause = getForeground().equals(Color.GREEN);
                            setText(pause ? "Resume Render" : "Pause Render");
                            setForeground(pause ? Color.RED : Color.GREEN);
                            engine.setDoRender(!pause);
                        });
                    }});
                    add(new JButton("Pause Update") {{
                        setForeground(Color.GREEN);
                        addActionListener(e -> {
                            var pause = getForeground().equals(Color.GREEN);
                            setText(pause ? "Resume Update" : "Pause Update");
                            setForeground(pause ? Color.RED : Color.GREEN);
                            engine.setDoUpdate(!pause);
                        });
                    }});
                    add(new JButton("Pause Input") {{
                        setForeground(Color.GREEN);
                        addActionListener(e -> {
                            var pause = getForeground().equals(Color.GREEN);
                            setText(pause ? "Resume Update" : "Pause Input");
                            setForeground(pause ? Color.RED : Color.GREEN);
                            engine.setDoInput(!pause);
                        });
                    }});
                }});
            }});
        }}).setLayout(new BoxLayout(element("main"), BoxLayout.Y_AXIS));
        add(new JScrollPane(element("main")));
        startUpdater();
    }

    private void startUpdater() {
        if (updateFuture != null)
            return;
        updateFuture = updater.scheduleAtFixedRate(this::updateElements, 0, updateRate, TimeUnit.MILLISECONDS);
    }

    private void stopUpdater() {
        if (updateFuture == null)
            return;
        updateFuture.cancel(true);
        updateFuture = null;
    }

    private void setUpdateRate(int millis) {
        updateRate = millis;
        stopUpdater();
        startUpdater();
    }

    @Override
    protected void updateElements() {
        var t = System.nanoTime();
        labelE("fps-label").setText("FPS: " + Utils.round(engine.getFps(), 2));
        labelE("ups-label").setText("| UPS: " + Utils.round(engine.getUps(), 2));
        labelE("ips-label").setText("| IPS: " + Utils.round(engine.getIps(), 2));

        labelE("renderTime-label").setText("Render: " + Utils.round(engine.getRenderTime(), 2) + " ms");
        labelE("updateTime-label").setText("| Update: " + Utils.round(engine.getUpdateTime(), 2) + " ms");
        labelE("inputTime-label").setText("| Input: " + Utils.round(engine.getInputTime(), 2) + " ms");
        ((MultiGraphPanelForSampling) element("latencies-graph")).update();

        labelE("frameLoss-label").setText("Frame Loss: " + engine.getFrameLoss());
        labelE("cpuUsage-label").setText("| CPU Usage: " + Utils.round(Utils.cpuUsageByJVM() * 100, 2) + "%");
        labelE("heapUsage-label").setText("| Heap Usage: " + Utils.round(Utils.usedHeapSize() / (float) Constants.MEGA, 2) + " MB");

        labelE("renderCount-label").setText("Render: " + engine.getRenderCount());
        labelE("updateCount-label").setText("| Update: " + engine.getUpdateCount());
        labelE("inputCount-label").setText("| Input: " + engine.getInputCount());

        labelE("totalFrameLoss-label").setText("Total Frame Loss: " + engine.getTotalFrameLoss());
        labelE("pureTotalFrameLoss-label").setText("| Total Pure Frame Loss: " + engine.getPureTotalFrameLoss());

        labelE("cameraPosition-label").setText("Camera Position: " + engine.getCamera().getPosition());
        labelE("cameraRotation-label").setText("Camera Rotation: " + engine.getCamera().getRotation());

        ((MultiGraphPanelForSampling) element("resourceUsage-graph")).update();

        labelE("updateLatency-label").setText("Latency: " + Utils.round(updateLatency, 2) + " ms");

        labelE("engineTimer-label").setText("Engine Timer: " + engine.getTimer());

        updateLatency = (System.nanoTime() - t) / (float) Constants.MILLION;
    }

    @Override
    protected void closeAction() {
        stopUpdater();
        gotoSystemTray();
    }

    @Override
    protected void gotoSystemTray() {
        stopUpdater();
        super.gotoSystemTray();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) startUpdater(); else stopUpdater();
    }
}
