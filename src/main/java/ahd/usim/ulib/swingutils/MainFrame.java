package ahd.usim.ulib.swingutils;

import ahd.usim.ulib.utils.Utils;
import ahd.usim.ulib.utils.api.StateBase;
import ahd.usim.ulib.visualization.canvas.Canvas;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme;
import com.formdev.flatlaf.ui.FlatRootPaneUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

@SuppressWarnings("unused")
public class MainFrame extends JFrame implements Runnable, StateBase<String, Container>, ElementBaseContainer {
    public static final int DEFAULT_HEIGHT = 720;
    public static final int DEFAULT_WIDTH = DEFAULT_HEIGHT * 16 / 9;

    public final String INITIAL_STATE_KEY;
    public final Container INITIAL_STATE_VALUE;

    private final Map<String, Container> stateMap;
    private final Map<String, JComponent> elements;

    private boolean isDark;
    private boolean isFullScreen;

    private final boolean isFrameBuilt;

    private String currentState;

    private final UIManager.LookAndFeelInfo[] allThemesInfo;

    public MainFrame(String title, boolean dark) {
        super(title);

        INITIAL_STATE_KEY = "initial";
        INITIAL_STATE_VALUE = getContentPane();

        stateMap = new HashMap<>();
        elements = new HashMap<>();
        isDark = dark;
        isFullScreen = false;

        allThemesInfo = new ArrayList<UIManager.LookAndFeelInfo>() {{
            addAll(List.of(new UIManager.LookAndFeelInfo(FlatDarkLaf.NAME, FlatDarkLaf.class.getName()),
                    new UIManager.LookAndFeelInfo(FlatLightLaf.NAME, FlatLightLaf.class.getName()),
                    new UIManager.LookAndFeelInfo(FlatIntelliJLaf.NAME, FlatIntelliJLaf.class.getName()),
                    new UIManager.LookAndFeelInfo(FlatDarculaLaf.NAME, FlatDarculaLaf.class.getName())));
            addAll(Arrays.asList(UIManager.getInstalledLookAndFeels()));
            addAll(Arrays.asList(FlatAllIJThemes.INFOS));
        }}.toArray(UIManager.LookAndFeelInfo[]::new);

        init();

        isFrameBuilt = true;
    }

    public MainFrame(String title) {
        this(title, true);
    }

    public MainFrame() {
        this(null, true);
    }

