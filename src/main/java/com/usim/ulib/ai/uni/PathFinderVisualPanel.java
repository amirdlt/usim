package com.usim.ulib.ai.uni;

import com.usim.ulib.utils.Utils;
import com.usim.ulib.utils.supplier.StringSupplier;
import com.usim.ulib.visualization.canvas.CoordinatedScreen;
import com.usim.ulib.visualization.shapes.shape2d.grid2d.GridPlain2D;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class PathFinderVisualPanel extends GridPlain2D {

    private final Grid2DPathFinderAlgorithm algorithm;

    public PathFinderVisualPanel(CoordinatedScreen cs, String[][] cells) {
        super(cs, cells.length, cells[0].length);
        var rows = cells.length;
        var cols = cells[0].length;
        algorithm = new Grid2DPathFinderAlgorithm(cells);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                var tile = getTile(i, j);
                int finalI = i;
                int finalJ = j;
                tile.setColorFunc(() -> switch (algorithm.getInfo()[finalI][finalJ]) {
                    case 0 -> colorOf(algorithm.getCells()[finalI][finalJ]);
                    case 1, 2 -> Color.BLUE;
                    case -1 -> Color.GREEN;
                    case 10 -> Color.YELLOW;
                    case 15, -15 -> Color.CYAN;
                    case 16 -> Color.RED;
                    default -> Color.WHITE;
                });
                tile.setTextFunction(new StringSupplier() {
                    @Override
                    public String getText() {
                        return textOf(algorithm.getCells()[finalI][finalJ]);
                    }

                    @Override
                    public Color getColor() {
                        return Color.YELLOW.darker();
                    }

                    @Override
                    public CoordinatedScreen cs() {
                        return cs;
                    }
                });
                tile.setVisible(() -> visibilityOf(algorithm.getCells()[finalI][finalJ]));
            }
    }

    public PathFinderVisualPanel(CoordinatedScreen cs, String path) throws FileNotFoundException {
        this(cs, loadFromFile(path));
    }

    private Color colorOf(String cell) {
        final var brown = new Color(50, 15, 5);
        final var pink = new Color(255, 0, 80).darker();
        final var darkBrown = new Color(130, 25, 0);
        return switch (cell.toLowerCase().charAt(0)) {
            case '1' -> darkBrown;
            case '2' -> pink;
            case 'x' -> brown;
            default -> Color.WHITE;
        };
    }

    private String textOf(String cell) {
        return switch (cell.toLowerCase()) {
            case "1b", "2b" -> "B";
            case "1r", "2r" -> "R";
            case "x" -> "X";
            case "1p", "2p" -> "P";
            case "1pb", "2pb" -> "PB";
            default -> "";
        };
    }

    private boolean visibilityOf(String cell) {
        return true;
    }

    public Grid2DPathFinderAlgorithm getAlgorithm() {
        return algorithm;
    }

    public static String[][] loadFromFile(String path) throws FileNotFoundException {
        var scanner = new Scanner(new File(path));
        var dim = Arrays.stream(scanner.nextLine().trim().split("\t")).mapToInt(Integer::parseInt).toArray();
        var res = new String[dim[0]][dim[1]];
        int counter = 0;
        while (scanner.hasNextLine())
            res[counter++] = scanner.nextLine().trim().split("\t");
        return res;
    }
}
