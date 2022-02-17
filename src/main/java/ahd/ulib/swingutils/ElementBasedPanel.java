package ahd.ulib.swingutils;

import ahd.ulib.utils.api.ConfigBase;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ElementBasedPanel extends JPanel implements ElementBaseContainer, ConfigBase<String, Object> {
    private final Map<String, Object> elements;
    private final Map<String, Object> configMap;

    public ElementBasedPanel() {
        super(new BorderLayout());
        elements = new HashMap<>();
        configMap = new HashMap<>();
    }

    protected void init() {}

    public void showErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showErrorDialog(String message) {
        showErrorDialog(message, "Error");
    }

    public void showErrorDialog() {
        showErrorDialog("Error", "Error");
    }

    @Override
    public Map<String, Object> elements() {
        return elements;
    }

    @Override
    public Map<String, Object> config() {
        return configMap;
    }
}
