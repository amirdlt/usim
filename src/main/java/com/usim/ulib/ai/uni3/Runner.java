package com.usim.ulib.ai.uni3;

import com.usim.ulib.swingutils.MainFrame;
import com.usim.ulib.visualization.canvas.Graph3DCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.usim.ulib.utils.Utils.unsafeExecutor;

public class Runner {
    public static void run(String filePath) {
        var f = new MainFrame("Table Solver (CSP)");
        var gp = new Graph3DCanvas();
        var vt = new VisualTable(gp, VisualTable.readTableFromFile(filePath));
        var mp = new JPanel(new BorderLayout());
        f.addState("main", mp);
        mp.add(gp);
        f.setState("main");
        gp.addRender(vt);
        unsafeExecutor.execute(() -> System.out.println(vt.getAlgorithm().solve() ? "Solved" : "Not solved:: Error"));
        gp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                vt.getAlgorithm().release("solve");
            }
        });
        vt.getAlgorithm().getReleaseTimer("solve", 200).start();
        new Timer(10, e -> gp.repaint()).start();
        SwingUtilities.invokeLater(f);
    }
}
