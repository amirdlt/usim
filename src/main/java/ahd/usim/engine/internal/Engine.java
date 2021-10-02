package ahd.usim.engine.internal;

import ahd.usim.engine.gui.swing.EngineRuntimeToolsPanel;
import ahd.usim.engine.internal.api.Logic;
import ahd.usim.engine.gui.swing.EngineRuntimeToolsFrame;
import ahd.usim.engine.internal.api.Rebuild;
import ahd.usim.ulib.utils.annotation.Constraint;
import ahd.usim.ulib.utils.annotation.NotFinal;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46C;

import javax.swing.*;
import java.util.concurrent.*;

import static ahd.usim.engine.Constants.*;

@SuppressWarnings("unused")
public final class Engine implements Rebuild {
    private static Engine engine = null;

    private boolean on;
    private final Semaphore startLoopLock;

    private final Window window;
    private final ScheduledThreadPoolExecutor executor;
    private final Semaphore renderSynchronizer;
    private int inputSynchronizer;
    private final @NotFinal Semaphore updateSynchronizer;
    private final Input input;
    private final EngineTimer timer;

    private EngineRuntimeToolsFrame engineRuntimeToolsFrame;
    private boolean working;
    private boolean initialized;
    private boolean doUpdate;
    private boolean doRender;
    private boolean doInput;
    private boolean useRenderSynchronizer;
    private boolean useUpdateSynchronizer;
    private Logic logic;
    private float renderTime;
    private float updateTime;
    private float inputTime;
    private float fps;
    private float ups;
    private float ips;
    private int _fps;
    private int _ups;
    private int _ips;
    private long renderCount;
    private long inputCount;
    private long updateCount;
    private long frameLoss;
    private long totalFrameLoss;
    private long updateAsyncCount;
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
        updateSynchronizer = new Semaphore(0);
        startLoopLock = new Semaphore(1);
        renderTime = 0;
        inputTime = 0;
        updateTime = 0;
        fps = _fps = 0;
        ups = _ups = 0;
        ips = _ips = 0;
        renderCount = 0;
        updateCount = 0;
        inputCount = 0;
        frameLoss = 0;
        updateAsyncCount = 0;
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
        useRenderSynchronizer = true;
        useUpdateSynchronizer = false;
        committedCommandsToMainThread = null;
    }

    private void _start() {
        if (working)
            return;
        if (logic == null)
            throw new IllegalStateException("AHD:: Please first set a logic for this engine.");
        working = true;
        renderSynchronizer.drainPermits();
        updateSynchronizer.drainPermits();
        renderSynchronizer.release(targetUps);
        inputSynchronizer = targetUps;
//        updateSynchronizer_ = 0;
        ScheduledFuture<?> update = null;
        inputCount++;
        try {
            initialize();
            timer.start();
            update = executor.scheduleAtFixedRate(() -> {
                try {
                    _input();
                    _update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, NANO / targetUps, TimeUnit.NANOSECONDS);
            long rTime = 0;
            while (working && !window.windowShouldClose()) {
                var t = System.currentTimeMillis();
                _render();
                totalFrameLoss += frameLoss = renderSynchronizer.availablePermits();
                rTime += System.currentTimeMillis() - t;
                if (rTime >= 1_000) {
                    fps = _fps * MILLI_F / rTime;
                    ups = _ups * MILLI_F / rTime;
                    ips = _ips * MILLI_F / rTime;
                    _fps = _ups = _ips = 0;
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
            if (window.windowShouldClose() || !on)
                cleanup();
        }
    }

    @Override
    public void initialize() {
        if (initialized)
            return;
        initialized = true;
        window.init();
        input.init();
        logic.initialize();
        if (engineRuntimeToolsFrame == null) {
            SwingUtilities.invokeLater(engineRuntimeToolsFrame = new EngineRuntimeToolsFrame());
        } else if (!engineRuntimeToolsFrame.isVisible()) {
            SwingUtilities.invokeLater(engineRuntimeToolsFrame);
        }
    }

    @Override
    public void cleanup() {
        if (!initialized)
            return;
        logic.cleanup();
        window.cleanup();
        engineRuntimeToolsFrame.dispose();
        initialized = false;
        timer.reset();
        startLoopLock.drainPermits();
        System.err.println("EngineWindowClosed -> System.exit");
        System.exit(0);
    }

    @Override
    public boolean isCleaned() {
        return on && !initialized;
    }

    private void _input() {
        if (inputSynchronizer < targetUps)
            return;
        else
            inputSynchronizer -= targetUps;
        var t = System.nanoTime();
        if (doInput) {
            input.input();
            logic.input();
            inputCount++;
            _ips++;
        }
        inputTime = (System.nanoTime() - t) / MILLION_F;
    }

    private void _update() throws InterruptedException {
        if (useUpdateSynchronizer)
            if (!updateSynchronizer.tryAcquire((long) (updateTime * 2), TimeUnit.MILLISECONDS))
                updateAsyncCount++;
        var t = System.nanoTime();
        if (doUpdate) {
            logic.update();
            updateCount++;
            _ups++;
            if (useRenderSynchronizer)
                renderSynchronizer.release(targetFps);
            inputSynchronizer += targetIps;
        }
        updateTime = (System.nanoTime() - t) / MILLION_F;
    }

    private void _render() {
        if (useRenderSynchronizer)
            renderSynchronizer.acquireUninterruptibly(targetUps);
        var tt = System.nanoTime();
        if (doRender) {
            logic.render();
            renderCount++;
            _fps++;
            if (useUpdateSynchronizer)
                updateSynchronizer.release();
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

    @Constraint(reasons = "ThreadMustBeMain")
    public void turnon() {
        if (!Thread.currentThread().getName().equals("main"))
            throw new RuntimeException("AHD:: Engine must be turned on inside the main thread due to lwjgl constraints.");
        if (on)
            return;
        var oldPriority = Thread.currentThread().getPriority();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        on = true;
        while (on) {
            startLoopLock.acquireUninterruptibly();
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

    @Constraint(reasons = "TheadMustBeMain")
    public void rebuildWindow() {
        logic.cleanup();
        window.rebuild();
        logic.initialize();
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

    public void setLogic(@NotNull Logic logic) {
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

    public long getUpdateAsyncCount() {
        return updateAsyncCount;
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
        var working = this.working;
        stop();
        this.targetFps = Math.min(targetFps, targetUps);
        if (working)
            start();
    }

    public void setTargetUps(int targetUps) {
        var working = this.working;
        stop();
        this.targetUps = targetUps;
        targetFps = Math.min(targetUps, targetFps);
        targetIps = Math.min(targetIps, targetUps);
        if (working)
            start();
    }

    public void setTargetIps(int targetIps) {
        var working = this.working;
        stop();
        this.targetIps = Math.min(targetIps, targetUps);
        if (working)
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

    public boolean isUseRenderSynchronizer() {
        return useRenderSynchronizer;
    }

    public void setUseRenderSynchronizer(boolean useRenderSynchronizer) {
        if (this.useRenderSynchronizer == useRenderSynchronizer)
            return;
        this.useRenderSynchronizer = useRenderSynchronizer;
        if (!useRenderSynchronizer)
            renderSynchronizer.release(targetUps);
        var working = this.working;
        stop();
        if (working)
            start();
    }

    public boolean isUseUpdateSynchronizer() {
        return useUpdateSynchronizer;
    }

    public void setUseUpdateSynchronizer(boolean useUpdateSynchronizer) {
        if (this.useUpdateSynchronizer == useUpdateSynchronizer)
            return;
        this.useUpdateSynchronizer = useUpdateSynchronizer;
        updateSynchronizer.release();
        renderSynchronizer.release(targetUps);

        var working = this.working;
        stop();
        if (working)
            start();
    }

    public static @NotNull Engine build(String windowTitle, int width, int height, boolean vSync) {
        if (engine != null && engine.on)
            engine.turnoff();
        return engine = new Engine(windowTitle, width, height, vSync);
    }

    public static @NotNull Engine getEngine() {
        return engine != null ? engine : build(DEFAULT_GLFW_WINDOW_NAME, DEFAULT_GLFW_WINDOW_WIDTH, DEFAULT_GLFW_WINDOW_HEIGHT, false);
    }

    @Override
    @Constraint(reasons = "ThreadMustBeMain")
    public void rebuild() {
        turnoff();
        turnon();
    }
}
