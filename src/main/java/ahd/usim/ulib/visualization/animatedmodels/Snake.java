package ahd.usim.ulib.visualization.animatedmodels;

import ahd.usim.ulib.visualization.canvas.CoordinatedScreen;
import ahd.usim.ulib.visualization.canvas.Render;
import ahd.usim.ulib.visualization.shapes.shape2d.grid2d.GridPlain2D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;

public class Snake implements Render {
    private final GridPlain2D grid;
    private final TileType[] tiles;
    private final int rows;
    private final int cols;
    private int headPos;
    private int foodPos;

    public Snake(CoordinatedScreen cs, int rows, int cols) {
        this.cols = cols;
        this.rows = rows;
        grid = new GridPlain2D(cs, rows, cols);
        tiles = new TileType[rows * cols];
        init();
    }

    private void init() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                int finalJ = j;
                int finalI = i;
                tiles[i * rows + j] = TileType.EMPTY;
                grid.getTile(i, j).setColorFunc(() -> tiles[finalI * rows + finalJ] == null ? Color.BLACK : tiles[finalI * rows + finalJ].color);
                grid.getTile(i, j).setTextFunction(() -> null);
            }
        tiles[headPos = (int) (Math.random() * tiles.length)] = TileType.SNAKE_HEAD;
        tiles[foodPos = (int) (Math.random() * tiles.length)] = TileType.FOOD;
    }

    private int snakeLen() {
        var res = 1;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (tiles[i*rows+j] == TileType.SNAKE_BODY)
                    res++;
        return res;
    }

    private int[] possibleMoves() {
        var list = new ArrayList<Integer>();
        if (isReachable(headPos + 1))
            list.add(headPos + 1);
        if (isReachable(headPos - 1))
            list.add(headPos - 1);
        if (isReachable(headPos + rows))
            list.add(headPos + rows);
        if (isReachable(headPos - rows))
            list.add(headPos - rows);
        System.out.println(list);
        return list.stream().mapToInt(e -> e).toArray();
    }

    private boolean isReachable(int i) {
        return i >= 0 && i < tiles.length && (tiles[i] == TileType.EMPTY || tiles[i] == TileType.FOOD);
    }

    @Override
    public void render(@NotNull Graphics2D g2d) {
        grid.render(g2d);
    }

    @Override
    public void tick() {
        var possibleMoves = possibleMoves();
        if (possibleMoves.length == 0) {
            System.err.println("AHD:: Game End.");
            return;
        }

        var old = headPos;
        headPos = possibleMoves[(int) (Math.random() * possibleMoves.length)];
        swap(headPos, old);
    }

    private void swap(int i, int j) {
        var temp = tiles[i];
        tiles[i] = tiles[j];
        tiles[j] = temp;
    }

    public enum TileType {
        SNAKE_BODY(Color.GREEN.darker()),
        SNAKE_HEAD(Color.BLUE.darker()),
        FOOD(Color.MAGENTA.darker()),
        WALL(Color.RED.darker()),
        EMPTY(Color.BLACK);

        TileType(Color color) {
            this.color = color;
        }

        public final Color color;
    }
}
