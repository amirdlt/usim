package ahd.ulib.swingutils;

import ahd.ulib.utils.Utils;
import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaListPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MediaPlayerPanel extends MainPanel {

    private static final String ICON_DIR = ".\\src\\main\\resources\\icons\\";
    private static final Map<String, String> icons = new HashMap<>() {{
        List.of(
                "play", "pause", "previous",
                "next", "fullscreen", "extended",
                "snapshot", "volume-high", "volume-muted", "stop").forEach(s -> put(s, ICON_DIR + "buttons\\" + s + ".png"));
    }};

    public MediaPlayerPanel() {
        init();
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void init() {
        // listeners


        // player
        final var player = new EmbeddedMediaListPlayerComponent();
        add(elementE("player", player), BorderLayout.CENTER);

        // controls
        add(elementE("controls", new JPanel() {{
            setLayout(new MigLayout("fill, insets 10 10 10 10", "[]20[]5[]5[]20[]5[]20[][][][]", "[]"));
            add(elementE("play", new JButton(new ImageIcon(icons.get("play"))) {{
                addActionListener(e -> {
                    if (!player.mediaPlayer().status().isPlayable()) {
                        player.mediaPlayer().media().play(JOptionPane.showInputDialog("url"));
                        return;
                    }
                    if (player.mediaPlayer().status().isPlaying()) {
                        player.mediaPlayer().controls().pause();
                        configC(Timer.class, "updater").stop();
                    } else {
                        player.mediaPlayer().controls().play();
                        configC(Timer.class, "updater").start();
                    }
                });
            }}));
            add(elementE("previous", new JButton(new ImageIcon(icons.get("previous")))), "sg 1");
            add(elementE("stop", new JButton(new ImageIcon(icons.get("stop")))), "sg 1");
            add(elementE("next", new JButton(new ImageIcon(icons.get("next")))), "sg 1");
            add(elementE("fullscreen", new JButton(new ImageIcon(icons.get("fullscreen")))), "sg 1");
            add(elementE("extended", new JButton(new ImageIcon(icons.get("extended")))), "sg 1");
            add(elementE("snapshot", new JButton(new ImageIcon(icons.get("snapshot")))), "sg 1");
            add(elementE("time", new JLabel("---:--")));
            add(elementE("position", new JSlider(0, 100, 0) {{
                configC(false, "positionChanging");
                configC(false, "sliderChanging");
                addChangeListener(e -> {
                    if (!asBooleanC("positionChanging")) {
                        configC(getValueIsAdjusting(), "sliderChanging");
                        player.mediaPlayer().controls().setPosition(getValue() / 1000.f);
                    }
                });
            }}), "growx, pushx");
            add(elementE("mute", new JButton(new ImageIcon(icons.get("volume-high"))) {{
                configC(false, "isMute");
                configC(60, "volumeValue");
                addActionListener(e -> {
                    var isMute = asBooleanC("isMute");
                    var slider = sliderE("volume");
                    if (isMute) {
                        player.mediaPlayer().audio().mute();
                        slider.setValue(asIntC("volumeValue"));
                    } else {
                        configC(slider.getValue(), "volumeValue");
                        slider.setValue(0);
                    }
                    configC(!isMute, "isMute");
                    setIcon(isMute ? new ImageIcon(icons.get("volume-high")) : new ImageIcon(icons.get("volume-muted")));
                });
            }}), "sg 1");
            add(elementE("volume", new JSlider(0, 200, 60) {{
                addChangeListener(e -> player.mediaPlayer().audio().setVolume(getValue()));
            }}), "wmax 100");
        }}), BorderLayout.SOUTH);
        configC(new Timer(1000, e -> updateElementsE()), "updater");
    }

    @Override
    public void updateElementsE() {
        if (!asBooleanC("sliderChanging")) {
            configC(true, "positionChanging");
            var player = elementE("player", EmbeddedMediaListPlayerComponent.class).mediaPlayer();
            sliderE("position").setValue((int) (player.status().position() * 1000f));
            var time = elementE("player", EmbeddedMediaListPlayerComponent.class).mediaPlayer().status().time() / 1000;
            labelE("time").setText(String.format("%03d:%02d", time / 60, time % 60));
            configC(false, "positionChanging");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MainFrame() {{
            add(new MediaPlayerPanel(), BorderLayout.CENTER);
        }});
    }
}