    private void init() {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        setLocationRelativeTo(null);
        setLocationByPlatform(false);

        currentState = INITIAL_STATE_KEY;
        addState(currentState, INITIAL_STATE_VALUE);

        initFlatLaf();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAction();
            }
        });

        handleMenuBar();
        handleSystemTray();

    }

    private void initFlatLaf() {
        setLookAndFeel(new FlatGitHubDarkIJTheme());
        getRootPane().setUI(new FlatRootPaneUI());
    }

    @Deprecated
    private void initNimbusLaf() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.exit(-1);
        }

        handleNimbusProperties();
    }

    @Deprecated
    private void handleNimbusProperties() {
        if (isDark) {
            UIManager.put("control", Color.DARK_GRAY.darker());
            UIManager.put("info", new Color(128,128,128));
            UIManager.put("nimbusBase", new Color( 18, 30, 49));
            UIManager.put("nimbusAlertYellow", new Color( 248, 187, 0));
            UIManager.put("nimbusDisabledText", new Color( 128, 128, 128));
            UIManager.put("nimbusFocus", new Color(115,164,209));
            UIManager.put("nimbusGreen", new Color(176,179,50));
            UIManager.put("nimbusInfoBlue", new Color( 66, 139, 221));
            UIManager.put("nimbusLightBackground", Color.DARK_GRAY);
            UIManager.put("nimbusOrange", new Color(191,98,4));
            UIManager.put("nimbusRed", new Color(169,46,34) );
            UIManager.put("nimbusSelectedText", new Color( 255, 255, 255));
            UIManager.put("nimbusSelectionBackground", new Color( 104, 93, 156));
            UIManager.put("text", new Color( 230, 230, 230));
        } else {
            UIManager.put("control", new Color(214,217,223));
            UIManager.put("info", new Color(242,242,189));
            UIManager.put("nimbusBase", new Color( 51,98,140));
            UIManager.put("nimbusAlertYellow", new Color( 255,220,35));
            UIManager.put("nimbusDisabledText", new Color( 142,143,145));
            UIManager.put("nimbusFocus", new Color(115,164,209));
            UIManager.put("nimbusGreen", new Color(176,179,50));
            UIManager.put("nimbusInfoBlue", new Color( 47,92,180));
            UIManager.put("nimbusLightBackground", new Color(255,255,255));
            UIManager.put("nimbusOrange", new Color(191,98,4));
            UIManager.put("nimbusRed", new Color(169,46,34) );
            UIManager.put("nimbusSelectedText", new Color( 255, 255, 255));
            UIManager.put("nimbusSelectionBackground", new Color( 57,105,138));
            UIManager.put("text", new Color( 0, 0, 0));
        }
    }

    private void handleMenuBar() {
        setJMenuBar(new JMenuBar() {{
            add(new JMenu("File") {{
                setMnemonic(KeyEvent.VK_F);
                add(new JMenuItem("Call Garbage Collector") {{
                    addActionListener(e -> System.gc());
                }});
                add(new JMenuItem("CMD") {{
                    addActionListener(e -> SwingUtilities.invokeLater(() -> new JDialog(MainFrame.this) {{
                        setSize(400, 550);
                        setLocation(MainFrame.this.getLocation());
                        setTitle("CMD Command Executor");
                        setLayout(new BorderLayout());
                        add(new CmdToolPanel());
                    }}.setVisible(true)));
                }});
            }});
            add(new JMenu("View") {{
                setMnemonic(KeyEvent.VK_V);
                add(new JMenuItem("Full Screen") {{
                    setAccelerator(KeyStroke.getKeyStroke("F11"));
                    addActionListener(e -> toggleFullScreen());
                }});
                add(new JMenu("Themes") {{
                    add(element("currentTheme-menuItem", new JMenuItem("Current: " + UIManager.getLookAndFeel().getName()) {{
                        addActionListener(e -> setThemeByIndex((int) (Math.random() * allThemesInfo.length)));
                        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.ALT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK));
                    }}));
                    add(new JMenu("Flat Core Themes") {{
                        add(new JMenuItem("Flat Dark") {{
                            addActionListener(e -> setLookAndFeel(new FlatDarkLaf()));
                        }});
                        add(new JMenuItem("Flat Light") {{
                            addActionListener(e -> setLookAndFeel(new FlatLightLaf()));
                        }});
                        add(new JMenuItem("Flat IntelliJ") {{
                            addActionListener(e -> setLookAndFeel(new FlatIntelliJLaf()));
                        }});
                        add(new JMenuItem("Flat Darcula") {{
                            addActionListener(e -> setLookAndFeel(new FlatDarculaLaf()));
                        }});
                    }});
                    add(new JMenu("JDK Native Installed") {{
                        for (var info : UIManager.getInstalledLookAndFeels())
                            add(new JMenuItem(info.getName()) {{
                                addActionListener(e -> setLookAndFeel(info.getClassName()));
                            }});
                    }});
                    add(new JMenu("IntelliJ Themes") {{
                        var dark = new JMenu("Dark Themes");
                        var light = new JMenu("Light Themes");
                        for (var info : FlatAllIJThemes.INFOS)
                            (info.isDark() ? dark : light).add(new JMenuItem(info.getName()) {{
                                addActionListener(e -> setLookAndFeel(info.getClassName()));
                            }});
                        add(dark);
                        add(light);
                    }});
                }});
                add(new JMenuItem("Go to System Tray") {{
                    addActionListener(e -> gotoSystemTray());
                    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
                }});
                add(new JMenuItem("Toggle Fullscreen") {{
                    addActionListener(e -> toggleFullScreen());
                }});
            }});
            add(new JMenu("Help") {{
                add(new JMenuItem("Help") {{
                    addActionListener(
                            e -> JOptionPane.showMessageDialog(MainFrame.this, "Please contact: AmirhosseinDolatkhah2000@gmail.com"));
                }});
            }});
        }});
    }

    protected void setLookAndFeel(LookAndFeel lookAndFeel) {
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(this);
        getAllComponents(this, Canvas.class).forEach(c -> c.setBackground(c.getBackGround().darker()));
    }

    protected void setLookAndFeel(String className) {
        try {
            setLookAndFeel((LookAndFeel) Class.forName(className).getDeclaredConstructor().newInstance());
        } catch (InstantiationException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
        }
    }

    private Runnable _gotoTray;
    private String trayIconPath;
    private void handleSystemTray() {
        TrayIcon trayIcon;
        SystemTray tray;
        String tooltip = getTitle();
        String exitText = "Exit";
        String openText = "Open";
        PopupMenu popupMenu;

        if (!SystemTray.isSupported()) return;

        tray = SystemTray.getSystemTray();
        popupMenu = new PopupMenu();

        trayIcon = new TrayIcon(new ImageIcon(trayIconPath == null ? "" : trayIconPath).getImage(), tooltip, popupMenu);
        JFrame main = this;
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    main.setVisible(true);
                    main.setState(Frame.NORMAL);
                }
            }
        });
        trayIcon.setImageAutoSize(true);

        MenuItem defaultItem = new MenuItem(exitText);
        defaultItem.addActionListener(e -> System.exit(0));
        popupMenu.add(defaultItem);

        defaultItem = new MenuItem(openText);
        defaultItem.addActionListener(e -> {
            this.setVisible(true);
            this.setExtendedState(JFrame.NORMAL);
        });
        popupMenu.add(defaultItem);

        this.add(popupMenu);

        this.addWindowStateListener(windowEvent -> {

            if (windowEvent.getNewState() == Frame.MAXIMIZED_BOTH) {
                tray.remove(trayIcon);
                main.setVisible(true);
            }

            if (windowEvent.getNewState() == Frame.NORMAL) {
                tray.remove(trayIcon);
                main.setVisible(true);
            }

        });

        _gotoTray = () -> {
            try {
                tray.add(trayIcon);
                main.setVisible(false);
            } catch (AWTException ignore) {}
        };
