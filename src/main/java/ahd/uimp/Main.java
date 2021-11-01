package ahd.uimp;

import ahd.ulib.swingutils.ElementBasedPanel;
import ahd.ulib.swingutils.MainFrame;
import ahd.ulib.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static void main(String[] args) {
        var message = "Hello, Implementation world";
        System.out.println(message);
    }
}
