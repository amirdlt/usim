package com.usim.ulib.swingutils;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class MainPanel extends JPanel implements ElementBaseContainer {
    private final Map<String, JComponent> elements;

    public MainPanel() {
        elements = new HashMap<>();
    }

    @Override
    public Map<String, JComponent> elements() {
        return elements;
    }
}
