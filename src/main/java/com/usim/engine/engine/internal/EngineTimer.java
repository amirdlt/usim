package com.usim.engine.engine.internal;

import com.usim.ulib.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

import static com.usim.engine.engine.Constants.*;

public final class EngineTimer {
    private record TimeRange(long start, long end) {}

    private final Object mutex = new Object();

    private final List<TimeRange> workingRanges;
    private final List<TimeRange> idleRanges;
    private long startTime;
    private long endTime;
    private long idleAccumulator;
    private long workingAccumulator;

    EngineTimer() {
        workingRanges = new ArrayList<>();
        idleRanges = new ArrayList<>();
        startTime = 0;
        endTime = 0;
        idleAccumulator = 0;
        workingAccumulator = 0;
    }

    public void start() {
        if (startTime != 0)
            return;
        synchronized (mutex) {
            startTime = System.nanoTime();
            if (endTime != 0) {
                idleRanges.add(new TimeRange(endTime, startTime));
                idleAccumulator += startTime - endTime;
            }
        }
    }

    public void stop() {
        if (startTime == 0)
            return;
        synchronized (mutex) {
            workingRanges.add(new TimeRange(startTime, endTime = System.nanoTime()));
            workingAccumulator += endTime - startTime;
            startTime = 0;
        }
    }

    public void reset() {
        synchronized (mutex) {
            startTime = 0;
            endTime = 0;
            idleAccumulator = 0;
            workingAccumulator = 0;
            workingRanges.clear();
            idleRanges.clear();
        }
    }

    public @Unmodifiable List<TimeRange> getIdleRanges() {
        synchronized (mutex) {
            return List.copyOf(idleRanges);
        }
    }

    public @Unmodifiable List<TimeRange> getWorkingRanges() {
        synchronized (mutex) {
            return List.copyOf(workingRanges);
        }
    }

    public float idleMillis() {
        synchronized (mutex) {
            return (idleAccumulator + (startTime == 0 ? System.nanoTime() - endTime : 0)) / MILLION_F;
        }
    }

    public float workingMillis() {
        synchronized (mutex) {
            return (workingAccumulator + (startTime == 0 ? 0 : System.nanoTime() - startTime)) / MILLION_F;
        }
    }

    public float idleSeconds() {
        synchronized (mutex) {
            return (idleAccumulator + (startTime == 0 && endTime != 0 ? System.nanoTime() - endTime : 0)) / NANO_F;
        }
    }

    public float workingSeconds() {
        synchronized (mutex) {
            return (workingAccumulator + (startTime == 0 ? 0 : System.nanoTime() - startTime)) / NANO_F;
        }
    }

    public boolean isWorking() {
        synchronized (mutex) {
            return startTime != 0;
        }
    }

    public boolean isIdle() {
        synchronized (mutex) {
            return startTime == 0;
        }
    }

    @Override
    public @NotNull String toString() {
        return "WorkingTime: " + Utils.round(workingSeconds(), 2) + "sec | IdleTime: " + Utils.round(idleSeconds(), 2) + "sec";
    }
}
