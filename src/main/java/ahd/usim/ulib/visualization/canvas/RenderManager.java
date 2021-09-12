package ahd.usim.ulib.visualization.canvas;

import ahd.usim.ulib.swingutils.MainFrame;
import ahd.usim.ulib.utils.Utils;
import ahd.usim.ulib.visualization.shapes.shape3d.Area;
import ahd.usim.ulib.visualization.shapes.shape3d.FlatSurface;
import ahd.usim.ulib.visualization.shapes.shape3d.Shape3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class RenderManager extends ArrayList<Render> implements Render {
    private static final RenderManager unsafe = new RenderManager();

    private static final long MAX_SINGLE_THREADED_TICK_TIME = 80;
    private int renderCounter;
    private int tickCounter;
    private final ThreadPoolExecutor tickExecutor;
    private final Runnable tickRunnable;
    private final AtomicLong lastTickTime;
    private long lastRenderTime;
    private String frameSequencePath;
    private Dimension frameDimension;

    public RenderManager(Render... renders) {
        super(Arrays.asList(renders));
        tickExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15);
        lastTickTime = new AtomicLong();
        renderCounter = 0;
        tickCounter = 0;
        frameDimension = new Dimension(MainFrame.DEFAULT_WIDTH, MainFrame.DEFAULT_HEIGHT);
        frameSequencePath = null;
        tickRunnable = () -> {
            var t = System.currentTimeMillis();
            forEach(Tick::tick);
            lastTickTime.set(System.currentTimeMillis() - t);
            tickCounter++;
        };
    }

    public int numOfAliveTickThreads() {
        return tickExecutor.getActiveCount();
    }

    public void addRender(Render... renders) {
        addAll(Arrays.asList(renders));
    }

    public void addTick(Runnable... ticks) {
        addRender(Arrays.stream(ticks).map(t -> new Render() {
            @Override
            public void render(Graphics2D g2d) {}

            @Override
            public void tick() {
                t.run();
            }
        }).toArray(Render[]::new));
    }

    public List<Shape3D> getShape3d() {
        return stream().filter(e -> e instanceof Shape3D).map(e -> (Shape3D) e).collect(Collectors.toList());
    }

    public List<FlatSurface> getFlatSurfaces() {
        return stream().filter(e -> e instanceof FlatSurface).map(e -> (FlatSurface) e).collect(Collectors.toList());
    }

    public <T> List<T> get(Class<T> clazz) {
        //noinspection unchecked
        return stream().filter(clazz::isInstance).map(e -> (T) e).toList();
    }

    @Override
    public void render(Graphics2D g2d) {
        var t = System.currentTimeMillis();
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderAction(g2d);
        if (frameSequencePath != null) {
            var img = new BufferedImage(MainFrame.DEFAULT_WIDTH, MainFrame.DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            var _g2d = img.createGraphics();
            renderAction(_g2d);
            _g2d.dispose();
            try {
                Utils.saveRenderedImage(img, frameSequencePath + "/frame" + renderCounter + ".png", "png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lastRenderTime = System.currentTimeMillis() - t;
        renderCounter++;
    }

    protected void renderAction(Graphics2D g2d) {
        var list = new ArrayList<Shape3D>();
        stream().filter(Area.class::isInstance).map(e -> ((Shape3D) e).getComponents()).forEach(list::addAll);
        list.sort(Comparator.comparingDouble(Shape3D::zAvgAccordingToCameraAngles));
        g2d.addRenderingHints(Map.of(
                RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY,
                RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY,
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY,
                RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE,
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON,
                RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON,
                RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE,
                RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC
        ));
        list.forEach(r -> r.renderIfInView(g2d));
        stream().filter(e -> !(e instanceof Area)).forEach(render -> render.renderIfInView(g2d));
//        forEach(e -> e.render(g2d));
    }

    public int getRenderCounter() {
        return renderCounter;
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public void resetCounters() {
        renderCounter = 0;
        tickCounter = 0;
    }

    public synchronized void asyncTickCounterChange(int change) {
        tickCounter += change;
    }

    public boolean singleThreadedTick() {
        return lastTickTime.get() < MAX_SINGLE_THREADED_TICK_TIME;
    }

    public long tickRoundTime() {
        return lastTickTime.get();
    }

    public long renderRoundTime() {
        return lastRenderTime;
    }

    public BufferedImage getFrame(int width, int height) {
        return getFrame(width, height, false);
    }

    public BufferedImage getFrame(int width, int height, boolean antiAlias) {
        var res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        var g2d = res.createGraphics();
        if (antiAlias)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        render(g2d);
        g2d.dispose();
        return res;
    }

    public void saveFrame(String fileAddress, int width, int height, boolean antiAlias) throws IOException {
        Utils.saveRenderedImage(getFrame(width, height, antiAlias), fileAddress, "png");
    }

    public void setFrameDimension(int width, int height) {
        frameDimension = new Dimension(width, height);
    }

    public void setPathToSaveFrameSequence(String dir) {
        this.frameSequencePath = dir;
    }

    @Deprecated
    public static RenderManager resetAndGetUnsafeRenderManager() {
        unsafe.clear();
        return unsafe;
    }

    @Override
    public void tick() {
        if (lastTickTime.get() < MAX_SINGLE_THREADED_TICK_TIME) {
            var t = System.currentTimeMillis();
            forEach(Tick::tick);
            lastTickTime.set(System.currentTimeMillis() - t);
            tickCounter++;
        } else {
            tickExecutor.execute(tickRunnable);
        }
    }
}
