package ahd.usim.ulib.visualization.animatedmodels;

import ahd.usim.ulib.visualization.canvas.CoordinatedScreen;
import ahd.usim.ulib.visualization.canvas.Render;
import ahd.usim.ulib.visualization.shapes.shape2d.grid2d.GridPlain2D;

import java.awt.*;
import java.util.*;

public class PuzzleGame implements Render {
    private boolean isVisible;
    private final int rows;
    private final int cols;
    private final GridPlain2D grid;
    private final int[][] cells;
    private final Point current;
    private final Point previous;

    public PuzzleGame(CoordinatedScreen cs, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new GridPlain2D(cs, rows, cols);
        isVisible = true;
        cells = new int[rows][cols];
        current = new Point();
        previous = new Point();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                var tile = grid.getTile(i, j);
                int finalJ = j;
                int finalI = i;
                cells[i][j] = i * rows + j + 1;
                tile.setTextFunction(() -> String.valueOf(cells[finalI][finalJ]));
                tile.setColorFunc(() -> cells[finalI][finalJ] % 2 == 0 ? Color.RED.darker().darker() : Color.BLUE.darker());
                tile.setVisible(() -> cells[finalI][finalJ] != rows * cols);
            }
        shuffle();
    }

    private void shuffle() {
        int counter = 0;
        current.setLocation(rows-1, cols-1);
        while (counter++ < rows * cols * 100) {
            var l = new ArrayList<>(possibleMoves());
            var p = l.get((int) (Math.random() * l.size()));
            swap(p);
            previous.setLocation(current);
            current.setLocation(p);
        }
        previous.setLocation(-1, -1);
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    private Set<Point> possibleMoves() {
        var res = new HashSet<Point>();
        var p = new Point(current);
        p.translate(1, 0);
        if (isValidAddress(p))
            res.add(new Point(p));
        p.translate(-2, 0);
        if (isValidAddress(p))
            res.add(new Point(p));
        p.translate(1, 1);
        if (isValidAddress(p))
            res.add(new Point(p));
        p.translate(0, -2);
        if (isValidAddress(p))
            res.add(p);
        return res;
    }

    private boolean isValidAddress(Point p) {
        return p.x >= 0 && p.y >= 0 && p.x < rows && p.y < cols && !p.equals(previous);
    }

    private void swap(Point p) {
        var temp = cells[p.x][p.y];
        cells[p.x][p.y] = cells[current.x][current.y];
        cells[current.x][current.y] = temp;
    }

    private double heuristic() {
        double score = 0;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                var num = cells[i][j] - 1;
                if (num != i * cols + j && num != rows * cols - 1)
                    score += Math.abs(num / cols - i) + Math.abs(num % cols - j);
//                if (num != i * cols + j && num != rows * cols - 1)
//                    score++;
//                if (num != i * cols + j && num != rows * cols - 1)
//                    score += new Point2D((double) num / cols - i, num % cols - j).distanceFromOrigin();
            }
        return score;
    }

    @Override
    public void render(Graphics2D g2d) {
        grid.renderIfInView(g2d);
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    private long tCount = 0;
    @Override
    public void tick() {
        var factor = heuristic();
        if (factor == 0) {
            System.err.println("AHD: Game End " + tCount);
            return;
        }
        var target = new Point();
        if (Math.random() < 0.925) {
            factor = Double.MAX_VALUE;
            for (var p : possibleMoves()) {
                swap(p);
                var f = heuristic();
                if (f < factor) {
                    factor = f;
                    target.setLocation(p);
                }
                swap(p);
            }
        } else {
            var l = new ArrayList<>(possibleMoves());
            target.setLocation(l.get((int) (l.size() * Math.random())));
        }
        swap(target);
        previous.setLocation(current);
        current.setLocation(target);
        tCount++;
    }
}
