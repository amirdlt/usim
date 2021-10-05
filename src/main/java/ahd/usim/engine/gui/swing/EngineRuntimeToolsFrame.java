package ahd.usim.engine.gui.swing;

import ahd.ulib.swingutils.MainFrame;
import ahd.usim.engine.Constants;

import javax.swing.*;

public class EngineRuntimeToolsFrame extends MainFrame {
    private final EngineRuntimeToolsPanel mainPanel;

    public EngineRuntimeToolsFrame() {
        super("AHD:: Engine Runtime Tools");
        add(mainPanel = new EngineRuntimeToolsPanel());
        init();
    }

    private void init() {
        setSize(720, 620);
        setTrayIconPath(Constants.DEFAULT_SWING_ICON_PATH);
        setIconImage(new ImageIcon(Constants.DEFAULT_SWING_ICON_PATH).getImage());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        mainPanel.startUpdater();
    }

    @Override
    protected void closeAction() {
        mainPanel.stopUpdater();
        gotoSystemTray();
    }

    @Override
    public void gotoSystemTray() {
        mainPanel.stopUpdater();
        super.gotoSystemTray();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) mainPanel.startUpdater(); else mainPanel.stopUpdater();
    }
}
