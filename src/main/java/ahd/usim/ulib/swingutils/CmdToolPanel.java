package ahd.usim.ulib.swingutils;

import ahd.usim.ulib.utils.Utils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CmdToolPanel extends ElementBasedPanel {

    public CmdToolPanel() {
        init();
    }

    protected void init() {
        setLayout(new BorderLayout());

        add(new JPanel(new BorderLayout()) {{
            add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, new RTextScrollPane(element("commandInput-textArea", new RSyntaxTextArea() {{
                setSyntaxEditingStyle(SYNTAX_STYLE_WINDOWS_BATCH);
                setAntiAliasingEnabled(true);
                setMinimumSize(new Dimension(280, 125));
                try {
                    //noinspection SpellCheckingInspection
                    Theme.load(getClass().getResourceAsStream("/org/fife/ui/rsyntaxtextarea/themes/dark.xml")).apply(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                addKeyListener(new KeyAdapter() {
                    private boolean working = false;
                    private SwingWorker<?, ?> worker;
                    private static final String waitingMessage = "\n Waiting For Response...";

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_F10 && !working) {
                            worker = new SwingWorker<String, Void>() {
                                @Override
                                protected String doInBackground() {
                                    setEditable(false);
                                    working = true;
                                    append(waitingMessage);
                                    try {
                                        return Utils.doCMD(getText().substring(0, getText().length() - waitingMessage.length()));
                                    } catch (IOException ex) {
                                        return ex.toString();
                                    }
                                }

                                @Override
                                protected void done() {
                                    setText(getText().substring(0, getText().length() - waitingMessage.length()));
                                    try {
                                        textAreaE("commandOutput-textArea").append("\n-->\n" + get());
                                    } catch (InterruptedException | ExecutionException ex) {
                                        textAreaE("commandOutput-textArea").append("\n-->\n" + ex);
                                    }
                                    setEditable(true);
                                    working = false;
                                }
                            };
                            worker.execute();
                        } else if (e.isControlDown() && working && e.getKeyCode() == KeyEvent.VK_F2) {
                            worker.cancel(false);
                        }
                    }
                });
                var font = getFont();
                setFont(new Font(font.getFontName(), font.getStyle(), 14));
            }})) {{
                setAutoscrolls(true);
            }}, element("commandOutputScrollPane-scrollPane", new JScrollPane(element("commandOutput-textArea", new JTextArea() {{
                setLineWrap(true);
                setEditable(false);
            }})) {{
                setAutoscrolls(true);
            }})) {{
                setDividerLocation(0.3);
            }}, BorderLayout.CENTER);
        }}, BorderLayout.CENTER);
    }
}
