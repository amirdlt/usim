package ahd.uimp.moviefinder;

import ahd.ulib.swingutils.MainPanel;
import ahd.ulib.swingutils.SwingUtils;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

public class MoviePanel extends MainPanel {
    public MoviePanel() {
        init();
    }

    private void init() {
        try {
            addState("main", mainState());
            addState("singleMovie", singleMovieState());
            setState("main");
        } catch (Exception e) {
            throw new RuntimeException("AHD:: Error", e);
        }
    }

    @Contract(" -> new")
    private @NotNull JPanel mainState() {
        return new JPanel(new BorderLayout()) {{
            add(new JPanel(new MigLayout("fill")) {{
                add(elementE("main.command", new JTextField() {{
                    setFont(new Font(Font.SERIF, Font.BOLD, 14));
                    addActionListener(e -> {
                        removeAllE("main.list");
                        MovieApi.searchInMovieBox(getText()).forEach(movie -> panelE("main.list." + movie.type()).add(new JPanel(new MigLayout()) {{
                            setOpaque(true);
                            setName(movie.coverUrl());
                            addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseEntered(MouseEvent e) {
                                    setBorder(BorderFactory.createLineBorder(Color.YELLOW));
                                    var preview = configOrElseC(new JDialog() {{
                                        setUndecorated(true);
                                        add(new JPanel() {
                                            @Override
                                            protected void paintComponent(Graphics g) {
                                                try {
                                                    g.drawImage(ImageIO.read(new URL(asStringC("coverUrl"))), 0, 0, 250, 250, null);
                                                } catch (IOException ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                        }, BorderLayout.CENTER);
                                    }}, "preview");
                                    preview.setBounds(e.getLocationOnScreen().x + 10, e.getLocationOnScreen().y + 5, 250, 250);
                                    configC((Object) getName(), "coverUrl");
                                    preview.setVisible(true);
                                }

                                @Override
                                public void mouseExited(MouseEvent e) {
                                    setBorder(null);
                                    configC(JDialog.class, "preview").setVisible(false);
                                }

                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    if (!stateMap().containsKey("m" + movie.id()))
                                        addState("m" + movie.id(), elementE("main.list.m" + movie.id(), new JPanel(new MigLayout()) {{
                                            try {
                                                add(new JLabel(new ImageIcon(ImageIO.read(new URL(movie.coverUrl())))), "wrap");
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                            add(new JLabel(String.valueOf(movie.year())));
                                            EmbeddedMediaPlayerComponent player;
                                            add(player = new EmbeddedMediaPlayerComponent() {{
                                                setSize(new Dimension(200, 200));
                                                setPreferredSize(new Dimension(200, 200));
                                                setMinimumSize(new Dimension(200, 200));
                                                setMaximumSize(new Dimension(200, 200));
                                            }});
                                            add(new JButton("Play") {{
                                                setContentAreaFilled(false);
                                                setBorder(SwingUtils.getRoundedBorder(60));
                                                addActionListener(e -> player.mediaPlayer().media().play("https://imdb-video.media-imdb.com/vi306231833/1434659607842-pgv4ql-1551719203043.mp4?Expires=1642680569&Signature=KFwKR9sA1hgCXqIyvNLzEc1cgBWDgFHTnKbpViIHHeI38kJXuK9U2tY5t9QYgP61zdSqlDNRVKfNAfGWZyvgNYbaAsLaalvnRB19k3Xw0NqgtFJy2vvmvATGe5LIUHyVSb2zzDT8Irg9zkqX49yW9Pkuw6bElcwmszuxSbgcQF6l~K8aSN0Jpts4oClkIyzhXWWuULlRHDYLbsSbV797szc13b5lhSHRCePXfsZJQEWLhDdWF46YCs8-E-c4XBeQ0~huH~GWP1~D~Iuf8hUxnniJjaAgHPxPQEsIa15mAtipS205e5jljNQvcHXIbPRfkl0Ryej0W3BhyVk1L5ZxoQ__&Key-Pair-Id=APKAIFLZBVQZ24NQH3KA"));
                                            }});
                                        }}));
                                    setState("m" + movie.id());
                                    configC(JDialog.class, "preview").setVisible(false);
                                }
                            });
                            add(new JLabel(movie.name()));
                        }}));
                    });
                }}));
            }}, BorderLayout.NORTH);
            add(new JSplitPane(JSplitPane.VERTICAL_SPLIT) {{
                setLeftComponent(new JScrollPane(elementE("main.list.movie", new JPanel(new MigLayout("wrap")) {{

                }})) {{
                    getVerticalScrollBar().setUnitIncrement(15);
                }});
                setRightComponent(new JScrollPane(elementE("main.list.series", new JPanel(new MigLayout("wrap")) {{

                }})) {{
                    getVerticalScrollBar().setUnitIncrement(15);
                }});
                setDividerLocation(300);
            }}, BorderLayout.CENTER);
        }};
    }

    @Contract(" -> new")
    private @NotNull JPanel singleMovieState() {
        return new JPanel(new MigLayout()) {{

        }};
    }
}
