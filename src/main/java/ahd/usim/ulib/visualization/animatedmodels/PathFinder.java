package ahd.usim.ulib.visualization.animatedmodels;

import ahd.usim.ulib.utils.Utils;
import ahd.usim.ulib.utils.supplier.StringSupplier;
import ahd.usim.ulib.visualization.canvas.CoordinatedScreen;
import ahd.usim.ulib.visualization.canvas.Render;
import ahd.usim.ulib.visualization.shapes.shape2d.grid2d.GridPlain2D;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PathFinder implements Render {
    private final int rows;
    private final int cols;
    private final GridPlain2D grid;
    private final TileType[][] tiles;
    private final Point head;
    private final Point pre;
    private final Point start;
    private final Point stop;
    private boolean isLocked;

    public PathFinder(CoordinatedScreen cs, int rows, int cols, Point start, Point stop) {
        this.rows = rows;
        this.cols = cols;
        this.start = start;
        this.stop = stop;
        isLocked = false;
        pre = new Point(-1, -1);
        head = new Point(start);
        tiles = new TileType[rows][cols];
        grid = new GridPlain2D(cs, rows, cols);
//        setAllTileTypes(
//                (i, j) -> (i - stop.x)*(i - stop.x)+(j - stop.y)*(j - stop.y) < 20 &&
//                (i - stop.x)*(i - stop.x)+(j - stop.y)*(j - stop.y) > 12 ||
//                (i - 5)*(i - 6)+(j - 4)*(j - 5) < 20 &&
//                (i - 3)*(i - 12)+(j - 2)*(j - 1) > 2
//                ? TileType.WALL : TileType.NONE);
        setAllTileTypes((i, j) -> Math.random() < 0.5 ? TileType.WALL : TileType.NONE);
        tiles[start.x][start.y] = TileType.START;
        tiles[stop.x][stop.y] = TileType.DESTINATION;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                int finalI = i;
                int finalJ = j;
                grid.getTile(i, j).setColorFunc(() -> tiles[finalI][finalJ].color);
                grid.getTile(i, j).setTextFunction(new StringSupplier() {
                    @Override
                    public String getText() {
                        return String.valueOf(Utils.round(/*start.distance(finalI, finalJ) + */stop.distance(finalI, finalJ), 2));
                    }

                    @Override
                    public Color getColor() {
                        return tiles[finalI][finalJ].color;
                    }

                    @Override
                    public Font getFont() {
                        return new Font("serif", Font.BOLD, 0);
                    }
                });
//                grid.getTile(i, j).setTextFunction(() -> "");
            }
    }

    public void setAllTileTypes(TileTypeSetter setter) {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                tiles[i][j] = setter.type(i, j);
    }

    public TileType getTileType(int i, int j) {
        return tiles[i][j];
    }

    public void setTileAt(int i, int j, TileType type) {
        tiles[i][j] = type;
    }

    public List<Point> getNeighbors(Point p) {
        var res = new ArrayList<Point>();
        p = new Point(p);
        p.translate(0, 1);
        if (isReachable(p))
            res.add(new Point(p));
        p.translate(0, -2);
        if (isReachable(p))
            res.add(new Point(p));
        p.translate(1, 1);
        if (isReachable(p))
            res.add(new Point(p));
        p.translate(-2, 0);
        if (isReachable(p))
            res.add(new Point(p));
        p.translate(2, 1);
        if (isReachable(p))
            res.add(new Point(p));
        p.translate(0, -2);
        if (isReachable(p))
            res.add(new Point(p));
        p.translate(-2, 0);
        if (isReachable(p))
            res.add(new Point(p));
        p.translate(0, 2);
        if (isReachable(p))
            res.add(new Point(p));
        return res;
    }

    private boolean isReachable(Point p) {
        if (p.x < 0 || p.y < 0 || p.x >= rows || p.y >= cols)
            return false;
        var t = tiles[p.x][p.y];
        if (t == TileType.WALL)
            return false;
        if (!isLocked && (t == TileType.ON_PATH || p.equals(pre)))
            return false;
        if (t != TileType.ON_PATH && t != TileType.START && t != TileType.DESTINATION)
        tiles[p.x][p.y] = TileType.EXPLORED;
        return isLocked || !p.equals(pre);
    }

    @Override
    public void render(@NotNull Graphics2D g2d) {
        grid.render(g2d);
    }

    @Override
    public void tick() {
        if (head.equals(stop)) {
            System.err.println("AHD:: END!");
            return;
        }
        var neighbors = getNeighbors(head);
        pre.setLocation(head);
        if (!pre.equals(start))
            tiles[pre.x][pre.y] = TileType.ON_PATH;

        if (neighbors.isEmpty()) {
            isLocked = true;
            neighbors = getNeighbors(head);
            isLocked = neighbors.isEmpty();
            if (neighbors.isEmpty()) {
                System.err.println("AHD:: HardLock END!");
                return;
            }
        }

        var newHead = Collections.min(neighbors, Comparator
                .comparingDouble(p -> Double.parseDouble(grid.getTile(p.x, p.y).getTextFunction().getText())));
        if (tiles[newHead.x][newHead.y] == TileType.ON_PATH) {
//            head.setLocation(neighbors.get((int) (Math.random() * neighbors.size())));
            var s = neighbors.stream().filter(n -> tiles[n.x][n.y] == TileType.ON_PATH).collect(Collectors.toList());
//            head.setLocation(Collections.max(s,
//                    Comparator.comparingDouble(p -> p.distance(pre) + Double.parseDouble(grid.getTile(p.x, p.y).getTextFunction().getText()))));
            head.setLocation(s.get((int) (Math.random() * s.size())));
        } else {
            head.setLocation(newHead);
        }
        tiles[head.x][head.y] = TileType.HEAD;
    }

    @FunctionalInterface
    public interface TileTypeSetter {
        TileType type(int i, int j);
    }

    public enum TileType {
        WALL(Color.RED.darker().darker()),
        EXPLORED(Color.MAGENTA),
        ON_PATH(Color.GREEN),
        NONE(Color.DARK_GRAY.darker()),
        DESTINATION(Color.WHITE),
        START(Color.MAGENTA.darker().darker()),
        HEAD(Color.BLUE.darker());

        TileType(Color color) {
            this.color = color;
        }

        public final Color color;

        public static TileType random(double wallProbability)  {
            return Math.random() < wallProbability ? WALL : NONE;
        }
    }
}
