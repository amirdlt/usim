package com.usim.engine.engine.internal;

import com.usim.engine.engine.logic.Logic;
import com.usim.engine.engine.swing.EngineRuntimeToolsFrame;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.concurrent.*;

import static com.usim.engine.engine.Constants.*;

@SuppressWarnings("unused")
public final class Engine {
    private static Engine engine = null;

    private boolean on;
    private final Semaphore startLoopLock;

    private final Window window;
    private final ScheduledThreadPoolExecutor executor;
    private final Semaphore renderSynchronizer;
    private final Input input;

    private EngineRuntimeToolsFrame engineRuntimeToolsFrame;
    private boolean working;
    private boolean initialized;
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
        engineRuntimeToolsFrame = null;
        executor = new ScheduledThreadPoolExecutor(1);
        renderSynchronizer = new Semaphore(0);
        startLoopLock = new Semaphore(1);
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
        on = false;
        initialized = false;
        input = new Input();
    }

    private void start(int targetFps, int targetUps, int targetIps) {
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

    private void start() {
        start(targetFps, targetUps, targetIps);
    }

    private void stop() {
        if (!working)
            return;
        working = false;
    }

    private void init() {
        if (initialized)
            return;
        initialized = true;
        window.init();
        input.init();
        logic.init();
        if (engineRuntimeToolsFrame == null) {
            SwingUtilities.invokeLater(engineRuntimeToolsFrame = new EngineRuntimeToolsFrame());
        } else if (!engineRuntimeToolsFrame.isVisible()) {
            SwingUtilities.invokeLater(engineRuntimeToolsFrame);
        }
    }

    private void cleanup() {
        logic.cleanup();
        engineRuntimeToolsFrame.dispose();
        window.cleanup();
        System.err.println("EngineWindowClosed -> System.exit");
        System.exit(0);
    }

    private void input() {
        input.input();
        logic.input();
    }

    private void update() {
        logic.update();
    }

    private void render() {
        logic.render();
        window.update();
    }

    private void _start() {
        startLoopLock.release();
    }

    public void turnon() {
        if (on)
            return;
        if (!Thread.currentThread().getName().equals("main"))
            throw new RuntimeException("AHD:: Engine must be turned on inside the main thread due to lwjgl constraints.");
        var oldPriority = Thread.currentThread().getPriority();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        if (engine == null)
            get();
        on = true;
        while (on) {
            try {
                startLoopLock.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            engine.start();
        }
        Thread.currentThread().setPriority(oldPriority);
    }

    public void turnoff() {
        if (!on)
            return;
        if (engine == null)
            get();
        on = false;
        engine.stop();
        cleanup();
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
        this.targetFps = Math.min(targetFps, targetUps);
        _start();
    }

    public void setTargetUps(int targetUps) {
        stop();
        this.targetUps = targetUps;
        targetFps = Math.min(targetUps, targetFps);
        targetIps = Math.min(targetIps, targetUps);
        _start();
    }

    public void setTargetIps(int targetIps) {
        stop();
        this.targetIps = Math.min(targetIps, targetUps);
        _start();
    }

    public boolean isWorking() {
        return working;
    }

    public Window getWindow() {
        return window;
    }

    public Input getInput() {
        return input;
    }

    public static @NotNull Engine get(String windowTitle, int width, int height, boolean vSync) {
        if (engine != null)
            engine.turnoff();
        return engine = new Engine(windowTitle, width, height, vSync);
    }

    public static @NotNull Engine get() {
        return engine != null ? engine : get(DEFAULT_GLFW_WINDOW_NAME, DEFAULT_GLFW_WINDOW_WIDTH, DEFAULT_GLFW_WINDOW_HEIGHT, false);
    }

    public static Window window() {
        return engine == null ? get().window : engine.window;
    }
}
