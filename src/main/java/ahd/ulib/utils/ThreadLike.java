package ahd.ulib.utils;

public abstract class ThreadLike implements Runnable {
    private Thread thread;
    protected final String threadName;
    private int runningCount;

    public ThreadLike(String threadName) {
        this.threadName = threadName;
        runningCount = 0;
        thread = null;
    }

    public ThreadLike() {
        this(Thread.currentThread().getName());
    }

    public boolean isRunning() {
        return thread != null;
    }

    public void start() {
        if (isRunning())
            return;
        runningCount++;
        thread = new Thread(this, threadName);
        thread.start();
    }

    public void join() {
        if (!isRunning())
            return;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            thread = null;
        }
    }

    public int getRunningCount() {
        return runningCount;
    }

    public String getThreadName() {
        return threadName;
    }

    @Override
    public String toString() {
        return "Thread->" + threadName + "::" + (isRunning() ? "running" : "stopped");
    }
}
