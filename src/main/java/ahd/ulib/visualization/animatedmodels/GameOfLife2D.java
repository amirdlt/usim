package ahd.ulib.visualization.animatedmodels;

import ahd.ulib.visualization.canvas.CoordinatedScreen;
import ahd.ulib.visualization.canvas.Render;

import java.awt.*;

public class GameOfLife2D implements Render {

    private final int row;
    private final int col;
    private boolean isVisible;
    private final boolean[][] cells;
    private final CoordinatedScreen cs;
    private final double cellSize;

    public GameOfLife2D(CoordinatedScreen cs, int row, int col) {
        if (row <= 0 || col <= 0)
            throw new RuntimeException("AHD:: Invalid row or col");
        this.cs = cs;
        this.row = row;
        this.col = col;
        isVisible = true;
        cellSize = 0.1;
        cells = new boolean[row][col];
        setCells((i, j) -> Math.random() < 0.1);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    private int validate(int i, int j) {
        while (i < 0)
            i += row;
        while (j < 0)
            j += col;
        return cells[i % row][j % col] ? 1 : 0;
    }

    private int numOfNeighbors(int i, int j) {
        return  validate(i+1, j) +
                validate(i-1, j) +
                validate(i, j+1) +
                validate(i+1, j+1) +
                validate(i-1, j+1) +
                validate(i, j-1) +
                validate(i+1, j-1) +
                validate(i-1, j-1);
    }

    public void setCells(CellSetter setter) {
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++)
                cells[i][j] = setter.isAlive(i, j);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(0.05f));
        for (int i = -1; i < row; i++)
            g2d.drawLine(cs.screenX(0),
                    cs.screenY(i * cellSize), cs.screenX(cellSize * col), cs.screenY(i * cellSize));
        for (int i = 0; i < col + 1; i++)
            g2d.drawLine(cs.screenX(i * cellSize),
                    cs.screenY(-cellSize), cs.screenX(cellSize * i), cs.screenY((row-1) * cellSize));
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        var w = cs.screenXLen(cellSize);
        var h = cs.screenYLen(cellSize);
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++)
                if (cells[i][j])
                    g2d.fillRect(cs.screenX(i * cellSize), cs.screenY(j * cellSize), w, h);
        drawGrid(g2d);
    }

    @Override
    public void tick() {
        var cellsTemp = new boolean[row][col];
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++) {
                var alive = cells[i][j];
                var n = numOfNeighbors(i, j);
                cellsTemp[i][j] = alive && (n == 2 || n == 3) ||!alive && n == 3;
            }
        for (int i = 0 ; i < row; i++)
            System.arraycopy(cellsTemp[i], 0, cells[i], 0, col);
    }

    @FunctionalInterface
    public interface CellSetter {
        boolean isAlive(int row, int col);
    }
}
