package com.usim.ulib.ai.uni3;

import com.usim.ulib.utils.supplier.StringSupplier;
import com.usim.ulib.visualization.canvas.CoordinatedScreen;
import com.usim.ulib.visualization.shapes.shape2d.grid2d.GridPlain2D;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class VisualTable extends GridPlain2D {

    private final int[][] cells;
    private final BacktrackAlgorithm algorithm;

    public VisualTable(CoordinatedScreen cs, int[][] cells) {
        super(cs, cells.length, cells[0].length);
        this.cells = cells;
        algorithm = new BacktrackAlgorithm(cells, CheckingMethod.FORWARD_CHECKING);
        gridColor = Color.WHITE;
        for (int i = 0; i < numOfRows; i++)
            for (int j = 0; j < numOfCols; j++) {
                final var fi = i;
                final var fj = j;
                var t = getTile(i, j);
                t.setTextFunction(new StringSupplier() {
                    @Override
                    public Color getColor() {
                        return Color.BLACK;
                    }

                    @Override
                    public CoordinatedScreen cs() {
                        return cs;
                    }

                    @Override
                    public String getText() {
                        return cells[fi][fj] == -1 ? "_" : String.valueOf(cells[fi][fj]);
                    }
                });
                t.setColorFunc(() -> switch (cells[fi][fj]) {
                    case -1 -> Color.GRAY.darker();
                    case 0 -> Color.GRAY;
                    case 1 -> Color.GRAY.brighter();
                    default -> Color.RED;
                });
            }
    }

    public BacktrackAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int[][] getCells() {
        return cells;
    }

    public static int[][] readTableFromFile(String filePath) {
        try (var scanner = new Scanner(new File(filePath))) {
            var mn = Arrays.stream(scanner.nextLine().trim().split(" ")).mapToInt(Integer::parseInt).toArray();
            var res = new int[mn[0]][];
            var counter = 0;
            while (scanner.hasNextLine())
                res[counter++] = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(s -> s.equals("-") ? -1 : Integer.parseInt(s.trim())).toArray();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return new int[0][0];
        }
    }
}
