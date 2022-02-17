package ahd.ulib.swingutils;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.media.MediaEventAdapter;
import uk.co.caprica.vlcj.player.base.State;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaListPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaPlayerPanel extends MainFrame {

    private static final String ICON_DIR = ".\\src\\main\\resources\\icons\\";
    private static final Map<String, Icon> icons = new HashMap<>() {{
        List.of(
                "play", "pause", "previous",
                "next", "fullscreen", "extended",
                "snapshot", "volume-high", "volume-muted", "stop").
                forEach(s -> put(s, new ImageIcon(ICON_DIR + "buttons\\" + s + ".png")));
    }};

    public MediaPlayerPanel() {
        init();
    }

    @SuppressWarnings("SpellCheckingInspection")
    protected void init() {
        // add states
        addState("settings", new JPanel() {{
            add(new JLabel("Hello"), BorderLayout.CENTER);
            add(new JPanel(new FlowLayout(FlowLayout.CENTER)) {{
                add(new JButton("Back") {{
                    addActionListener(e -> setState("initial"));
                }});
            }}, BorderLayout.SOUTH);
        }});
        setState("settings");

        // listeners


        // player
        final var player = new EmbeddedMediaListPlayerComponent() {
            {
                mediaPlayer().events().addMediaEventListener(new MediaEventAdapter() {
                    @Override
                    public void mediaStateChanged(Media media, State newState) {
                        switch (newState) {
                            case ENDED -> {
                                media.release();
                                sliderE("position").setEnabled(false);
                                buttonE("play").setIcon(icons.get("play"));
                                configC(Timer.class, "updater").stop();
                                updateElementsE();
                            }
                            case STOPPED, PAUSED -> {
                                buttonE("play").setIcon(icons.get("play"));
                                configC(Timer.class, "updater").stop();
                                updateElementsE();
                            }
                            case PLAYING -> {
                                buttonE("play").setIcon(icons.get("pause"));
                                configC(Timer.class, "updater").start();
                                sliderE("position").setEnabled(true);
                            }
                            case OPENING -> {

                            }
                        }
                    }
                });
            }

            @Override
            public void keyTyped(KeyEvent e) {
                var slider = sliderE("position");
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT -> slider.setValue(slider.getValue() + 1);
                    case KeyEvent.VK_LEFT -> slider.setValue(slider.getValue() - 1);
                    case KeyEvent.VK_UP, KeyEvent.VK_1 -> sliderE("volume").setValue(sliderE("volume").getValue() + 2);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                panelE("controls").setVisible(e.getY() > getHeight() - 30);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                switch (e.getButton()) {
                    case 1 -> {
                        if (!mediaPlayer().status().isPlayable())
                            return;
                        if (mediaPlayer().status().isPlaying())
                            mediaPlayer().controls().pause();
                        else
                            mediaPlayer().controls().start();

                    }
                    case 2 -> mediaPlayer().media().play(JOptionPane.showInputDialog("url"));
                    case 3 -> new JPopupMenu() {{
                        add(new JButton("Not Implemented yet"));
                    }}.show(this, e.getX(), e.getY());
                    case 4 -> mediaPlayer().controls().skipTime(-5000);
                    case 5 -> mediaPlayer().controls().skipTime(5000);
                }
            }
        };
        add(elementE("player", player), BorderLayout.CENTER);

        // controls
        add(elementE("controls", new JPanel() {{
            setLayout(new MigLayout("fill, insets 10 10 10 10", "[]10[]3[]3[]10[]3[]10[][][][][]", "[]"));
            add(elementE("play", new JButton(icons.get("play")) {{
                addActionListener(e -> {
                    if (!player.mediaPlayer().status().isPlayable()) {
                        player.mediaPlayer().media().play(/*JOptionPane.showInputDialog("url")*/
                                "D:\\Entertainment\\music\\David Bowie - Ashes To Ashes (Official Video)-HyMm4rJemtI.mkv"
                                /*"https://youtu.be/RCU6Ik-_fsg"*/);
                        sliderE("position").setEnabled(true);
                        configC(Timer.class, "updater").start();
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
            add(elementE("previous", new JButton(icons.get("previous"))), "sg 1");
            add(elementE("stop", new JButton(icons.get("stop"))), "sg 1");
            add(elementE("next", new JButton(icons.get("next"))), "sg 1");
            add(elementE("fullscreen", new JButton(icons.get("fullscreen"))), "sg 1");
            add(elementE("extended", new JButton(icons.get("extended")) {{
                addActionListener(e -> {
                    if (player.mediaPlayer().status().isPlaying())
                        player.mediaPlayer().controls().pause();
                    setState("settings");
                });
            }}), "sg 1");
            add(elementE("snapshot", new JButton(icons.get("snapshot"))), "sg 1");
            add(elementE("time", new JLabel("---:--")));
            add(elementE("position", new JSlider(0, 1000, 0) {{
                setEnabled(false);
                configC(false, "positionChanging");
                configC(false, "sliderChanging");
                addChangeListener(e -> {
                if (!asBooleanC("positionChanging")) {
                    configC(getValueIsAdjusting(), "sliderChanging");
                    player.mediaPlayer().controls().setPosition(getValue() / 1000.f);
                }});
            }}), "growx, pushx");
            add(elementE("remainingTime", new JLabel("---:--")));
            add(elementE("mute", new JButton(icons.get("volume-high")) {{
                addActionListener(e -> {
                    var audio = player.mediaPlayer().audio();
                    audio.setMute(!audio.isMute());
                    sliderE("volume").setEnabled(audio.isMute());
                    setIcon(icons.get(audio.isMute() ? "volume-high" : "volume-muted"));
                });
            }}), "sg 1");
            add(elementE("volume", new JSlider(0, 200, 60) {{
                addChangeListener(e -> player.mediaPlayer().audio().setVolume(getValue()));
            }}), "wmax 100");
        }}), BorderLayout.SOUTH);
        configC(new Timer(400, e -> updateElementsE()), "updater");
    }

    public EmbeddedMediaListPlayerComponent getPlayerComponent() {
        return elementE("player", EmbeddedMediaListPlayerComponent.class);
    }

    @Override
    public void updateElementsE() {
        if (!asBooleanC("sliderChanging")) {
            configC(true, "positionChanging");
            var player = getPlayerComponent().mediaPlayer();
            sliderE("position").setValue((int) (player.status().position() * 1000f));
            var time = getPlayerComponent().mediaPlayer().status().time() / 1000;
            labelE("time").setText(String.format("%03d:%02d", time / 60, time % 60));
            var remaining = player.media().info().duration() / 1000 - time;
            labelE("remainingTime").setText(String.format("%03d:%02d", remaining / 60, remaining % 60));
            configC(false, "positionChanging");
        }
    }

    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new MainFrame() {{
//            add(new MediaPlayerPanel(), BorderLayout.CENTER);
//        }});
        SwingUtilities.invokeLater(new MediaPlayerPanel());
    }
}
