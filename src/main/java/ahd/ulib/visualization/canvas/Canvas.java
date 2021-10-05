package ahd.ulib.visualization.canvas;

import ahd.ulib.swingutils.MainFrame;
import ahd.ulib.utils.Utils;
import com.sun.management.OperatingSystemMXBean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.management.ManagementFactory;

@SuppressWarnings("unused")
public class Canvas extends JPanel implements Runnable {
    public static final int DEFAULT_REDRAW_DELAY = 4;
    private static int numberOfPanels = 0;
    private final Timer redrawTimer;
    protected Color backGround;
    private Image bgImage;
    private int fps;
    private long lastTime;
    private double timePerTick;
    private double delta;
    private long timer;
    private long loopCounter;
    private Color infoColor;
    protected Font infoFont;
    private boolean showInfo;
    private boolean showBgImg;
    protected final Camera camera;
    private final boolean fixedLayout;

    public Canvas(boolean allowChangeLayout) {
        redrawTimer = new Timer(DEFAULT_REDRAW_DELAY, e -> this.run());
        camera = new Camera();
        init();
        fixedLayout = !allowChangeLayout;
    }

    public Canvas() {
        this(false);
    }

    private void init() {
        super.setLayout(new BorderLayout());
        setPreferredSize(new Dimension(MainFrame.DEFAULT_WIDTH, MainFrame.DEFAULT_HEIGHT));
        setName("Canvas: Id=" + numberOfPanels++);
        backGround = Color.DARK_GRAY.darker();
        loopCounter = 0;
        delta = 0;
        timer = 1;
        bgImage = null;
        infoColor = Color.GREEN.darker();
        infoFont = new Font(Font.SANS_SERIF, Font.BOLD, 11);
        showInfo = true;
        showBgImg = true;
        setFps(30);
        setDoubleBuffered(true);

        addMouseListener(new MouseAdapter() {
            private boolean isRunning = false;

            @Override
            public void mousePressed(MouseEvent e) {
                isRunning = isRunning();
                stop();
                if (e.getButton() == MouseEvent.BUTTON1 && getComponentCount() != 0 && !e.isControlDown())
                    removeSettingPanel();
                if (e.getButton() == MouseEvent.BUTTON3 && getComponentCount() == 0 && !e.isControlDown())
                    addSettingPanel();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isRunning)
                    start();
            }
        });
    }

    protected JPanel getSettingPanel() {
        var fps = new JButton("Set FPS");
        var start = new JButton("Start");
        var stop = new JButton("Stop");
        var showInfo = new JCheckBox("Show Info", null, isShowInfo());
        var showBgImg = new JCheckBox("Show BgImg", null, isShowBgImg());
        var changeBgColor = new JButton("BgColor");
        var setBgImg = new JButton("Background Image");
        var setInfoColor = new JButton("Info Font Color");
        var setInfoFontSize = new JButton("Info Font Size");
        var capture = new JButton("Capture");
        var tooltip = new JButton("TooltipText");
        var resetRenderManager = new JButton("ResetRenderManager");
        var tickAndShow = new JButton("Tick and show");

        var settingPanel = new JPanel(new GridLayout(0, 2)) {{
            add(fps);
            add(new JLabel(getName(), JLabel.CENTER));
            add(start);
            add(stop);
            add(showInfo);
            add(showBgImg);
            add(changeBgColor);
            add(setInfoColor);
            add(setInfoFontSize);
            add(setBgImg);
            add(capture);
            add(tooltip);
            add(resetRenderManager);
            add(tickAndShow);
        }};
        start.addActionListener(e -> start());
        stop.addActionListener(e -> stop());
        showInfo.addActionListener(e -> setShowInfo(showInfo.isSelected()));
        showBgImg.addActionListener(e -> setShowBgImg(showBgImg.isSelected()));
        fps.addActionListener(e -> setFps(Integer.parseInt(JOptionPane.showInputDialog(Canvas.this, "Enter new FPS: (If any exception occurred nothing will change)", getFps()))));
        changeBgColor.addActionListener(e -> setBackground(JColorChooser.showDialog(Canvas.this, "Choose Background Color", getBackGround())));
        setInfoColor.addActionListener(e -> setInfoColor(JColorChooser.showDialog(Canvas.this, "Choose InfoText Color", getBackGround())));
        setBgImg.addActionListener(e -> setBgImage(JOptionPane.showInputDialog(Canvas.this, "Enter path of image: (If any exception occurred nothing will change)")));
        tooltip.addActionListener(e -> setToolTipText(JOptionPane.showInputDialog(Canvas.this, "Enter Tooltip Text of Canvas: (If any exception occurred nothing will change)", getToolTipText())));
        setInfoFontSize.addActionListener(e -> setInfoFont(new Font("serif", Font.BOLD, Integer.parseInt(JOptionPane.showInputDialog(Canvas.this, "Enter size of info font: (If any exception occurred nothing will change)", infoFont.getSize())))));
        capture.addActionListener(e -> Utils.saveCanvasAsImage(System.nanoTime() + "", Canvas.this));
        resetRenderManager.addActionListener(e -> {
            camera.clear();
            removeSettingPanel();
            addSettingPanel();
        });
        tickAndShow.addActionListener(e -> {
            camera.tick();
            repaint();
        });
        settingPanel.setBorder(BorderFactory.createTitledBorder("Plain Canvas"));
        return settingPanel;
    }

    public boolean isShowBgImg() {
        return showBgImg;
    }

    public void setShowBgImg(boolean showBgImg) {
        this.showBgImg = showBgImg;
        repaint();
    }

    public synchronized void start() {
        resetLoopInfo();
        lastTime = System.nanoTime();
        redrawTimer.start();
    }

    public synchronized void stop() {
        redrawTimer.stop();
//        repaint();
    }

    public RenderManager getRenderManager() {
        return camera;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        if (fps <= 0) {
            stop();
            return;
        }
        this.fps = fps;
        redrawTimer.setDelay(fps > 50 ? 0 : DEFAULT_REDRAW_DELAY);
        resetLoopInfo();
    }

    private void resetLoopInfo() {
        timer = 1;
        delta = 0;
        loopCounter = 0;
        camera.resetCounters();
        timePerTick = 1_000_000_000 / (double) fps;
    }

    public Image getBgImage() {
        return bgImage;
    }

    public void setBgImage(Image bgImage) {
        this.bgImage = bgImage;
        repaint();
    }

    public void setBgImage(String imgPath) {
        if (imgPath == null || !new File(imgPath).exists())
            return;
        setBgImage(new ImageIcon(imgPath).getImage());
    }

    public Color getBackGround() {
        return backGround;
    }

    public long getLoopCounter() {
        return loopCounter;
    }

    public long getTicksCounter() {
        return camera.getTickCounter();
    }

    public double getTimePerTick() {
        return timePerTick;
    }

    public long getRealFps() {
        return camera.getRenderCounter() * 1_000_000_000L / timer + 1;
    }

    public long getRealTps() {
        return camera.getTickCounter() * 1_000_000_000L / timer + 1;
    }

    public long getLps() {
        return loopCounter * 1_000_000_000L / timer + 1;
    }

    public boolean isRunning() {
        return redrawTimer.isRunning();
    }

    public long getRenderCounter() {
        return camera.getRenderCounter();
    }

    public Color getInfoColor() {
        return infoColor;
    }

    public void setInfoColor(Color infoColor) {
        this.infoColor = infoColor;
        repaint();
    }

    public Font getInfoFont() {
        return infoFont;
    }

    public void setInfoFont(Font infoFont) {
        this.infoFont = infoFont;
        repaint();
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
        repaint();
    }

    public void addRender(Render... renders) {
        camera.addRender(renders);
        repaint();
        revalidate();
    }

    public final void addSettingPanel() {
        removeSettingPanel();
        var p = getSettingPanel();
        if (p == null)
            return;
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));
        add(new JScrollPane(p, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.EAST);
        repaint();
        revalidate();
    }

    public void removeSettingPanel() {
        try {
            remove(0);
        } catch (Exception ignore) {}
        revalidate();
        repaint();
    }

    public final Camera camera() {
        return camera;
    }

    public final void removeAllRenders() {
        camera.clear();
    }

    @Override
    public void setLayout(LayoutManager mgr) {
        if (fixedLayout)
            System.err.println("AHD:: Can't change layout manager of an Canvas");
        else
            super.setLayout(mgr);
    }

    @Override
    public void setBackground(Color bg) {
        backGround = bg;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        if (g == null)
            return;
        var g2d = (Graphics2D) g;

        if (bgImage == null || !showBgImg) {
            g2d.setColor(backGround);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2d.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
        }

        camera.render(g2d);

        if (!showInfo)
            return;

        g2d.setFont(infoFont);
        g2d.setColor(infoColor);
        if (!isRunning()) {
            g2d.drawString("Not Dynamic, Tick: " + getTicksCounter() + ", Render: " + getRenderCounter(), 0, 10);
            return;
        }

        g2d.drawString(
                "FPS: " + getRealFps() +
                        ", TPS: " + getRealTps() +
                        ", CPU: " + Utils.round(((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getProcessCpuLoad() * 100, 2) +
                        "%, Tick: " + getTicksCounter() + ", Render: " + getRenderCounter() +
                        ", LTT: " + camera.numOfAliveTickThreads() +
                        ", STT: " + camera.singleThreadedTick() +
                        ", TRT: " + camera.tickRoundTime() +
                        ", RRT: " + camera.renderRoundTime()
                , 0, (int) (infoFont.getSize() * 0.8)
        );
    }

    @Override
    public final void run() {
        long now = System.nanoTime();
        delta += (now - lastTime) / timePerTick;
        timer += now - lastTime;
        lastTime = now;
        boolean flag = delta >= 1;

        while (delta-- >= 1)
            camera.tick();

        if (flag)
            repaint();

        loopCounter++;
    }
}
