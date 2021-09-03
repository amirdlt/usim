package com.usim.ulib.notmine.tmp4.model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.usim.ulib.notmine.tmp4.model.DownloadState.*;

public class DownloadThread implements Runnable {
    public static final int BLOCK_SIZE = 4096;
    public static final int BUFFER_SIZE = 4096;
    public static final int MIN_DOWNLOAD_SIZE = BLOCK_SIZE * 100;

    private final String outputFile;
    private long startByte;
    private final long endByte;
    private boolean isFinished;
    private Thread thread;
    private final DownloadRecord record;

    public DownloadThread(DownloadRecord record, long startByte, long endByte) {
        this.record = record;
        this.outputFile = record.directory + record.fileName;
        this.startByte = startByte;
        this.endByte = endByte;
        isFinished = false;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void start() {
        if (thread != null)
            return;
        thread = new Thread(this, "Download->" + record.fileName);
        thread.start();
    }

    public void join() {
        if (thread == null)
            return;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            thread = null;
            isFinished = true;
        }
    }

    @Override
    public void run() {
        BufferedInputStream in = null;
        RandomAccessFile raf = null;

        try {
            var conn = record.url.openConnection();
            conn.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
            conn.connect();
            in = new BufferedInputStream(conn.getInputStream());
            raf = new RandomAccessFile(outputFile, "rw");
            raf.seek(startByte);
            byte[] data = new byte[BUFFER_SIZE];
            int numRead;
            while (record.getState() == DOWNLOADING && (numRead = in.read(data, 0, BUFFER_SIZE)) != -1) {
                raf.write(data, 0, numRead);
                startByte += numRead;
                record.downloaded(numRead);
            }
            if (record.getState() == DOWNLOADING)
                isFinished = true;
        } catch (IOException e) {
            record.error();
        } finally {
            thread = null;
            try {
                if (raf != null)
                    raf.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
