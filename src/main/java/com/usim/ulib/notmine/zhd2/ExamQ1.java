package com.usim.ulib.notmine.zhd2;

import java.util.Arrays;
import java.util.Scanner;

public class ExamQ1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int row = Integer.parseInt(scanner.nextLine().trim());
        int col = Integer.parseInt(scanner.nextLine().trim());
        int cells = Integer.parseInt(scanner.nextLine().trim());
        int generation = Integer.parseInt(scanner.nextLine().trim());
        String tmp;
        boolean[][] _cells = new boolean[row][col];
        while (cells-- > 0) {
            int[] p = Arrays.stream((tmp = scanner.nextLine()).substring(1, tmp.length() - 1).split(",")).mapToInt(Integer::parseInt)
                    .toArray();
            _cells[p[0]][p[1]] = true;
        }
        Game g = new Game(_cells);
        while (generation-- > 0)
            g.run();
        System.out.println(g);
    }
}

class Game {
    private final int row;
    private final int col;
    private final boolean[][] cells;

    public Game(boolean[][] cells) {
        this.cells = cells;
        this.row = cells.length;
        this.col = cells[0].length;
    }

    private int validate(int i, int j) {
//        while (i < 0)
//            i += row;
//        while (j < 0)
//            j += col;
        if (i < 0 || j < 0 || i >= row || j >= col)
            return 0;
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

    public void run() {
        boolean[][] cellsTemp = new boolean[row][col];
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++) {
                boolean alive = cells[i][j];
                int n = numOfNeighbors(i, j);
                cellsTemp[i][j] = alive && (n == 2 || n == 3) ||!alive && n == 3;
            }
        for (int i = 0 ; i < row; i++)
            System.arraycopy(cellsTemp[i], 0, cells[i], 0, col);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++)
                if (cells[i][j])
                    sb.append('(').append(i).append(',').append(j).append(')').append('\n');
        return sb.substring(0, Math.max(0, sb.length() - 1));
    }
}
