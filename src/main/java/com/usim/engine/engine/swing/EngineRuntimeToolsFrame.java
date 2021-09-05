package com.usim.engine.engine.swing;

import com.usim.engine.engine.internal.Engine;
import com.usim.ulib.swingutils.MainFrame;

import javax.swing.*;
import java.awt.*;

public class EngineRuntimeToolsFrame extends MainFrame {
    private final Engine engine;

    public EngineRuntimeToolsFrame() {
        super("AHD:: Engine Runtime Tools");
        engine = Engine.get();
        init();
    }

    private void init() {
        setSize(400, 650);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
            add(element("fps-textField", new JTextField(String.valueOf(engine.getTargetFps())) {{
                setPreferredSize(new Dimension(60, 30));
            }}));
            add(element("ups-textField", new JTextField(String.valueOf(engine.getTargetUps())) {{
                setPreferredSize(new Dimension(60, 30));
            }}));
            add(element("ips-textField", new JTextField(String.valueOf(engine.getTargetIps())) {{
                setPreferredSize(new Dimension(60, 30));
            }}));
            add(element("apply-button", new JButton("Apply") {{
                addActionListener(e -> {
                    try {
                        engine.setTargetFps(Integer.parseInt(textFieldE("fps-textField").getText()));
                        engine.setTargetUps(Integer.parseInt(textFieldE("ups-textField").getText()));
                        engine.setTargetIps(Integer.parseInt(textFieldE("ips-textField").getText()));
                    } catch (Exception ex) { ex.printStackTrace(); }
                    updateElements();
                });
            }}));
        }});
    }

    public void updateElements() {
        textFieldE("fps-textField").setText(String.valueOf(engine.getTargetFps()));
        textFieldE("ups-textField").setText(String.valueOf(engine.getTargetUps()));
        textFieldE("ips-textField").setText(String.valueOf(engine.getTargetIps()));
    }

    @Override
    protected void closeAction() {}
}
