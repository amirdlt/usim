package ahd.uimp;

import ahd.ulib.swingutils.MainFrame;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class LinkScraper {
    @SuppressWarnings("SpellCheckingInspection")
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MainFrame() {{
            Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
                if (event instanceof KeyEvent e && e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    closeAction();
            }, AWTEvent.KEY_EVENT_MASK);
            add(new JScrollPane(elementE("mainPanel", new JPanel(new MigLayout()) {{
                add(elementE("rootTextField", new JTextField("https://google.com")), "pushx, growx, wmax 90%");
                add(new JButton("AddXPath") {{
                    addActionListener(e -> {
                        panelE("mainPanel").
                                add(elementE("xpath" + configC("xpathCount", asIntOrElseC("xpathCount", 0) + 1),
                                        new JTextField("//a[@href]") {{
                                            setName(configC("xpathCount").toString());
                                        }}), "pushx, growx");
                        panelE("mainPanel").add(elementWithDummyTagE(new JButton("Remove") {{
                            final var id = "xpath" + asIntC("xpathCount");
                            addActionListener(ev -> {
                                removeE(id);
                                removeE(this);
//                                repaintAndRevalidateElementE("mainPanel");
                            });
                        }}), "wrap");
                    });
                }});
                add(elementE("scrape", new JButton("ScrapeLinks") {{
                    addActionListener(e -> {
//                        replaceFromE("mainPanel", "dummy-2", new JScrollPane(element("resultsTextArea", new JTextArea())), "wrap");
//                        replaceFromE("mainPanel", "dummy-3", new JScrollPane(element("resultsTextArea2", new JTextArea())), "wrap");
                        asManualTypeOrElseC("executor", Executors.newFixedThreadPool(1)).execute(() -> {
                            try (final var client = new WebClient()) {
                                buttonE("scrape").setEnabled(false);
                                var queue = new ArrayDeque<String>() {{
                                    var count = asIntOrElseC("xpathCount", 0);
                                    while (count > 0) {
                                        var xpath = textFieldE("xpath" + count--);
                                        if (xpath == null)
                                            continue;
                                        add(xpath.getText());
                                    }
                                }};
                                var roots = new ArrayDeque<String>() {{
                                    add(textFieldE("rootTextField").getText());
                                }};
                                var innerRoots = new ArrayList<String>();
                                while (queue.size() > 1) {
                                    final var xpath = queue.pollLast();
                                    while (!roots.isEmpty()) {
                                        final HtmlPage page = client.getPage(roots.pop());
                                        page.getByXPath(xpath).stream().map(a -> (HtmlAnchor) a).
                                                forEach(a -> innerRoots.add(a.getHrefAttribute()));
                                        textAreaE("resultsTextArea2").append("-".repeat(100) + "\n" + page.asNormalizedText());
                                    }
                                    roots.addAll(innerRoots);
                                    innerRoots.clear();
                                }
                                final var xpath = queue.isEmpty() ? "//a[@href]" : queue.pop();
                                while (!roots.isEmpty()) {
                                    final HtmlPage page = client.getPage(roots.pop());
                                    page.getByXPath(xpath).stream().map(a -> (HtmlAnchor) a).
                                            forEach(a -> textAreaE("resultsTextArea").append("\n" + a.getHrefAttribute()));
                                    textAreaE("resultsTextArea2").append("-".repeat(100) + "\n" + page.asXml());
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                buttonE("scrape").setEnabled(true);
                            }
                        });
                    });
                }}));
                add(new JButton("Clear") {{
                    addActionListener(e -> {
                        elementsE(JButton.class).stream().filter(b -> b.getText().equals("Remove") && b.getParent() == this.getParent()).forEach(b -> {
                            b.doClick();
                            removeE(b);
                        });
                        configC("xpathCount", 0);
                        textFieldE("rootTextField").setText("Root");
                    });
                }}, "wrap");
            }})));
        }});
    }
}
