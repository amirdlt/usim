package com.usim.ulib.ai.uni2;

import com.usim.ulib.utils.Utils;
import com.usim.ulib.visualization.canvas.CoordinatedScreen;
import com.usim.ulib.visualization.canvas.Render;

import javax.swing.*;
import java.awt.*;

public class Visualization implements Render {
    private final GeneticAlgorithm algorithm;
    private final String level;
    private final CoordinatedScreen cs;

    private static final Image agentImage = new ImageIcon("tmp/agent.jpg").getImage();
    private static final Image brickImage = new ImageIcon("tmp/_.jpg").getImage();
    private static final Image mImage = new ImageIcon("tmp/m.jpg").getImage();
    private static final Image lImage = new ImageIcon("tmp/l.jpg").getImage();
    private static final Image gImage = new ImageIcon("tmp/g.jpg").getImage();
    private static final Image flagImage = new ImageIcon("tmp/Capture.jpg").getImage();

    public Visualization(CoordinatedScreen cs, String level) {
        this.level = level;
        this.algorithm = new GeneticAlgorithm(level);
        this.cs = cs;
    }

    public GeneticAlgorithm getAlgorithm() {
        return algorithm;
    }

    @Override
    public void render(Graphics2D g2d) {
        int sx = cs.screenX(1);
        int sy = cs.screenY(1);
        int offsetX = -10 + sx;
        int offsetY = -10 + sy;
        int len = cs.screenXLen(2);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (var ch : level.toCharArray()) {
            switch (ch) {
                case 'G' -> g2d.drawImage(gImage, offsetX += len, offsetY - 3 * len, len, len, null);
                case 'L' -> g2d.drawImage(lImage, offsetX += len, offsetY - 4 * len, len, len, null);
                case 'M' -> g2d.drawImage(mImage, offsetX += len, offsetY - 3 * len, len, len, null);
                case '_' -> offsetX += len;
            }
            g2d.drawImage(brickImage, offsetX, offsetY - 2 * len, len, len, null);
        }
        g2d.setColor(Color.magenta);
        g2d.drawImage(
                agentImage,
                sx + len - 10,
                offsetY - 4 * len,
                len,
                2 * len,
                null
        );
        offsetX += len;
        g2d.drawImage(brickImage, offsetX, offsetY - 2 * len, len, len, null);
        g2d.drawImage(
                flagImage,
                offsetX,
                offsetY - 6 * len,
                len,
                4 * len,
                null
        );
    }
}
