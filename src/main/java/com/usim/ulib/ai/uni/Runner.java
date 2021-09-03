package com.usim.ulib.ai.uni;

import com.usim.ulib.swingutils.MainFrame;
import com.usim.ulib.utils.Utils;
import com.usim.ulib.utils.supplier.StringSupplier;
import com.usim.ulib.visualization.canvas.CoordinatedScreen;
import com.usim.ulib.visualization.canvas.Graph3DCanvas;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class Runner {

    public static void run(PathFinderAlgorithm algorithm, String mapPath)
            throws FileNotFoundException {
        var res = new HashMap<Point, int[]>();
        var cells = PathFinderVisualPanel.loadFromFile(mapPath);
        var gp = new Graph3DCanvas();
        var vp = new PathFinderVisualPanel(gp, cells);
        gp.setShowInfo(false);
        gp.addRender(vp);
        var algo = vp.getAlgorithm();
        AtomicInteger stepCounter = new AtomicInteger(0);
        var f = new MainFrame();
        f.add(gp);
        SwingUtilities.invokeLater(f);
        var timer = algo.getReleaseTimer("step", 100);
        new Timer(35, e -> gp.repaint()).start();
        var initRobot = algo.findRobot();
        gp.addMouseListener(new MouseAdapter() {
            private boolean running = false;
            private boolean ignored = false;

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isShiftDown()) {
                    algo.release("step");
                } else if (e.isControlDown()) {
                    running = !running;
                    if (running)
                        timer.start();
                    else
                        timer.stop();
                } else if (e.isAltDown()) {
                    ignored = !ignored;
                    if (ignored)
                        algo.ignoreSemaphore("step");
                    else algo.drainPermits("step");
                }
            }
        });

        var notPossible = new ArrayList<Point>();
        Utils.checkTimePerform(() -> {
            Point butter;
            while ((butter = findNearestButter(cells, algo.findRobot(), notPossible)) != null) {
                Point finalButter = butter;
                Utils.checkTimePerform(e -> algo.robotPath(switch (algorithm) {
                            case IDS -> algo.ids(finalButter);
                            case BBFS -> algo.bbfs(finalButter);
                            case A_STAR -> algo.aStar(finalButter);
                        }), true, algorithm.toString(),
                        path -> {
                            algo.resetCells();
                            if (path == null) {
                                System.err.println("Path not found.");
                                notPossible.add(finalButter);
                                return;
                            }
                            path.forEach(point -> res.put(point, new int[] { stepCounter.get(),
                                    res.getOrDefault(point, new int[] { stepCounter.getAndIncrement(), 0 })[1]
                                            + 1 }));
                            notPossible.clear();
                        }
                );
            }
            timer.stop();
            algo.drainPermits("step");
            System.out.println("END");
        }, true, "TheMainThreadOfAlgorithms");

        int numOfRemainedButters = 0;
        for (int i = 0; i < algo.getRows(); i++)
            for (int j = 0; j < algo.getCols(); j++) {
                var t = res.get(new Point(i, j));
                if (vp.getTile(i, j).getTextFunction().getText().equals("B"))
                    numOfRemainedButters++;
                if (t == null)
                    continue;
                var text = vp.getTile(i, j).getTextFunction().getText() + " " + Arrays.toString(t) ;
                vp.getTile(i, j).setTextFunction(new StringSupplier() {
                    @Override
                    public String getText() {
                        return text;
                    }

                    @Override
                    public Color getColor() {
                        return Color.BLACK;
                    }

                    @Override
                    public CoordinatedScreen cs() {
                        return vp.getCs();
                    }

                    @Override
                    public int defaultFontSize() {
                        return 8;
                    }
                });
                if (text.contains("R ["))
                    vp.getTile(i, j).setColorFunc(() -> Color.RED);
                else
                    vp.getTile(i, j).setColorFunc(() -> Color.CYAN);
            }
        vp.getTile(initRobot.x, initRobot.y).setColorFunc(() -> Color.GREEN);
        System.out.println(numOfRemainedButters + " butter(s) couldn't reach to destination");
    }

    private static Point findNearestButter(String[][] cells, Point robot, List<Point> notPossible) {
        var explored = new int[cells.length][cells[0].length];
        var queue = new ArrayDeque<>(List.of(robot));
        while (!queue.isEmpty()) {
            var start = queue.pop();
            explored[start.x][start.y] = 1;
            if (cells[start.x][start.y].contains("b") && !cells[start.x][start.y].contains("pb") && !notPossible.contains(start))
                return start;

            var neighbors = neighbors(start, cells);
            neighbors.stream().filter(e -> explored[e.x][e.y] == 0).forEach(e -> {
                queue.add(e);
                explored[e.x][e.y] = -1;
            });
        }
        return null;
    }

    private static List<Point> neighbors(Point point, String[][] cells) {
        var res = new ArrayList<Point>();
        var clone = new Point(point);
        clone.translate(0, 1);
        if (validNeighbor(clone, cells))
            res.add(new Point(clone));
        clone.translate(0, -2);
        if (validNeighbor(clone, cells))
            res.add(new Point(clone));
        clone.translate(1, 1);
        if (validNeighbor(clone, cells))
            res.add(new Point(clone));
        clone.translate(-2, 0);
        if (validNeighbor(clone, cells))
            res.add(clone);
        return res;
    }

    private static boolean validNeighbor(Point point, String[][] cells) {
        var rows = cells.length;
        var cols = cells[0].length;
        return point.x >= 0 && point.x < rows && point.y >= 0 && point.y < cols &&
                !cells[point.x][point.y].contains("r") &&
                !cells[point.x][point.y].contains("p") &&
                !cells[point.x][point.y].contains("x");
    }
}
