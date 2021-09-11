package com.usim.engine.swing;

import com.usim.engine.Constants;
import com.usim.engine.internal.Engine;
import com.usim.ulib.swingutils.MainFrame;
import com.usim.ulib.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45C.*;

public class EngineRuntimeToolsFrame extends MainFrame {
    private final Engine engine;
    private final ScheduledThreadPoolExecutor updater;
    private ScheduledFuture<?> updateFuture;
    private int updateRate;
    private float updateLatency;
    private int updateCount;
    private final List<MultiGraphPanelForSampling> graphs;

    public EngineRuntimeToolsFrame() {
        super("AHD:: Engine Runtime Tools");
        engine = Engine.getEngine();
        updater = new ScheduledThreadPoolExecutor(1);
        updateFuture = null;
        updateRate = 500;
        graphs = new ArrayList<>();
        updateLatency = Float.NaN;
        updateCount = 0;
        init();
    }

    private void init() {
        setSize(600, 650);
        setTrayIconPath(Constants.DEFAULT_SWING_ICON_PATH);
        setIconImage(new ImageIcon(Constants.DEFAULT_SWING_ICON_PATH).getImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(new JPanel(new BorderLayout()) {{
            add(element("main-tabbedPane", new JTabbedPane() {{
                add("Stats", new JScrollPane(element("stats-panel", new JPanel() {{
                    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                    setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                    add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                        add(new JLabel("Targets: FPS: "));
                        add(element("fps-textField", new JTextField(String.valueOf(engine.getTargetFps())) {{
                            setPreferredSize(new Dimension(80, 30));
                            addActionListener(e -> {
                                try {
                                    engine.setTargetFps(Integer.parseInt(getText()));
                                } catch (NumberFormatException ignore) {
                                }
                                setText(String.valueOf(engine.getTargetFps()));
                            });
                        }}));
                        add(new JLabel("UPS: "));
                        add(element("ups-textField", new JTextField(String.valueOf(engine.getTargetUps())) {{
                            setPreferredSize(new Dimension(80, 30));
                            addActionListener(e -> {
                                try {
                                    engine.setTargetUps(Integer.parseInt(getText()));
                                } catch (NumberFormatException ignore) {
                                }
                                setText(String.valueOf(engine.getTargetUps()));
                                textFieldE("fps-textField").setText(String.valueOf(engine.getTargetFps()));
                                textFieldE("ips-textField").setText(String.valueOf(engine.getTargetIps()));
                            });
                        }}));
                        add(new JLabel("IPS: "));
                        add(element("ips-textField", new JTextField(String.valueOf(engine.getTargetIps())) {{
                            setPreferredSize(new Dimension(80, 30));
                            addActionListener(e -> {
                                try {
                                    engine.setTargetIps(Integer.parseInt(getText()));
                                } catch (NumberFormatException ignore) {
                                }
                                setText(String.valueOf(engine.getTargetIps()));
                            });
                        }}));
                    }});
                    add(new JSeparator());
                    add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                        add(new JLabel("Real Time: "));
                        add(element("fps-label", new JLabel("FPS: " + Utils.round(engine.getFps(), 2)) {{
                            setForeground(Color.RED);
                        }}));
                        add(element("ups-label", new JLabel("| UPS: " + Utils.round(engine.getUps(), 2)) {{
                            setForeground(Color.GREEN);
                        }}));
                        add(element("ips-label", new JLabel("| IPS: " + Utils.round(engine.getIps(), 2)) {{
                            setForeground(Color.BLUE);
                        }}));
                        add(new JCheckBox("Graph") {{
                            setSelected(true);
                            addActionListener(e -> element("ps-graph").setVisible(isSelected()));
                        }});
                    }});
                    add(element("ps-graph", new MultiGraphPanelForSampling(350, 120) {{
                        addGraph("fps", 2, Color.RED, engine::getFps);
                        addGraph("ups", 2, Color.GREEN, engine::getUps);
                        addGraph("ips", 2, Color.BLUE, engine::getIps);
                    }}));
                    add(new JSeparator());
                    add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                        add(new JLabel("Accumulated: "));
                        add(element("fpsAccumulated-label", new JLabel("FPS: " + Utils.round(engine.getAccumulatedFps(), 2)) {{
                            setForeground(Color.RED);
                        }}));
                        add(element("upsAccumulated-label", new JLabel("| UPS: " + Utils.round(engine.getAccumulatedUps(), 2)) {{
                            setForeground(Color.GREEN);
                        }}));
                        add(element("ipsAccumulated-label", new JLabel("| IPS: " + Utils.round(engine.getAccumulatedIps(), 2)) {{
                            setForeground(Color.BLUE);
                        }}));
                        add(new JCheckBox("Graph") {{
                            setSelected(true);
                            addActionListener(e -> element("accumulatedPs-graph").setVisible(isSelected()));
                        }});
                    }});
                    add(element("accumulatedPs-graph", new MultiGraphPanelForSampling(350, 120) {{
                        addGraph("accumulatedFps", 2, Color.RED, engine::getAccumulatedFps);
                        addGraph("accumulatedUps", 2, Color.GREEN, engine::getAccumulatedUps);
                        addGraph("accumulatedIps", 2, Color.BLUE, engine::getAccumulatedIps);
                    }}));
                    add(new JSeparator());
                    add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                        add(new JLabel("Counts: "));
                        add(element("renderCount-label", new JLabel("Render: " + engine.getRenderCount()) {{
                            setForeground(Color.RED);
                        }}));
                        add(element("updateCount-label", new JLabel("| Update: " + engine.getUpdateCount()) {{
                            setForeground(Color.GREEN);
                        }}));
                        add(element("inputCount-label", new JLabel("| Input: " + engine.getInputCount()) {{
                            setForeground(Color.BLUE);
                        }}));
                        add(element("totalFrameLoss-label", new JLabel("| Frame Loss: " + engine.getTotalFrameLoss()) {{
                            setForeground(Color.YELLOW);
                        }}));
                        add(element("pureTotalFrameLoss-label", new JLabel("| Pure Frame Loss: " + engine.getPureTotalFrameLoss()) {{
                            setForeground(Color.CYAN);
                        }}));
                        add(new JCheckBox("Graph") {{
                            setSelected(true);
                            addActionListener(e -> element("counts-graph").setVisible(isSelected()));
                        }});
                    }});
                    add(element("counts-graph", new MultiGraphPanelForSampling(350, 120) {{
                        addGraph("renderCount", 2, Color.RED, engine::getRenderCount);
                        addGraph("update", 2, Color.GREEN, engine::getUpdateCount);
                        addGraph("inputCount", 2, Color.BLUE, engine::getInputCount);
                        addGraph("totalFrameLossCount", 2, Color.YELLOW, engine::getTotalFrameLoss);
                        addGraph("totalPureFrameLossCount", 2, Color.CYAN, engine::getPureTotalFrameLoss);
                    }}));
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
                        add(new JCheckBox("Graph") {{
                            setSelected(true);
                            addActionListener(e -> element("latencies-graph").setVisible(isSelected()));
                        }});
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
                        add(new JCheckBox("Graph") {{
                            setSelected(true);
                            addActionListener(e -> element("resourceUsage-graph").setVisible(isSelected()));
                        }});
                    }});
                    add(element("resourceUsage-graph", new MultiGraphPanelForSampling(350, 120) {{
                        addGraph("cpuUsage", 2, Color.RED, Utils::cpuUsageByJVM);
                    }}));
                    add(new JSeparator());
                    add(new JPanel(new GridLayout(0, 1)) {{
                        setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                        add(element("cameraPosition-label", new JLabel("Camera Position: " + engine.getCamera().getPosition())));
                        add(element("cameraRotation-label", new JLabel("Camera Rotation: " + engine.getCamera().getRotation())));
                    }});
                    add(new JSeparator());
                    add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                        add(new JLabel("Sampling Rate (ms): "));
                        add(new JTextField(String.valueOf(updateRate)) {{
                            setPreferredSize(new Dimension(60, 30));
                            addActionListener(e -> {
                                try {
                                    setUpdateRate(Integer.parseInt(getText()));
                                } catch (NumberFormatException ignore) {
                                }
                                setText(String.valueOf(updateRate));
                            });
                        }});
                        add(element("updateLatency-label", new JLabel("Latency: " + updateLatency + "ms")));
                        add(element("sampleCount-label", new JLabel("| Sample Counts: " + updateCount)));
                        add(new JPanel(new GridLayout(0, 1)) {{
                            add(new JButton("Pause Monitoring") {{
                                setForeground(Color.GREEN);
                                setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
                                addActionListener(e -> {
                                    var running = getForeground().equals(Color.GREEN);
                                    if (getForeground().equals(Color.GREEN))
                                        stopUpdater();
                                    else
                                        startUpdater();
                                    setForeground(running ? Color.RED : Color.GREEN);
                                    setText(running ? "Resume Monitoring" : "Pause Monitoring");
                                });
                            }});
                            add(new JButton("Clear Graphs") {{
                                addActionListener(
                                        e -> elements(MultiGraphPanelForSampling.class).forEach(MultiGraphPanelForSampling::reset));
                            }});
                        }});
                    }});
                    add(new JSeparator());
                    add(new JPanel(new GridLayout(0, 1)) {{
                        add(element("engineTimer-label", new JLabel("Engine Timer: " + engine.getTimer(), JLabel.CENTER) {{
                            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                        }}));
                        add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                            add(new JButton("Pause Engine") {{
                                setForeground(Color.GREEN);
                                addActionListener(e -> {
                                    var pause = getForeground().equals(Color.GREEN);
                                    setText(pause ? "Resume Engine" : "Pause Engine");
                                    setForeground(pause ? Color.RED : Color.GREEN);
                                    if (pause)
                                        engine.stop();
                                    else
                                        engine.start();
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
                                    setText(pause ? "Resume Input" : "Pause Input");
                                    setForeground(pause ? Color.RED : Color.GREEN);
                                    engine.setDoInput(!pause);
                                });
                            }});
                        }});
                    }});
                }})));
                add("Engine Settings", new JScrollPane(element("engineSettings-panel", new JPanel() {{
                    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                    add(ComponentBuilder.createFunctionCallerPanel("glPolygonMode",
                            args -> engine.commitCommandsToMainThread(() -> glPolygonMode((int) args.get("face"), (int) args.get("mode"))),
                            () -> showErrorDialog("Bad Argument(s)"),
                            new ComponentBuilder.OptionBasedArg<>("face", Map.of("GL_FRONT", GL_FRONT, "GL_BACK", GL_BACK, "GL_FRONT_AND_BACK", GL_FRONT_AND_BACK)),
                            new ComponentBuilder.OptionBasedArg<>("mode", Map.of("GL_POINT", GL_POINT, "GL_LINE", GL_LINE, "GL_FILL", GL_FILL))));
                    add(ComponentBuilder.createFunctionCallerPanel("glEnable & glDisable", args -> engine.commitCommandsToMainThread(() -> {
                                if ((boolean) args.get("flag"))
                                    glEnable((int) args.get("state"));
                                else
                                    glDisable((int) args.get("state"));
                            }), () -> showErrorDialog("Bad Argument(s)"),
                            new ComponentBuilder.OptionBasedArg<>("state", new HashMap<>() {{
                                put("GL_BLEND", GL_BLEND);
                                put("GL_CLIP_DISTANCE0", GL_CLIP_DISTANCE0);
                                put("GL_COLOR_LOGIC_OP", GL_COLOR_LOGIC_OP);
                                put("GL_CULL_FACE", GL_CULL_FACE);
                                put("GL_DEBUG_OUTPUT", GL_DEBUG_OUTPUT);
                                put("GL_DEBUG_OUTPUT_SYNCHRONOUS", GL_DEBUG_OUTPUT_SYNCHRONOUS);
                                put("GL_DEPTH_CLAMP", GL_DEPTH_CLAMP);
                                put("GL_DEPTH_TEST", GL_DEPTH_TEST);
                                put("GL_DITHER", GL_DITHER);
                                put("GL_FRAMEBUFFER_SRGB", GL_FRAMEBUFFER_SRGB);
                                put("GL_LINE_SMOOTH", GL_LINE_SMOOTH);
                                put("GL_MULTISAMPLE", GL_MULTISAMPLE);
                                put("GL_POLYGON_OFFSET_FILL", GL_POLYGON_OFFSET_FILL);
                                put("GL_POLYGON_OFFSET_LINE", GL_POLYGON_OFFSET_LINE);
                                put("GL_POLYGON_OFFSET_POINT", GL_POLYGON_OFFSET_POINT);
                                put("GL_POLYGON_SMOOTH", GL_POLYGON_SMOOTH);
                                put("GL_PRIMITIVE_RESTART", GL_PRIMITIVE_RESTART);
                                put("GL_PRIMITIVE_RESTART_FIXED_INDEX", GL_PRIMITIVE_RESTART_FIXED_INDEX);
                                put("GL_RASTERIZER_DISCARD", GL_RASTERIZER_DISCARD);
                                put("GL_SAMPLE_ALPHA_TO_COVERAGE", GL_SAMPLE_ALPHA_TO_COVERAGE);
                                put("GL_SAMPLE_ALPHA_TO_ONE", GL_SAMPLE_ALPHA_TO_ONE);
                                put("GL_SAMPLE_COVERAGE", GL_SAMPLE_COVERAGE);
                                put("GL_SAMPLE_SHADING", GL_SAMPLE_SHADING);
                                put("GL_SAMPLE_MASK", GL_SAMPLE_MASK);
                                put("GL_SCISSOR_TEST", GL_SCISSOR_TEST);
                                put("GL_STENCIL_TEST", GL_STENCIL_TEST);
                                put("GL_TEXTURE_CUBE_MAP_SEAMLESS", GL_TEXTURE_CUBE_MAP_SEAMLESS);
                                put("GL_PROGRAM_POINT_SIZE", GL_PROGRAM_POINT_SIZE);
                            }}),
                            new ComponentBuilder.OptionBasedArg<>("flag", Map.of("Enable", true, "Disable", false))));
                    add(ComponentBuilder.createFunctionCallerPanel("glLineWidth",
                            args -> engine.commitCommandsToMainThread(() -> glLineWidth(((Double) args.get("width")).floatValue())),
                            () -> showErrorDialog("Bad Arg(s)"),
                            new ComponentBuilder.NumberBasedArg<>("width", 0, Float.MAX_VALUE)));
                    add(ComponentBuilder.createFunctionCallerPanel("glfwSwapIntervals",
                            args -> engine.commitCommandsToMainThread(() -> glfwSwapInterval(((Double) args.get("interval")).intValue())),
                            () -> showErrorDialog("Bad Argument"), new ComponentBuilder.NumberBasedArg<>("interval", 0, Integer.MAX_VALUE)));
                    add(ComponentBuilder.createFunctionCallerPanel("glfwWindowHint",
                            args -> engine.commitCommandsToMainThread(() -> {
                                glfwWindowHint((int) args.get("hint"), (int) args.get("value"));
                                engine.rebuildWindow();
                            }),
                            () -> showErrorDialog("Bad Argument(s)"),
                            new ComponentBuilder.OptionBasedArg<>("hint", new HashMap<>() {{
                                put("GLFW_RESIZABLE", GLFW_RESIZABLE);
                                put("GLFW_VISIBLE", GLFW_VISIBLE);
                                put("GLFW_DECORATED", GLFW_DECORATED);
                                put("GLFW_FOCUSED", GLFW_FOCUSED);
                                put("GLFW_AUTO_ICONIFY", GLFW_AUTO_ICONIFY);
                                put("GLFW_FLOATING", GLFW_FLOATING);
                                put("GLFW_MAXIMIZED", GLFW_MAXIMIZED);
                                put("GLFW_CENTER_CURSOR", GLFW_CENTER_CURSOR);
                                put("GLFW_TRANSPARENT_FRAMEBUFFER", GLFW_TRANSPARENT_FRAMEBUFFER);
                                put("GLFW_FOCUS_ON_SHOW", GLFW_FOCUS_ON_SHOW);
                                put("GLFW_SCALE_TO_MONITOR", GLFW_SCALE_TO_MONITOR);
                                put("GLFW_STEREO", GLFW_STEREO);
                                put("GLFW_SRGB_CAPABLE", GLFW_SRGB_CAPABLE);
                                put("GLFW_DOUBLEBUFFER", GLFW_DOUBLEBUFFER);
                                put("GLFW_CONTEXT_NO_ERROR", GLFW_CONTEXT_NO_ERROR);
                                put("GLFW_OPENGL_FORWARD_COMPAT", GLFW_OPENGL_FORWARD_COMPAT);
                                put("GLFW_OPENGL_DEBUG_CONTEXT", GLFW_OPENGL_DEBUG_CONTEXT);
                                put("GLFW_COCOA_RETINA_FRAMEBUFFER", GLFW_COCOA_RETINA_FRAMEBUFFER);
                                put("GLFW_COCOA_GRAPHICS_SWITCHING", GLFW_COCOA_GRAPHICS_SWITCHING);
                            }}),
                            new ComponentBuilder.OptionBasedArg<>("value", Map.of("TRUE", GLFW_TRUE, "FALSE", GLFW_FALSE))));
                    add(ComponentBuilder.createFunctionCallerPanel("glfwWindowHint",
                            args -> engine.commitCommandsToMainThread(() -> {
                                glfwWindowHint((int) args.get("hint"), ((Double) args.get("value")).intValue());
                                engine.rebuildWindow();
                            }),
                            () -> showErrorDialog("Bad Argument(s)"),
                            new ComponentBuilder.OptionBasedArg<>("hint", new HashMap<>() {{
                                put("GLFW_RED_BITS", GLFW_RED_BITS);
                                put("GLFW_GREEN_BITS", GLFW_GREEN_BITS);
                                put("GLFW_BLUE_BITS", GLFW_BLUE_BITS);
                                put("GLFW_ALPHA_BITS", GLFW_ALPHA_BITS);
                                put("GLFW_DEPTH_BITS", GLFW_DEPTH_BITS);
                                put("GLFW_STENCIL_BITS", GLFW_STENCIL_BITS);
                                put("GLFW_ACCUM_RED_BITS", GLFW_ACCUM_RED_BITS);
                                put("GLFW_ACCUM_GREEN_BITS", GLFW_ACCUM_GREEN_BITS);
                                put("GLFW_ACCUM_BLUE_BITS", GLFW_ACCUM_BLUE_BITS);
                                put("GLFW_ACCUM_ALPHA_BITS", GLFW_ACCUM_ALPHA_BITS);
                                put("GLFW_AUX_BUFFERS", GLFW_AUX_BUFFERS);
                                put("GLFW_SAMPLES", GLFW_SAMPLES);
                                put("GLFW_REFRESH_RATE", GLFW_REFRESH_RATE);
                            }}),
                            new ComponentBuilder.NumberBasedArg<>("value", 0, Integer.MAX_VALUE)));
                    add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                        add(new JButton("Turn off") {{addActionListener(e -> engine.turnoff());}});
                    }});
                }})));
                add("Entity", new JScrollPane(ComponentBuilder.createEntityPanel(EngineRuntimeToolsFrame.this)));
            }}), BorderLayout.CENTER);
        }});
        graphs.addAll(elements(MultiGraphPanelForSampling.class));
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
    public void updateElements() {
        var t = System.nanoTime();

        graphs.forEach(MultiGraphPanelForSampling::update);

        labelE("fps-label").setText("FPS: " + Utils.round(engine.getFps(), 2));
        labelE("ups-label").setText("| UPS: " + Utils.round(engine.getUps(), 2));
        labelE("ips-label").setText("| IPS: " + Utils.round(engine.getIps(), 2));

        labelE("fpsAccumulated-label").setText("FPS: " + Utils.round(engine.getAccumulatedFps(), 2));
        labelE("upsAccumulated-label").setText("| UPS: " + Utils.round(engine.getAccumulatedUps(), 2));
        labelE("ipsAccumulated-label").setText("| IPS: " + Utils.round(engine.getAccumulatedIps(), 2));

        labelE("renderTime-label").setText("Render: " + Utils.round(engine.getRenderTime(), 2) + " ms");
        labelE("updateTime-label").setText("| Update: " + Utils.round(engine.getUpdateTime(), 2) + " ms");
        labelE("inputTime-label").setText("| Input: " + Utils.round(engine.getInputTime(), 2) + " ms");

        labelE("frameLoss-label").setText("Frame Loss: " + engine.getFrameLoss());
        labelE("cpuUsage-label").setText("| CPU Usage: " + Utils.round(Utils.cpuUsageByJVM() * 100, 2) + "%");
        labelE("heapUsage-label").setText("| Heap Usage: " + Utils.round(Utils.usedHeapSize() / (float) Constants.MEGA, 2) + " MB");

        labelE("renderCount-label").setText("Render: " + engine.getRenderCount());
        labelE("updateCount-label").setText("| Update: " + engine.getUpdateCount());
        labelE("inputCount-label").setText("| Input: " + engine.getInputCount());
        labelE("totalFrameLoss-label").setText("| Frame Loss: " + engine.getTotalFrameLoss());
        labelE("pureTotalFrameLoss-label").setText("| Pure Frame Loss: " + engine.getPureTotalFrameLoss());


        labelE("cameraPosition-label").setText("Camera Position: " + engine.getCamera().getPosition());
        labelE("cameraRotation-label").setText("Camera Rotation: " + engine.getCamera().getRotation());

        labelE("updateLatency-label").setText("Latency: " + Utils.round(updateLatency, 2) + " ms");
        labelE("sampleCount-label").setText("| Sample Counts: " + ++updateCount);

        labelE("engineTimer-label").setText("Engine Timer: " + engine.getTimer());

        updateLatency = (System.nanoTime() - t) / Constants.MILLION_F;
    }

    @Override
    protected void closeAction() {
        stopUpdater();
        gotoSystemTray();
    }

    @Override
    public void gotoSystemTray() {
        stopUpdater();
        super.gotoSystemTray();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) startUpdater(); else stopUpdater();
    }
}
