package ahd.ulib.visualization.animatedmodels;

import ahd.ulib.jmath.datatypes.tuples.Point3D;
import ahd.ulib.visualization.canvas.CoordinatedScreen;
import ahd.ulib.visualization.shapes.shape3d.Area;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashSet;

public class GameOfLife3D extends Area {
    private final int cubeSize;

    public GameOfLife3D(CoordinatedScreen cs, int cubeSize) {
        super(cs);
        this.cubeSize = cubeSize;
        init();
    }

    private void init() {
        var set = new HashSet<Point3D>();
        for (int i = 0; i < cubeSize; i++)
            for (int j = 0; j < cubeSize; j++)
                for (int k = 0; k < cubeSize; k++) {
                    var cell = Area.cube(cs, new Point3D(i * 0.2 + 0.05, j * 0.2 + 0.05, k * 0.2 + 0.05), 0.11);
                    components.add(cell);
                    set.addAll(cell.getPoints());
                }
        points.addAll(set);
        setCells((i, j, k) -> Math.random() < 0.1);
        //        setCells((i, j, k) ->
        //                    i == 8 && j == 8 && k == 4 ||
        //                    i == 8 && j == 9 && k == 4 ||
        //                    i == 7 && j == 7 && k == 4 ||
        //                    i == 6 && j == 7 && k == 4 ||
        //                    i == 5 && j == 8 && k == 4 ||
        //                    i == 5 && j == 9 && k == 4 ||
        //                    i == 6 && j == 10 && k == 4 ||
        //                    i == 7 && j == 10 && k == 4
        //                );
    }

    public void setCells(CellSetter cellSetter) {
        for (int i = 0; i < cubeSize; i++)
            for (int j = 0; j < cubeSize; j++)
                for (int k = 0; k < cubeSize; k++)
                    components.get(i + j * cubeSize + k * cubeSize * cubeSize).setVisible(cellSetter.isAlive(i, j, k));
    }

    public boolean getCell(int i, int j, int k) {
        return components.get(i + j * cubeSize + k * cubeSize * cubeSize).isVisible();
    }

    private int asInt(boolean... b) {
        int res = 0;
        for (var e : b)
            res += e ? 1 : 0;
        return res;
    }

    private int validate(int i) {
        if (i < 0)
            return i + cubeSize;
        return i % cubeSize;
    }

    @Override
    public void render(@NotNull Graphics2D g2d) {
    }

    @Override
    public void tick() {
        var cellsTemp = new boolean[cubeSize][cubeSize][cubeSize];
        for (int i = 0; i < cubeSize; i++)
            for (int j = 0; j < cubeSize; j++)
                for (int k = 0; k < cubeSize; k++) {
                    var n = asInt(
                            getCell(validate(i+1), j, k),
                            getCell(validate(i-1), j, k),

                            getCell(i, validate(j+1), k),
                            getCell(validate(i+1), validate(j+1), k),
                            getCell(validate(i-1), validate(j+1), k),

                            getCell(i, validate(j-1), k),
                            getCell(validate(i+1), validate(j-1), k),
                            getCell(validate(i-1), validate(j-1), k),

                            getCell(i, j, validate(k+1)),
                            getCell(validate(i+1), j, validate(k+1)),
                            getCell(validate(i-1), j, validate(k+1)),

                            getCell(i, validate(j+1), validate(k+1)),
                            getCell(validate(i+1), validate(j+1), validate(k+1)),
                            getCell(validate(i-1), validate(j+1), validate(k+1)),

                            getCell(i, validate(j-1), validate(k+1)),
                            getCell(validate(i+1), validate(j-1), validate(k+1)),
                            getCell(validate(i-1), validate(j-1), validate(k+1)),

                            getCell(i, j, validate(k-1)),
                            getCell(validate(i+1), j, validate(k-1)),
                            getCell(validate(i-1), j, validate(k-1)),

                            getCell(i, validate(j+1), validate(k-1)),
                            getCell(validate(i+1), validate(j+1), validate(k-1)),
                            getCell(validate(i-1), validate(j+1), validate(k-1)),

                            getCell(i, validate(j-1), validate(k-1)),
                            getCell(validate(i+1), validate(j-1), validate(k-1)),
                            getCell(validate(i-1), validate(j-1), validate(k-1))
                    );
                    var alive = getCell(i, j, k);
                    if (alive && (n == 6 || n == 5) || !alive && n == 4)
                        cellsTemp[i][j][k] = true;
                }

        setCells((i, j, k) -> cellsTemp[i][j][k]);
    }

    @FunctionalInterface
    public interface CellSetter {
        boolean isAlive(int row, int col, int page);
    }
}
