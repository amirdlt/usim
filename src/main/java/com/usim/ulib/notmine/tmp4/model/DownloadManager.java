package com.usim.ulib.notmine.tmp4.model;

import com.usim.ulib.utils.api.SemaphoreBase;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class DownloadManager implements SemaphoreBase<Long> {
    private final Map<Long, Semaphore> semaphoreMap;
    private final Map<Long, DownloadRecord> recordMap;

    public DownloadManager() {
        recordMap = new HashMap<>();
        semaphoreMap = new HashMap<>();
    }

    public long addDownload(URL url, int numOfThreads, String directory, String recordName) {
        var d = new DownloadRecord(url, numOfThreads, directory, recordName);
        recordMap.put(d.id, d);
        addSemaphore(d.id, d.semaphore);
        return d.id;
    }

    public long addDownload(String url, int numOfThreads, String directory, String recordName)
            throws MalformedURLException {
        return addDownload(new URL(url), numOfThreads, directory, recordName);
    }

    public long addDownload(String url, int numOfThreads, String directory) throws MalformedURLException {
        return addDownload(new URL(url), numOfThreads, directory, null);
    }

    public void start(long id) {
        recordMap.get(id).download();
    }

    public void pause(long id) {
        recordMap.get(id).pause();
    }

    public void startAll() {
        recordMap.values()
                .stream()
                .filter(e -> e.getState() == DownloadState.NOT_STARTED || e.getState() == DownloadState.PAUSED)
                .forEach(DownloadRecord::download);
    }

    public File file(long id) {
        var r = recordMap.get(id);
        return new File(r.directory + r.fileName);
    }

    public void pauseAll() {
        recordMap.values().stream().filter(e -> e.getState() == DownloadState.DOWNLOADING)
                .forEach(DownloadRecord::pause);
    }

    public Semaphore semaphore(long id) {
        return recordMap.get(id).semaphore;
    }

    public void removeRecord(long id) {
        recordMap.remove(id);
        semaphoreMap.remove(id);
    }

    public String recordName(long id) {
        return recordMap.get(id).recordName;
    }

    public long size(long id) {
        return recordMap.get(id).size;
    }

    public double speed(long id) {
        return recordMap.get(id).getAverageSpeed();
    }

    public double remainedTime(long id) {
        return recordMap.get(id).getRemainedTime();
    }

    public long downloadedSize(long id) {
        return recordMap.get(id).getDownloadedSize();
    }

    public DownloadState state(long id) {
        return recordMap.get(id).getState();
    }

    public double progress(long id) {
        return recordMap.get(id).getProgress();
    }

    @Override
    public Map<Long, Semaphore> getSemaphoreMap() {
        return semaphoreMap;
    }
}
