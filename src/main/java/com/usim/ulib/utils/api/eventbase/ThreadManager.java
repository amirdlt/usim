package com.usim.ulib.utils.api.eventbase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

// thread safe class
@SuppressWarnings("unused")
public class ThreadManager<K> {
    private final Map<K, Semaphore> semaphoreMap;
    private final Map<K, AtomicBoolean> isRunning;
    private final Map<K, Callable<?>> callableMap;
    private final Map<K, Runnable> runnableMap;
    private final Map<K, Integer> taskExecutionCount;
    private final Map<K, Future<?>> results;
    private ExecutorService executor;
    private final Object globalMutex;

    public ThreadManager(ExecutorService executor) {
        semaphoreMap = new HashMap<>();
        callableMap = new HashMap<>();
        isRunning = new HashMap<>();
        taskExecutionCount = new HashMap<>();
        results = new HashMap<>();
        runnableMap = new HashMap<>();
        globalMutex = new Object();
        this.executor = executor;
    }

    public ThreadManager(int poolSize) {
        this(Executors.newFixedThreadPool(poolSize));
    }

    public void execute(K key) {
        synchronized (globalMutex) {
            taskExecutionCount.put(key, taskExecutionCount.getOrDefault(key, 0) + 1);
            if (callableMap.containsKey(key)) {
                results.put(key, executor.submit(callableMap.get(key)));
            } else if (runnableMap.containsKey(key)) {
                results.put(key, executor.submit(runnableMap.get(key)));
            }
        }
    }

    public void execute(K key, Callable<?> callable) {
        synchronized (globalMutex) {
            checkKeyDuplication(key);
            taskExecutionCount.put(key, taskExecutionCount.get(key) + 1);
            callableMap.put(key, callable);
            results.put(key, executor.submit(callable));
        }
    }

    public void execute(K key, Runnable runnable) {
        synchronized (globalMutex) {
            checkKeyDuplication(key);
            taskExecutionCount.put(key, taskExecutionCount.get(key) + 1);
            runnableMap.put(key, runnable);
            results.put(key, executor.submit(runnable));
        }
    }

    public void addTask(K key, Callable<?> callable) {
        synchronized (globalMutex) {
            checkKeyDuplication(key);
            taskExecutionCount.put(key, 0);
            callableMap.put(key, callable);
        }
    }

    public void addTask(K key, Runnable runnable) {
        synchronized (globalMutex) {
            checkKeyDuplication(key);
            taskExecutionCount.put(key, 0);
            runnableMap.put(key, runnable);
        }
    }

    public void addRepeatedlyRunnable(K key, Runnable runnable, @Nullable Runnable afterDoneRunnable, @NotNull Semaphore semaphore, int acquirePermits) {
        synchronized (globalMutex) {
            checkKeyDuplication(key);
            semaphoreMap.put(key, semaphore);
            isRunning.put(key, new AtomicBoolean());
            runnableMap.put(key, () -> {
                while (isRunning.get(key).get()) {
                    try {
                        semaphore.acquire(acquirePermits);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runnable.run();
                }
                if (afterDoneRunnable != null)
                    afterDoneRunnable.run();
            });
        }
    }

    public void addRepeatedlyRunnable(K key, Runnable runnable) {
        addRepeatedlyRunnable(key, runnable, null, new Semaphore(0), 1);
    }

    public void addRepeatedlyRunnable(K key, Runnable runnable, @Nullable Runnable afterDoneRunnable) {
        addRepeatedlyRunnable(key, runnable, afterDoneRunnable, new Semaphore(0), 1);
    }

    public void removeTask(K key) {
        synchronized (globalMutex) {
            callableMap.remove(key);
            runnableMap.remove(key);
        }
    }

    public Class<? extends ExecutorService> getExecutorClass() {
        synchronized (globalMutex) {
            return executor.getClass();
        }
    }

    public void setExecutor(ExecutorService executor) {
        synchronized (globalMutex) {
            terminate();
            this.executor = executor;
        }
    }

    public int getRunnableExecutionCount(K key) {
        synchronized (globalMutex) {
            return taskExecutionCount.get(key);
        }
    }

    public Map<K, Callable<?>> getCallableMap() {
        synchronized (globalMutex) {
            return Map.copyOf(callableMap);
        }
    }

    public void terminate() {
        synchronized (globalMutex) {
            if (executor.isTerminated())
                return;
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS))
                        System.err.println("Pool did not terminate");
                }
            } catch (InterruptedException ie) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isTerminated() {
        synchronized (globalMutex) {
            return executor.isTerminated();
        }
    }

    public boolean isNewRunnableAcceptable() {
        synchronized (globalMutex) {
            return executor.isShutdown();
        }
    }

    public Future<?> getResult(K key) {
        synchronized (globalMutex) {
            return results.get(key);
        }
    }

    public int unfinishedTaskCount() {
        synchronized (globalMutex) {
            return (int) results.values().stream().filter(Future::isDone).count();
        }
    }

    public boolean isAllTaskDone() {
        synchronized (globalMutex) {
            return results.values().stream().allMatch(Future::isDone);
        }
    }

    public boolean isTaskDone(K key) {
        synchronized (globalMutex) {
            return results.get(key).isDone();
        }
    }

    public boolean isRunningForRepeatedlyRunnable(K key) {
        return isRunning.get(key).get();
    }

    public void stopRepeatedlyRunnable(K key) {
        var isRunning = this.isRunning.get(key);
        if (isRunning == null || !isRunning.get())
            return;
        isRunning.set(false);
        synchronized (globalMutex) {
            try {
                results.get(key).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void startRepeatedlyRunnable(K key) {
        var isRunning = this.isRunning.get(key);
        if (isRunning == null || isRunning.get())
            return;
        isRunning.set(true);
        execute(key);
    }

    public Semaphore getSemaphoreOfRepeatedlyRunnable(K key) {
        synchronized (globalMutex) {
            return semaphoreMap.get(key);
        }
    }

    private void checkKeyDuplication(K key) {
        if (callableMap.containsKey(key) || runnableMap.containsKey(key))
            throw new IllegalArgumentException("Duplicated key. Cannot add or replace tasks with the same key.");
    }
}
