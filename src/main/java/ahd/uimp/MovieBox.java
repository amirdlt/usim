package ahd.uimp;

import ahd.ulib.swingutils.JSyntaxTextArea;
import ahd.ulib.swingutils.MainFrame;
import ahd.ulib.utils.mapper.StringMapper;
import com.google.gson.*;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MovieBox {
    public static void main(String[] args) throws IOException, InterruptedException {
        SwingUtilities.invokeLater(new MainFrame() {{
            configC("client", HttpClient.newHttpClient());
            configC("gson", new GsonBuilder().setPrettyPrinting().create());
            configC("url", new TreeSet<String>());
            configC("title", new TreeSet<String>());
            configC("pretty", (StringMapper) json -> asManualTypeC("gson", Gson.class).toJson(JsonParser.parseString(json)));
            configC("api.search", (StringMapper) query -> String.
                    format("https://movieboxapp.ir/api/search/%s/a/", URLEncoder.encode(query, StandardCharsets.UTF_8)));
            configC("api.getMovie", (StringMapper) query -> String.
                    format("https://movieboxapp.ir/api/genre/%s/a/", URLEncoder.encode(query, StandardCharsets.UTF_8)));
            configC("executor", Executors.newFixedThreadPool(3));
            configC("request", (Runnable) () ->
                    configC("executor", Executor.class).execute(() -> {
                        var s = textFieldE("search").getText();
                        textFieldE("search").setEnabled(false);
                        try {
                            configC("jsonObject", JsonParser.parseString(configC("client", HttpClient.class).
                                    send(HttpRequest.newBuilder().uri(URI.create(
                                                    configC(s.substring(0, s.indexOf('/')), StringMapper.class).
                                                            map(s.substring(s.indexOf('/') + 1)))).build(),
                                            HttpResponse.BodyHandlers.ofString()).body()).getAsJsonObject());
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            textFieldE("search").setEnabled(true);
                        }
                    }));
            add(new JPanel(new GridLayout(0, 1)) {{
                add(elementE("result", new JSyntaxTextArea(SyntaxConstants.SYNTAX_STYLE_JSON)).getScrollPaned());
                add(new JPanel(new GridLayout(1, 2)) {{
                    add(elementE("resultTitle", new JSyntaxTextArea(SyntaxConstants.SYNTAX_STYLE_JSON)).getScrollPaned());
                    add(elementE("resultUrl", new JSyntaxTextArea(SyntaxConstants.SYNTAX_STYLE_JSON)).getScrollPaned());
                }});
            }});
            add(elementE("search", new JTextField() {{
                addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            try {
                                configC("json", JsonParser.parseString(configC("client", HttpClient.class).
                                        send(HttpRequest.newBuilder().
                                                uri(URI.create(configC("api.search", StringMapper.class).map(getText()))).
                                                build(), HttpResponse.BodyHandlers.ofString()).body()).getAsJsonObject().getAsJsonArray("posters"));
                                asManualTypeC("json", JsonArray.class).
                                        forEach(element -> element.getAsJsonObject().getAsJsonArray("sources").
                                        forEach(source -> asCollectionC(String.class, "url").add(source.getAsJsonObject().get("url").getAsString())));
                                asManualTypeC("json", JsonArray.class).
                                        forEach(element -> asCollectionC(String.class, "title").add(element.getAsJsonObject().get("title").getAsString()));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            textAreaE("result").setText(asManualTypeC("pretty", StringMapper.class).map(configC("json").toString()));
                            textAreaE("resultUrl").setText("");
                            asCollectionC(String.class, "url").forEach(url -> textAreaE("resultUrl").
                                    append("?>  " + URLDecoder.decode(url, StandardCharsets.UTF_8) + "\n"));
                            textAreaE("resultTitle").setText("");
                            asCollectionC(String.class, "title").forEach(url -> textAreaE("resultTitle").
                                    append("?>  " + URLDecoder.decode(url, StandardCharsets.UTF_8) + "\n"));
                        }
                    }
                });
            }}), BorderLayout.NORTH);
        }});
    }
}
