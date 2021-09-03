package com.usim.ulib.notmine.tmp4.view;

import com.usim.ulib.notmine.tmp4.model.DownloadState;
import com.usim.ulib.swingutils.MainFrame;

public abstract class WindowFrame extends MainFrame {

    private DownloadPanel downloadPanel;

    public WindowFrame() {
        super("Download Manager");
        init();
    }

    private void init() {
        addState("download-main", downloadPanel = new DownloadPanel() {
            @Override
            protected void addDownloadUrl(String address) {
                WindowFrame.this.addDownloadUrl(address);
            }

            @Override
            protected void removeWithFile(long id) {
                if (id == -1)
                    return;
                delete(id);
            }

            @Override
            protected void start(long id) {
                if (id == -1)
                    return;
                WindowFrame.this.start(id);
            }

            @Override
            protected void pause(long id) {
                if (id == -1)
                    return;
                WindowFrame.this.pause(id);
            }

            @Override
            protected void startAll() {
                WindowFrame.this.startAll();
            }

            @Override
            protected void pauseAll() {
                WindowFrame.this.pauseAll();
            }

            @Override
            protected void stopUpdater(long id) {
                WindowFrame.this.stopUpdater(id);
            }
        });

        setState("download-main");
    }

    protected void addRow(long id, String name, long size) {
        downloadPanel.addRow(id, name, size / 1024);
    }
    protected void updateRow(long id, double speed, double remainedTime, long size, double progress, DownloadState state) {
        acquire(id);
        try {
            downloadPanel.updateTable(id, speed, remainedTime, size / 1024, progress, state);
        } catch (Exception ignore) {}
    }

    protected abstract void addDownloadUrl(String address);
    protected abstract void start(long id);
    protected abstract void pause(long id);
    protected abstract void delete(long id);
    protected abstract void startAll();
    protected abstract void pauseAll();
    protected abstract void acquire(long id);
    protected abstract void stopUpdater(long id);
}