//        getJMenuBar().getMenu(4).getItem(0).addActionListener(e -> _gotoTray.run());

        trayIcon.addActionListener(e -> tray.remove(trayIcon));

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (main.isVisible()) tray.remove(trayIcon);
        }, AWTEvent.WINDOW_EVENT_MASK);
    }

    protected void setThemeByIndex(int index) {
        while (index < 0)
            index += allThemesInfo.length;
        while (index >= allThemesInfo.length)
            index -= allThemesInfo.length;
        try {
            setLookAndFeel(allThemesInfo[index].getClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        menuItemE("currentTheme-menuItem").setText("Current: " + UIManager.getLookAndFeel().getName());
    }

    public void gotoSystemTray() {
        _gotoTray.run();
    }

    @Override
    public Map<String, JComponent> elements() {
        return elements;
    }

    public void showErrorDialog(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showErrorDialog(String message) {
        showErrorDialog(message, "Error");
    }

    public void showErrorDialog() {
        showErrorDialog("Error", "Error");
    }

    private int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;
    public void toggleFullScreen() {
        isFullScreen = !isFullScreen;
        setVisible(false);
        if (isFullScreen) {
            width = getWidth();
            height = getHeight();
            dispose();
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);
        } else {
            dispose();
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
            setUndecorated(false);
            setExtendedState(JFrame.NORMAL);
            setSize(width, height);
            setVisible(true);
        }
    }

    @Deprecated
    public void toggleDarkTheme() {
        isDark = !isDark;
        handleNimbusProperties();
        getAllComponents(this).stream().filter(e -> e instanceof JComponent).map(e -> (JComponent) e).forEach(JComponent::updateUI);
        repaint();
    }

    protected void closeAction() {
        System.exit(0);
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        if (fullScreen != isFullScreen)
            toggleFullScreen();
    }

    @Deprecated
    public void setDark(boolean dark) {
        if (dark != isDark)
            toggleDarkTheme();
    }

    @Deprecated
    public boolean isDark() {
        return isDark || FlatLaf.isLafDark();
    }

    public String getTrayIconPath() {
        return trayIconPath;
    }

    public void setTrayIconPath(String trayIconPath) {
        this.trayIconPath = trayIconPath;
        handleSystemTray();
    }

    @Override
    protected void setRootPane(JRootPane root) {
        if (isFrameBuilt)
            throw new RuntimeException("AHD:: Cannot change root pane of the main frame");
        super.setRootPane(root);
    }

    @Override
    public String currentState() {
        return null;
    }

    @Override
    public Map<String, Container> stateMap() {
        return stateMap;
    }

    @Override
    public void addState(String stateName, Container stateContainer) {
        stateMap.put(stateName, stateContainer);
    }

    @Override
    @Deprecated
    public void removeState(String key) {
        throw new RuntimeException("AHD:: not implemented yet");
    }

    @Override
    public Container getState(String stateName) {
        return stateMap.get(stateName);
    }

    public List<String> getStateNames() {
        return stateKeys();
    }

    @Override
    public void setState(String stateName) {
        setContentPane(stateMap.getOrDefault(currentState = stateName, getContentPane()));
        repaint();
        revalidate();
    }

    public static @NotNull List<Component> getAllComponents(@NotNull Container container) {
        return getAllComponents(container, Component.class);
    }

    public static <T> @NotNull List<T> getAllComponents(@NotNull Container container, Class<T> type) {
        Component[] components = container.getComponents();
        List<T> componentList = new ArrayList<>();
        for (Component component : components) {
            if (type.isInstance(component)) {
                //noinspection unchecked
                componentList.add((T) component);
            }
            if (component instanceof Container)
                componentList.addAll(getAllComponents((Container) component, type));
        }

        return componentList;
    }

    @Override
    public final void run() {
        setVisible(true);
    }
}
