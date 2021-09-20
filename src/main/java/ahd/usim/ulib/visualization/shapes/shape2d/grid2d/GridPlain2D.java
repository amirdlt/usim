package ahd.usim.ulib.visualization.shapes.shape2d.grid2d;

import ahd.usim.ulib.utils.Utils;
import ahd.usim.ulib.visualization.canvas.CoordinatedScreen;
import ahd.usim.ulib.visualization.canvas.Render;
import ahd.usim.ulib.jmath.datatypes.tuples.Point2D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;

public class GridPlain2D implements Render {
    protected boolean isVisible;
    protected boolean drawGrid;
    protected float gridThickness;
    protected Color gridColor;
    protected double tilesWidth;
    protected double tilesHeight;
    protected final int numOfRows;
    protected final int numOfCols;
    protected final Tile2D[][] gridTiles;
    protected final Point2D pos;
    protected CoordinatedScreen cs;

    public GridPlain2D(CoordinatedScreen cs, int numOfRows, int numOfCols, Point2D pos) {
        this.numOfRows = numOfRows;
        this.numOfCols = numOfCols;
        tilesWidth = 0.5;
        tilesHeight = 0.5;
        isVisible = true;
        drawGrid = true;
        gridThickness = 1f;
        gridColor = Color.GRAY;
        this.cs = cs;
        this.pos = pos;
        gridTiles = new Tile2D[numOfRows][numOfCols];
        for (int i = numOfRows-1; i >= 0; i--)
            for (int j = 0; j < numOfCols; j++) {
                int finalI = i;
                int finalJ = j+1;
                gridTiles[numOfRows-i-1][j] = new Tile2D(cs, new Point2D(j * tilesWidth + pos.x, i * tilesHeight + pos.y),
                        tilesWidth, tilesHeight, Utils::randomColor, true, () -> String.valueOf(finalI*numOfCols+finalJ));
            }
    }

    public GridPlain2D(CoordinatedScreen cs, int numOfRows, int numOfCols) {
        this(cs, numOfRows, numOfCols, new Point2D());
    }

    public Point2D getPos() {
        return pos.getCopy();
    }

    public void setPos(double x, double y) {
        pos.set(x, y);
        for (int i = numOfRows-1; i >= 0; i--)
            for (int j = 0; j < numOfCols; j++)
                gridTiles[numOfRows-i-1][j].setPos(new Point2D(j * tilesWidth + pos.x, i * tilesHeight + pos.y));
    }

    public void move(double dx, double dy) {
        setPos(pos.x + dx, pos.y + dy);
    }

    public double getTilesWidth() {
        return tilesWidth;
    }

    public void setTilesWidth(double tilesWidth) {
        this.tilesWidth = tilesWidth;
    }

    public double getTilesHeight() {
        return tilesHeight;
    }

    public void setTilesHeight(double tilesHeight) {
        this.tilesHeight = tilesHeight;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public int getNumOfCols() {
        return numOfCols;
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public void setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
    }

    public float getGridThickness() {
        return gridThickness;
    }

    public void setGridThickness(float gridThickness) {
        this.gridThickness = gridThickness;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    public CoordinatedScreen getCs() {
        return cs;
    }

    public void setCs(CoordinatedScreen cs) {
        this.cs = cs;
    }

    public Tile2D getTile(int i, int j) {
        return gridTiles[i][j];
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void actOnAllTiles(ActOnTile actOnTile) {
        for (int i = 0; i < numOfRows; i++)
            for (int j = 0; j < numOfCols; j++)
                actOnTile.act(gridTiles[i][j]);
    }

    private void drawGrid(Graphics2D g2d) {
        if (!drawGrid)
            return;
        g2d.setColor(gridColor);
        g2d.setStroke(new BasicStroke(gridThickness));
        for (int i = -1; i < numOfRows; i++)
            g2d.drawLine(cs.screenX(pos.x),
                    cs.screenY(i * tilesHeight + pos.y), cs.screenX(tilesWidth * numOfCols + pos.x), cs.screenY(i * tilesHeight + pos.y));
        for (int i = 0; i < numOfCols + 1; i++)
            g2d.drawLine(cs.screenX(i * tilesWidth + pos.x),
                    cs.screenY(-tilesHeight + pos.y), cs.screenX(tilesWidth * i + pos.x), cs.screenY((numOfRows-1) * tilesHeight + pos.y));
    }

    @Override
    public void render(@NotNull Graphics2D g2d) {
        for (var row : gridTiles)
            Arrays.stream(row).forEach(tile -> tile.renderIfInView(g2d));
        drawGrid(g2d);
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @FunctionalInterface
    public interface ActOnTile {
        void act(Tile2D tile);
    }
}
