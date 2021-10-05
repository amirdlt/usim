package ahd.ulib.swingutils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EnTextField extends JTextField {
    private String defaultString;
    private boolean isDark;
    private Color majorForeground;
    private Color emptyForeground;

    public EnTextField(String defaultString, boolean isDark) {
        this.defaultString = defaultString;
        this.isDark = isDark;
        init();
    }

    private void init() {
        majorForeground = getForeground();
        emptyForeground = isDark ? majorForeground.darker().darker() : majorForeground.brighter().brighter();
        setText(defaultString);
        setForeground(emptyForeground);
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(defaultString)) {
                    setCaretPosition(0);
                    return;
                }
                setText("");
                setForeground(majorForeground);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().equals(defaultString) || getText().replace(" ", "").isEmpty()) {
                    setText(defaultString);
                    setForeground(emptyForeground);
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (getText().equals(defaultString)) {
                    setForeground(majorForeground);
                    setText("");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && getText().isEmpty()) {
                    setText(defaultString);
                    setForeground(emptyForeground);
                    setCaretPosition(0);
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (getText().equals(defaultString))
                    setCaretPosition(0);
            }
        });

        setCaretPosition(0);
    }

    public String getDefaultString() {
        return defaultString;
    }

    public void setDefaultString(String defaultString) {
        this.defaultString = defaultString;
        init();
        repaint();
        revalidate();
    }

    public boolean isDark() {
        return isDark;
    }

    public void setDark(boolean dark) {
        isDark = dark;
        init();
        repaint();
        revalidate();
    }
}
