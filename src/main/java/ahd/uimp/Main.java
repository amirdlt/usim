package ahd.uimp;

import ahd.ulib.swingutils.ElementBasedPanel;
import ahd.ulib.swingutils.MainFrame;
import ahd.ulib.utils.Utils;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import net.miginfocom.swing.MigLayout;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Main {

    public static void sendRequestExample() {
        SwingUtilities.invokeLater(new MainFrame() {{
            setTitle("Test Http Request");
            add(element("main", new ElementBasedPanel() {{
                add(new JButton("send") {{
                    addActionListener(e -> {
                        try {
                            var client = HttpClient.newHttpClient();
                            var request = HttpRequest.newBuilder().uri(URI.create("http://www.amirdlt.ihweb.ir/assets/php/savetofile.php"))
                                    .POST(HttpRequest.BodyPublishers.ofString("This is me: ",
                                            StandardCharsets.UTF_8)).build();
                            System.out.println(client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode());
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                }});
            }}));
        }});
    }

    public static void main123(String[] args) throws Exception {
        var client = HttpClient.newHttpClient();
        int index = 0;
        while (index++ < 100) {
                client.send(HttpRequest.newBuilder().uri(URI.create("https://portal.aut.ac.ir/aportal/regadm/student.portal/student.portal.jsp?action=edit&st_info=register&st_sub_info=u_persian"))
                                .headers("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3", "Accept-Encoding",
                                        "gzip, deflate, br", "Accept-Language", "en-US,en;q=0.9,fa;q=0.8", "Cache-Control", "no-cache",
                                        "Pragma", "no-cache", "Referer", "https://portal.aut.ac.ir/aportal/", "sec-ch-ua",
                                        "\"Chromium\";v=\"94\", \"Microsoft Edge\";v=\"94\", \";Not A Brand\";v=\"99\"", "sec-ch-ua-mobile",
                                        "?0", "Cookie", "JSESSIONID=FC007BEA74654027B3FFE0DE406415CF; " + index, "sec-ch-ua-platform",
                                        "\"Windows\"", "Sec-Fetch-Dest", "image", "Sec-Fetch-Mode", "no-cors", "Sec-Fetch-Site", "same-origin",
                                        "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                                                + "Chrome/94.0.4606.81 Safari/537.36 Edg/94.0.992.50").GET().build(),
                        HttpResponse.BodyHandlers.ofString());
                client.send(HttpRequest.newBuilder().uri(URI.create("https://portal.aut.ac.ir/aportal/PassImageServlet/")).GET()
                                .headers("Accept", "image/webp,image/apng,image/*,*/*;q=0.8", "Accept-Encoding",
                                        "gzip, deflate, br", "Accept-Language", "en-US,en;q=0.9,fa;q=0.8", "Cache-Control", "no-cache",
                                        "Cookie", "JSESSIONID=FC007BEA74654027B3FFE0DE406415CF; " + index, "Pragma", "no-cache", "Referer",
                                        "https://portal.aut.ac.ir/aportal/", "sec-ch-ua",
                                        "\"Chromium\";v=\"94\", \"Microsoft Edge\";v=\"94\", \";Not A Brand\";v=\"99\"", "sec-ch-ua-mobile",
                                        "?0", "sec-ch-ua-platform", "\"Windows\"", "Sec-Fetch-Dest", "image", "Sec-Fetch-Mode", "no-cors",
                                        "Sec-Fetch-Site", "same-origin", "User-Agent",
                                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                                                + "Chrome/94.0.4606.81 Safari/537.36 Edg/94.0.992.50").build(),
                        HttpResponse.BodyHandlers.ofFile(Path.of(".\\out\\portal\\portalset2\\" + index + ".jpeg")));
        }
    }

    public static void main1(String[] args) throws IOException {
        var files = new File(".\\out\\portal\\portalset").listFiles();
        assert files != null;
        for (var file : files) {
            var t = System.currentTimeMillis();
            Utils.saveRenderedImage(Utils.readImage(file.getPath()), ".\\out\\portal\\portalsetpng\\" + file.getName().substring(0, 5),
                    "png");
            System.out.println(System.currentTimeMillis() - t);
        }
    }

    public static void main12(String[] args) throws IOException {
        Utils.saveRenderedImage(new BufferedImage(90, 40, BufferedImage.TYPE_INT_RGB) {{
            var g = createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 90, 40);
            g.dispose();
        }}, "armani", "jpeg");
    }

    public static void main69(String[] args) throws IOException {
        var list = new File(".\\out\\portal\\portalset2").listFiles();
        int index = 0;
        assert list != null;
        for (var file : list)
            Utils.saveRenderedImage(new BufferedImage(150, 40, BufferedImage.TYPE_INT_RGB) {{
                var g = createGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 150, 40);
                g.drawImage(Utils.readImage(file.getAbsolutePath()), 0, 0, 60, 40, null);
                g.dispose();
            }}, ".\\out\\portal\\portalset3\\" + ++index, "jpeg");
    }

    public static void main32(String[] args) {
        var message = "Hello, Implementation world";
        System.out.println(message);
    }

    public static void main65(String[] args) throws IOException {
        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            final HtmlPage page = webClient.getPage("https://htmlunit.sourceforge.io/");
            System.out.println(page.getTitleText());
        }
    }

    // script view
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MainFrame() {{
            add(element("mainTabbedPane", new JTabbedPane() {{
                add("ISAC", element("isacPanel", new JPanel(new BorderLayout()) {{
                    add(new JPanel(new GridLayout(0, 1)) {{
                        add(new JTabbedPane() {{
                            setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
                            add("Authentication", new JPanel(new BorderLayout()) {{
                                add(element("isacPanelAuthentication", new JTextArea()), BorderLayout.CENTER);
                            }});
                        }});
                        add(new JPanel(new GridLayout(0, 1)) {{
                            add(new JScrollPane(element("isacPanelResponse", new JTextArea())));
                        }});
                    }}, BorderLayout.CENTER);
                    add(new JPanel(new FlowLayout(FlowLayout.RIGHT)) {{
                        add(new JButton("Submit") {{
                            final var client = new WebClient();
                            addActionListener(e -> {
                                try {
                                    textAreaE("isacPanelResponse").setText(
                                            client.getPage(textFieldE("isacPanelURL").getText()).
                                                    getWebResponse().getContentAsString());
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            });
                        }});
                    }}, BorderLayout.NORTH);
                    add(new JPanel(new MigLayout()) {{
                        add(element("isacPanelURL", new JTextField()), "width 200px");
                    }}, BorderLayout.EAST);
                }}));
                add("Paint", element("paintPanel", new JPanel(new BorderLayout()) {
                    private final ArrayDeque<List<Point>> points = new ArrayDeque<>();
                    static record Config(Stroke stroke, Color color) {}
                    private final ArrayList<Config> configs = new ArrayList<>();

                    {
                        config("paintPanelPenColor", Color.WHITE);
                        config("paintPanelPenStroke", new BasicStroke(1.5f));
                        config("paintPanelPenState", "pen");
                        addMouseMotionListener(new MouseAdapter() {
                            @SuppressWarnings("ConstantConditions")
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                var list = points.peekLast();
                                var center = asManualTypeC("paintPanelPenPosition", Point.class);
                                var current = e.getPoint();
                                switch (asStringC("paintPanelPenState")) {
                                    case "pen" -> list.add(current);
                                    case "circle" -> {
                                        list.clear();
                                        var radius = center.distance(current);
                                        var x = -0.1;
                                        while ((x += 0.1) < 2 * Math.PI)
                                            list.add(new Point((int) (center.x + radius * Math.cos(x)), (int) (center.y + radius * Math.sin(x))));
                                        list.add(new Point(center.x + (int) radius, center.y));
                                    }
                                    case "rectangle" -> {
                                        list.clear();
                                        var sideA = 2 * (center.x - e.getX());
                                        var sideB = 2 * (center.y - e.getY());
                                        list.add(current);
                                        list.add(new Point(current.x, current.y + sideB));
                                        list.add(new Point(current.x + sideA, current.y + sideB));
                                        list.add(new Point(current.x + sideA, current.y));
                                        list.add(current);
                                    }
                                    case "triangle" -> {
                                        list.clear();
                                        var side = current.x - center.x + current.y - center.y;
                                        list.add(current);
                                        list.add(new Point(current.x + side / 2, current.y + side));
                                        list.add(new Point(current.x + side, current.y));
                                        list.add(current);
                                    }
                                    default -> {}
                                }
                                repaint();
                            }
                        });
                        addMouseListener(new MouseAdapter() {
                            @Override
                            public void mousePressed(MouseEvent e) {
                                if (e.getButton() != MouseEvent.BUTTON1)
                                    return;
                                points.add(new ArrayList<>());
                                configs.add(new Config(asManualTypeC("paintPanelPenStroke", Stroke.class),
                                        asManualTypeC("paintPanelPenColor", Color.class)));
                                config("paintPanelPenPosition", e.getPoint());
                            }
                        });
                        add(new JPanel(new FlowLayout(FlowLayout.LEFT)) {{
                            add(new JSlider(1, 1500) {{
                                setValue(60);
                                addChangeListener(e -> {
                                    config("paintPanelPenStroke", new BasicStroke(getValue() / 30.f));
                                    element("paintPanel").repaint();
                                });
                            }});
                            add(new JButton("Pen Color") {{
                                addActionListener(e -> {
                                    var color = JColorChooser.
                                            showDialog(element("paintPanel"),
                                                    "Pen Color", asManualTypeC("paintPanelPenColor", Color.class));
                                    config("paintPanelPenColor", color);
                                    setForeground(color);
                                    element("paintPanel").repaint();
                                });
                            }});
                            add(new JButton("Pen") {{
                                addActionListener(e -> config("paintPanelPenState", "pen"));
                            }});
                            add(new JButton("Circle") {{
                                addActionListener(e -> config("paintPanelPenState", "circle"));
                            }});
                            add(new JButton("Rectangle") {{
                                addActionListener(e -> config("paintPanelPenState", "rectangle"));
                            }});
                            add(new JButton("Triangle") {{
                                addActionListener(e -> config("paintPanelPenState", "triangle"));
                            }});
                            add(new JButton("Clear") {{
                                addActionListener(e -> {
                                    points.clear();
                                    element("paintPanel").repaint();
                                });
                            }});
                        }}, BorderLayout.SOUTH);
                    }

                    @Override
                    public void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        var g2d = (Graphics2D) g;
                        var oldStroke = g2d.getStroke();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        var count = 0;
                        for (var points : this.points) {
                            var conf = configs.get(count++);
                            g2d.setStroke(conf.stroke);
                            g2d.setColor(conf.color);
                            for (int i = 0; i < points.size() - 1; i++)
                                g2d.drawLine(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
                        }
                        g2d.setStroke(oldStroke);
                    }
                }));
            }}), BorderLayout.CENTER);
        }});
    }
}
