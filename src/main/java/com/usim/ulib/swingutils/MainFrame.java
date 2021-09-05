package com.usim.ulib.swingutils;

import com.usim.ulib.utils.api.StateBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class MainFrame extends JFrame implements Runnable, StateBase<String, Container> {
    public static final int DEFAULT_HEIGHT = 720;
    public static final int DEFAULT_WIDTH = DEFAULT_HEIGHT * 16 / 9;

    private final Map<String, Container> stateMap;
    private final Map<String, JComponent> elements;

    private boolean isDark;
    private boolean isFullScreen;

    private String currentState;

    public MainFrame(String title, boolean dark) {
        super(title);

        stateMap = new HashMap<>();
        elements = new HashMap<>();
        isDark = dark;
        isFullScreen = false;
        currentState = "initial";

        init();
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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAction();
            }
        });

        handleMenuBar();
        handleSystemTray();
    }

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
        JMenuBar menuBar = new JMenuBar();

        JMenu helpMenu = new JMenu("Help");
        JMenu toolsMenu = new JMenu("Tools");
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JMenu analyzeMenu = new JMenu("Analyze");

        JMenuItem toggleDarkTheme = new JMenuItem("Toggle Dark Theme");
        toggleDarkTheme.addActionListener(e -> toggleDarkTheme());
        toggleDarkTheme.setAccelerator(KeyStroke.getKeyStroke('t', InputEvent.ALT_DOWN_MASK));
        viewMenu.add(toggleDarkTheme);

        JMenuItem fullScreen = new JMenuItem("Full Screen");
        fullScreen.addActionListener(e -> toggleFullScreen());
        fullScreen.setAccelerator(KeyStroke.getKeyStroke("F11"));
        viewMenu.add(fullScreen);

        JMenuItem quit = new JMenuItem("Quit");
        quit.addActionListener(e -> closeAction());
        quit.setAccelerator(KeyStroke.getKeyStroke('Q', InputEvent.ALT_DOWN_MASK));
        fileMenu.add(quit);

        JMenuItem tray = new JMenuItem("System Tray");
        tray.setAccelerator(KeyStroke.getKeyStroke('W', InputEvent.CTRL_DOWN_MASK));
        helpMenu.add(tray);

        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(viewMenu);
        menuBar.add(analyzeMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
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
        getJMenuBar().getMenu(4).getItem(0).addActionListener(e -> _gotoTray.run());

        trayIcon.addActionListener(e -> tray.remove(trayIcon));

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (main.isVisible()) tray.remove(trayIcon);
        }, AWTEvent.ACTION_EVENT_MASK + AWTEvent.WINDOW_EVENT_MASK);
    }

    protected void gotoSystemTray() {
        _gotoTray.run();
    }

    protected JComponent element(String tag, JComponent component) {
        elements.put(tag, component);
        return component;
    }

    protected JComponent element(String tag) {
        return elements.get(tag);
    }

    protected JButton buttonE(String tag) {
        return (JButton) elements.get(tag);
    }

    protected JTextField textFieldE(String tag) {
        return (JTextField) elements.get(tag);
    }

    protected JSlider sliderE(String tag) {
        return (JSlider) elements.get(tag);
    }

    protected JTabbedPane tabbedPaneE(String tag) {
        return (JTabbedPane) elements.get(tag);
    }

    protected JTextArea textAreaE(String tag) {
        return (JTextArea) elements.get(tag);
    }

    protected JTextPane textPaneE(String tag) {
        return (JTextPane) elements.get(tag);
    }

    protected JScrollPane scrollPaneE(String tag) {
        return (JScrollPane) elements.get(tag);
    }

    protected JPanel panelE(String tag) {
        return (JPanel) elements.get(tag);
    }

    protected JTable tableE(String tag) {
        return (JTable) elements.get(tag);
    }

    protected JComboBox<?> comboBoxE(String tag) {
        return (JComboBox<?>) elements.get(tag);
    }

    protected JLabel labelE(String tag) {
        return (JLabel) elements.get(tag);
    }

    protected JSplitPane splitPaneE(String tag) {
        return (JSplitPane) elements.get(tag);
    }

    protected JSeparator separatorE(String tag) {
        return (JSeparator) elements.get(tag);
    }

    protected JList<?> listE(String tag) {
        return (JList<?>) elements.get(tag);
    }

    protected JCheckBox checkBoxE(String tag) {
        return (JCheckBox) elements.get(tag);
    }

    protected JMenu menuE(String tag) {
        return (JMenu) elements.get(tag);
    }

    protected JMenuBar menuBarE(String tag) {
        return (JMenuBar) elements.get(tag);
    }

    protected JEditorPane editorPaneE(String tag) {
        return (JEditorPane) elements.get(tag);
    }

    public void toggleFullScreen() {
        isFullScreen = !isFullScreen;
        setVisible(false);
        if (isFullScreen) {
            dispose();
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);
        } else {
            dispose();
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
            setUndecorated(false);
            setExtendedState(JFrame.NORMAL);
            setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            setVisible(true);
        }
    }

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

    public void setDark(boolean dark) {
        if (dark != isDark)
            toggleDarkTheme();
    }

    public boolean isDark() {
        return isDark;
    }

    public String getTrayIconPath() {
        return trayIconPath;
    }

    public void setTrayIconPath(String trayIconPath) {
        this.trayIconPath = trayIconPath;
        handleSystemTray();
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

    public static List<Component> getAllComponents(Container container) {
        Component[] components = container.getComponents();
        List<Component> componentList = new ArrayList<>();
        for (Component component : components) {
            componentList.add(component);
            if (component instanceof Container)
                componentList.addAll(getAllComponents((Container) component));
        }

        return componentList;
    }

    @Override
    public final void run() {
        setVisible(true);
    }
}
