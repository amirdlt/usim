package com.usim.engine.engine.internal;

import com.usim.engine.engine.logic.Logic;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

import static com.usim.engine.engine.Constants.*;

@SuppressWarnings("unused")
public final class Engine {
    private static Engine engine = null;

    private final Window window;
    private final ScheduledThreadPoolExecutor executor;
    private final Semaphore renderSynchronizer;
    private boolean working;
    private Logic logic;
    private float renderTime;
    private float updateTime;
    private float inputTime;
    private float fps;
    private float ups;
    private float ips;
    private long renderCount;
    private long inputCount;
    private long updateCount;
    private int frameLoss;
    private int targetFps;
    private int targetUps;
    private int targetIps;

    private Engine(String windowTitle, int width, int height, boolean vSync) {
        window = new Window(windowTitle, width, height, vSync);
        logic = null;
        executor = new ScheduledThreadPoolExecutor(1);
        renderSynchronizer = new Semaphore(0);
        renderTime = Float.NaN;
        inputTime = Float.NaN;
        updateTime = Float.NaN;
        fps = Float.NaN;
        ups = Float.NaN;
        ips = Float.NaN;
        renderCount = 0;
        updateCount = 0;
        inputCount = 0;
        frameLoss = -1;
        targetFps = DEFAULT_TARGET_FPS;
        targetIps = DEFAULT_TARGET_IPS;
        targetUps = DEFAULT_TARGET_UPS;
        working = false;
    }

    public void start(int targetFps, int targetUps, int targetIps) {
        if (working)
            return;
        working = true;
        if (logic == null)
            throw new IllegalStateException("AHD:: Please first set a logic for this engine.");
        this.targetUps = targetUps;
        this.targetFps = targetFps;
        this.targetIps = targetIps;
        ScheduledFuture<?> update = null;
        try {
            init();
            var fps = 0;
            final var inputFactor = targetUps / targetIps == 0 ? 1 : targetUps / targetIps;
            update = executor.scheduleAtFixedRate(() -> {
                if (updateCount % inputFactor == 0) {
                    var t = System.nanoTime();
                    input();
                    inputTime = (System.nanoTime() - t) / (float) MILLION;
                    inputCount++;
                }
                var t = System.nanoTime();
                update();
                updateTime = (System.nanoTime() - t) / (float) MILLION;
                renderSynchronizer.release();
                updateCount++;
            }, 0, NANO / targetUps, TimeUnit.NANOSECONDS);
            long rTime = 0;
            final var renderFactor = targetUps / targetFps == 0 ? 1 : targetUps / targetFps;
            while (working && !window.windowShouldClose()) {
                var t = System.currentTimeMillis();
                renderSynchronizer.acquire(renderFactor);
                var tt = System.nanoTime();
                render();
                renderTime = (System.nanoTime() - tt) / (float) MILLION;
                fps++;
                renderCount++;
                frameLoss = renderSynchronizer.availablePermits();
                rTime += System.currentTimeMillis() - t;
                if (rTime >= 1_000) {
                    this.fps = fps * 1_000f / rTime;
                    ups = (fps * renderFactor + frameLoss) * (float) MILLI / rTime;
                    ips = ups / inputFactor;
                    System.out.println(
                            "fps: " + this.fps + " ups: " + ups + " ips: " + ips + " render time: " + renderTime + " update time: "
                                    + updateTime + " frameLoss: " + frameLoss + " input time: " + inputTime + "  #n input: "
                                    + inputCount);
                    renderSynchronizer.drainPermits();
                    fps = 0;
                    rTime = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (update != null)
                update.cancel(true);
            working = false;
            if (window.windowShouldClose())
                cleanup();
        }
    }

    public void start() {
        start(DEFAULT_TARGET_FPS, DEFAULT_TARGET_UPS, DEFAULT_TARGET_IPS);
    }

    public void stop() {
        if (!working)
            return;
        working = false;
    }

    private void init() {
        window.init();
        logic.init();
    }

    private void cleanup() {
        logic.cleanup();
        window.cleanup();
    }

    private void input() {
        logic.input();
    }

    private void update() {
        logic.update();
    }

    private void render() {
        logic.render();
        window.update();
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public float getFps() {
        return fps;
    }

    public float getIps() {
        return ips;
    }

    public float getInputTime() {
        return inputTime;
    }

    public float getRenderTime() {
        return renderTime;
    }

    public Logic getLogic() {
        return logic;
    }

    public float getUpdateTime() {
        return updateTime;
    }

    public float getUps() {
        return ups;
    }

    public long getRenderCount() {
        return renderCount;
    }

    public long getInputCount() {
        return inputCount;
    }

    public long getUpdateCount() {
        return updateCount;
    }

    public int getFrameLoss() {
        return frameLoss;
    }

    public int getTargetFps() {
        return targetFps;
    }

    public int getTargetUps() {
        return targetUps;
    }

    public int getTargetIps() {
        return targetIps;
    }

    public void setTargetFps(int targetFps) {
        stop();
        this.targetFps = targetFps;
        start();
    }

    public void setTargetUps(int targetUps) {
        stop();
        this.targetUps = targetUps;
        start();
    }

    public void setTargetIps(int targetIps) {
        stop();
        this.targetIps = targetIps;
        start();
    }

    public boolean isWorking() {
        return working;
    }

    public Window getWindow() {
        return window;
    }

    public static @NotNull Engine get(String windowTitle, int width, int height, boolean vSync) {
        return engine == null ? engine = new Engine(windowTitle, width, height, vSync) : engine;
    }

    public static @NotNull Engine get() {
        return get(DEFAULT_GLFW_WINDOW_NAME, DEFAULT_GLFW_WINDOW_WIDTH, DEFAULT_GLFW_WINDOW_HEIGHT, false);
    }

    public static Window window() {
        if (engine == null)
            throw new IllegalStateException("AHD:: First invoke Engine.get to create an Engine.");
        return engine.window;
    }
}
