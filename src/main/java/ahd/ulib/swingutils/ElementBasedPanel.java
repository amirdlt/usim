package ahd.ulib.swingutils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ElementBasedPanel extends JPanel implements ElementBaseContainer {
    private final Map<String, JComponent> elements;

    public ElementBasedPanel() {
        super(new BorderLayout());
        elements = new HashMap<>();
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
    public Map<String, JComponent> elements() {
        return elements;
    }
}
