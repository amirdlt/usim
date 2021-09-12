package ahd.usim.engine.internal;

import ahd.usim.engine.logic.Logic;
import ahd.usim.engine.swing.EngineRuntimeToolsFrame;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.concurrent.*;

import static ahd.usim.engine.Constants.*;

@SuppressWarnings("unused")
public final class Engine {
    private static Engine engine = null;

    private boolean on;
    private final Semaphore startLoopLock;

    private final Window window;
    private final ScheduledThreadPoolExecutor executor;
    private final Semaphore renderSynchronizer;
    private final Input input;
    private final EngineTimer timer;

    private EngineRuntimeToolsFrame engineRuntimeToolsFrame;
    private boolean working;
    private boolean initialized;
    private boolean doUpdate;
    private boolean doRender;
    private boolean doInput;
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
    private long frameLoss;
    private long totalFrameLoss;
    private int targetFps;
    private int targetUps;
    private int targetIps;
    private Runnable committedCommandsToMainThread;

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
        frameLoss = 0;
        totalFrameLoss = 0;
        targetFps = DEFAULT_TARGET_FPS;
        targetIps = DEFAULT_TARGET_IPS;
        targetUps = DEFAULT_TARGET_UPS;
        working = false;
        on = false;
        initialized = false;
        doInput = true;
        doUpdate = true;
        doRender = true;
        input = new Input();
        timer = new EngineTimer();
        committedCommandsToMainThread = null;
    }

    private void _start(int targetFps, int targetUps, int targetIps) {
        if (working)
            return;
        if (logic == null)
            throw new IllegalStateException("AHD:: Please first set a logic for this engine.");
        working = true;
        this.targetUps = targetUps;
        this.targetFps = targetFps;
        this.targetIps = targetIps;
        ScheduledFuture<?> update = null;
        try {
            init();
            var fps = 0;
            final var inputFactor = targetUps / targetIps == 0 ? 1 : targetUps / targetIps;
            timer.start();
            update = executor.scheduleAtFixedRate(() -> {
                if (updateCount % inputFactor == 0)
                    _input();
                _update();
                renderSynchronizer.release();
            }, 0, NANO / targetUps, TimeUnit.NANOSECONDS);
            long rTime = 0;
            final var renderFactor = targetUps / targetFps < 2 ? 1 : targetUps / targetFps;
            while (working && !window.windowShouldClose()) {
                var t = System.currentTimeMillis();
                renderSynchronizer.acquire(renderFactor);
                _render();
                fps++;
                totalFrameLoss += frameLoss = renderSynchronizer.availablePermits() / renderFactor;
                rTime += System.currentTimeMillis() - t;
                if (rTime >= 1_000) {
                    this.fps = fps * 1_000f / rTime;
                    ups = (fps * renderFactor + frameLoss) * MILLI_F / rTime;
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
            timer.stop();
            renderTime = 0;
            inputTime = 0;
            updateTime = 0;
            fps = 0;
            ups = 0;
            ips = 0;
            frameLoss = 0;
            working = false;
            if (committedCommandsToMainThread != null) {
                committedCommandsToMainThread.run();
                committedCommandsToMainThread = null;
            }
            if (window.windowShouldClose())
                cleanup();
        }
    }

    private void _start() {
        _start(targetFps, targetUps, targetIps);
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
        window.cleanup();
        engineRuntimeToolsFrame.dispose();
        initialized = false;
        timer.reset();
        startLoopLock.drainPermits();
        System.err.println("EngineWindowClosed -> System.exit");
        System.exit(0);
    }

    private void _input() {
        var t = System.nanoTime();
        if (doInput) {
            input.input();
            logic.input();
            inputCount++;
        }
        inputTime = (System.nanoTime() - t) / MILLION_F;
    }

    private void _update() {
        var t = System.nanoTime();
        if (doUpdate) {
            logic.update();
            updateCount++;
        }
        updateTime = (System.nanoTime() - t) / MILLION_F;
    }

    private void _render() {
        var tt = System.nanoTime();
        if (doRender) {
            logic.render();
            renderCount++;
        }
        window.update();
        renderTime = (System.nanoTime() - tt) / MILLION_F;
    }

    public void start() {
        if (!on) {
            turnon();
        } else {
            startLoopLock.release();
        }
    }

    public synchronized void stop() {
        if (!working)
            return;
        working = false;
    }

    /**
     * need to be run in main thread
     */
    public void turnon() {
        if (!Thread.currentThread().getName().equals("main"))
            throw new RuntimeException("AHD:: Engine must be turned on inside the main thread due to lwjgl constraints.");
        if (on)
            return;
        var oldPriority = Thread.currentThread().getPriority();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        on = true;
        while (on) {
            try {
                startLoopLock.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            _start();
        }
        cleanup();
        Thread.currentThread().setPriority(oldPriority);
    }

    public void turnoff() {
        if (!on)
            return;
        on = false;
        var oldRender = doRender;
        var oldUpdate = doUpdate;
        var oldInput = doInput;
        if (!working)
            start();
        stop();
        doUpdate = oldUpdate;
        doInput = oldInput;
        doRender = oldRender;
    }

    /**
     * needs to be invoked inside main thread
     */
    public void rebuildWindow() {
        logic.cleanup();
        window.rebuild();
        logic.init();
        input.init();
    }

    public void commitCommandsToMainThread(Runnable commands) {
        if (!on && !Thread.currentThread().getName().equals("main"))
            throw new RuntimeException("AHD:: Engine must be turned on inside main thread.");
        committedCommandsToMainThread = commands;
        var oldRender = doRender;
        var oldUpdate = doUpdate;
        var oldInput = doInput;
        if (!on) {
            doUpdate = doInput = doRender = false;
            _start();
            stop();
        } else if (working) {
            stop();
            start();
        } else {
            doUpdate = doInput = doRender = false;
            start();
            stop();
        }
        doUpdate = oldUpdate;
        doInput = oldInput;
        doRender = oldRender;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public float getFps() {
        return doRender ? fps : 0;
    }

    public float getIps() {
        return doInput ? ips : 0;
    }

    public float getInputTime() {
        return doInput ? inputTime : 0;
    }

    public float getRenderTime() {
        return doRender ? renderTime : 0;
    }

    public Logic getLogic() {
        return logic;
    }

    public float getUpdateTime() {
        return doUpdate ? updateTime : 0;
    }

    public float getUps() {
        return doUpdate ? ups : 0;
    }

    public float getAccumulatedFps() {
        return renderCount / timer.workingSeconds();
    }

    public float getAccumulatedUps() {
        return updateCount / timer.workingSeconds();
    }

    public float getAccumulatedIps() {
        return inputCount / timer.workingSeconds();
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

    public long getFrameLoss() {
        return frameLoss;
    }

    public long getTotalFrameLoss() {
        return totalFrameLoss;
    }

    public long getPureTotalFrameLoss() {
        return updateCount - renderCount;
    }

    public EngineTimer getTimer() {
        return timer;
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
        start();
    }

    public void setTargetUps(int targetUps) {
        stop();
        this.targetUps = targetUps;
        targetFps = Math.min(targetUps, targetFps);
        targetIps = Math.min(targetIps, targetUps);
        start();
    }

    public void setTargetIps(int targetIps) {
        stop();
        this.targetIps = Math.min(targetIps, targetUps);
        start();
    }

    public boolean isWorking() {
        return working;
    }

    public boolean isOn() {
        return on;
    }

    public boolean isDoUpdate() {
        return doUpdate;
    }

    public void setDoUpdate(boolean doUpdate) {
        this.doUpdate = doUpdate;
    }

    public boolean isDoRender() {
        return doRender;
    }

    public void setDoRender(boolean doRender) {
        this.doRender = doRender;
    }

    public boolean isDoInput() {
        return doInput;
    }

    public void setDoInput(boolean doInput) {
        this.doInput = doInput;
    }

    public Window getWindow() {
        return window;
    }

    public Input getInput() {
        return input;
    }

    public Camera getCamera() {
        return logic.camera();
    }

    public static @NotNull Engine build(String windowTitle, int width, int height, boolean vSync) {
        if (engine != null && engine.on)
            engine.turnoff();
        return engine = new Engine(windowTitle, width, height, vSync);
    }

    public static @NotNull Engine getEngine() {
        return engine != null ? engine : build(DEFAULT_GLFW_WINDOW_NAME, DEFAULT_GLFW_WINDOW_WIDTH, DEFAULT_GLFW_WINDOW_HEIGHT, false);
    }
}
