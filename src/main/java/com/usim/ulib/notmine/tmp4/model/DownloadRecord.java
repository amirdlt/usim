package com.usim.ulib.notmine.tmp4.model;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;

import static com.usim.ulib.notmine.tmp4.model.DownloadState.*;
import static com.usim.ulib.notmine.tmp4.model.DownloadThread.*;

public class DownloadRecord implements Runnable {
    public final long id;
    public final String recordName;
    public final URL url;
    private long downloaded;
    public final int numOfThreads;
    public final String directory;
    public final String fileName;
    public final long size;
    private DownloadState state;
    private final List<DownloadThread> downloadThreads;
    private Thread thread;
    private long elapsedTime;
    private long preTime;
    public final Semaphore semaphore;

    public DownloadRecord(URL url, int numOfThreads, String directory, String recordName) {
        this.url = url;
        this.numOfThreads = Math.max(Math.min(numOfThreads, 64), 1);
        this.directory = directory;
        fileName = getFileName(url);
        this.recordName = recordName != null ? recordName : fileName;
        semaphore = new Semaphore(0);
        elapsedTime = 0;
        downloadThreads = new ArrayList<>();
        state = NOT_STARTED;
        downloaded = 0;
        id = System.nanoTime();
        size = size(url);
        thread = null;
    }

    private static long size(URL url) {
        URLConnection conn = null;
        try {
            conn = url.openConnection();
            conn.setConnectTimeout(10_000);
            conn.connect();
            return conn.getContentLengthLong();
        } catch (IOException e) {
            return -1;
        } finally {
            if (conn instanceof HttpURLConnection connection)
                connection.disconnect();
        }
    }

    public DownloadRecord(URL url, int numOfThreads, String directory) {
        this(url, numOfThreads, directory, getFileName(url));
    }

    public DownloadRecord(URL url, int numOfThreads) {
        this(url, numOfThreads, "");
    }

    public DownloadRecord(String url, int numOfThreads) throws MalformedURLException {
        this(new URL(url.trim()), numOfThreads);
    }

    public DownloadRecord(String url) throws MalformedURLException {
        this(url, 8);
    }

    public long getDownloadedSize() {
        return downloaded;
    }

    protected synchronized void downloaded(long change) {
        downloaded += change;
        var delta = System.nanoTime() - preTime;
        elapsedTime += delta;
        preTime = System.nanoTime();
        semaphore.release();
    }

    public double getAverageSpeed() {
        return elapsedTime == 0 ? Double.NaN : downloaded / (double) elapsedTime * 1_000_000_000;
    }

    public double getElapsedTime() {
        return elapsedTime / 1_000_000_000f;
    }

    public double getRemainedTime() {
        var avg = getAverageSpeed();
        return avg == 0 ? Double.NaN : ((size - downloaded) / avg);
    }

    public double getProgress() {
        return size == 0 ? 0 : (double) downloaded / size;
    }

    public DownloadState getState() {
        return state;
    }

    public void download() {
        if (thread != null || state == DOWNLOADING || state == COMPLETED)
            return;
        state = DOWNLOADING;
        thread = new Thread(this);
        preTime = System.nanoTime();
        thread.start();
    }

    public void pause() {
        try {
            state = PAUSED;
            thread.join();
        } catch (Exception ignore) {
        } finally {
            thread = null;
        }
    }

    public boolean isFinished() {
        return state == COMPLETED;
    }

    private static String getFileName(URL url) {
        String tmp;
        return URLDecoder.decode((tmp = url.getFile().substring(url.getFile().lastIndexOf('/') + 1)).
                substring(0, tmp.indexOf('?') >= 0 ? tmp.indexOf('?') : tmp.length()), StandardCharsets.UTF_8);
    }

    @Override
    public void run() {
        URLConnection conn = null;
        semaphore.drainPermits();
        try {
            conn = url.openConnection();
            conn.setConnectTimeout(10_000);
            conn.connect();
            if (size < 1)
                throw new RuntimeException("Could not retrieve file size: " + url);
            if (state == DOWNLOADING) {
                if (downloadThreads.isEmpty()) {
                    if (size > MIN_DOWNLOAD_SIZE) {
                        int partSize = Math.round(((float) size / numOfThreads) / BLOCK_SIZE) * BLOCK_SIZE;
                        int startByte = 0;
                        int endByte = partSize - 1;
                        var thread = new DownloadThread(this, startByte, endByte);
                        downloadThreads.add(thread);
                        while (endByte < size) {
                            startByte = endByte + 1;
                            endByte += partSize;
                            thread = new DownloadThread(this, startByte, endByte);
                            downloadThreads.add(thread);
                        }
                    } else {
                        downloadThreads.add(new DownloadThread(this, 0, size));
                    }
                    downloadThreads.forEach(DownloadThread::start);
                } else {
                    downloadThreads.stream().filter(t -> !t.isFinished()).forEach(DownloadThread::start);
                }
                downloadThreads.forEach(DownloadThread::join);
            }
        } catch (Exception e) {
            error();
        } finally {
            thread = null;
            if (state == DOWNLOADING) {
                downloaded = size;
                state = COMPLETED;
            }
            if (conn instanceof HttpURLConnection connection)
                connection.disconnect();
        }
    }

    public void error() {
        state = ERROR;
        System.err.println("ERROR OCCURRED");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DownloadRecord that = (DownloadRecord) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DownloadRecord{" + "id=" + id + ", recordName='" + recordName + '\'' + ", url=" + url + ", downloaded="
                + downloaded + ", numOfThreads=" + numOfThreads + ", directory='" + directory + '\'' + ", fileName='"
                + fileName + '\'' + ", size=" + size + ", state=" + state + ", elapsedTime=" + elapsedTime + '}';
    }
}
