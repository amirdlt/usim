package com.usim.ulib.notmine.tmp4.controller;

import com.usim.ulib.notmine.tmp4.model.DownloadManager;
import com.usim.ulib.notmine.tmp4.view.WindowFrame;

import javax.swing.*;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class Controller implements Runnable {

    private final DownloadManager downloadManager = new DownloadManager();
    private final Map<Long, Timer> updaters = new HashMap<>();
    private final WindowFrame window = new WindowFrame() {

        @Override
        protected void addDownloadUrl(String address) {
            try {
                final var id = downloadManager.addDownload(address, 8, "");
                addRow(id, downloadManager.recordName(id), downloadManager.size(id));
                updaters.put(id, new Timer(200,
                        e -> updateRow(id, downloadManager.speed(id), downloadManager.remainedTime(id),
                                downloadManager.downloadedSize(id), downloadManager.progress(id),
                                downloadManager.state(id))));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void start(long id) {
            downloadManager.start(id);
            updaters.get(id).start();
        }

        @Override
        protected void pause(long id) {
            downloadManager.pause(id);
        }

        @Override
        protected void delete(long id) {
            if (downloadManager.file(id).delete())
                System.err.println("AHD:: Removed");
            downloadManager.pause(id);
            updaters.get(id).stop();
            downloadManager.removeRecord(id);
        }

        @Override
        protected void startAll() {
            downloadManager.startAll();
            updaters.values().forEach(Timer::start);
        }

        @Override
        protected void pauseAll() {
            downloadManager.pauseAll();
            updaters.values().forEach(Timer::stop);
        }

        @Override
        protected void acquire(long id) {
            downloadManager.acquire(id);
        }

        @Override
        protected void stopUpdater(long id) {
            updaters.get(id).stop();
        }
    };

    @Override
    public void run() {
        window.run();
    }
}
