package ahd.ulib.swingutils;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class JSyntaxTextArea extends RSyntaxTextArea {
    private JScrollPane scrollPaned = null;

    // syntax from SyntaxConstants
    public JSyntaxTextArea(String syntax) {
        setCodeFoldingEnabled(true);
        setSyntaxEditingStyle(syntax);
        setAntiAliasingEnabled(true);
        try {
            //noinspection SpellCheckingInspection
            Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml")).apply(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        var font = getFont();
        setFont(new Font(font.getFontName(), font.getStyle(), 14));
    }

    public JSyntaxTextArea() {
        this(SYNTAX_STYLE_JAVA);
    }

    public JScrollPane getScrollPaned() {
        return scrollPaned == null ? scrollPaned = new RTextScrollPane(this) : scrollPaned;
    }
}
