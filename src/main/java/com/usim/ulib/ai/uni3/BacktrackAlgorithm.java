package com.usim.ulib.ai.uni3;

import com.usim.ulib.utils.api.SemaphoreBase;
import com.usim.ulib.utils.annotation.Algorithm;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Semaphore;

@Algorithm(type = Algorithm.SEARCH)
public class BacktrackAlgorithm implements SemaphoreBase<String> {
    private final Map<String, Semaphore> semaphoreMap;
    private final int[][] cells;
    private final int rows;
    private final int cols;
    private final Map<Point, List<Integer>> domainMap;
    private final CheckingMethod checkingMethod;

    public BacktrackAlgorithm(int[][] cells, CheckingMethod checkingMethod)  {
        this.cells = cells;
        this.checkingMethod = checkingMethod;
        domainMap = new HashMap<>();
        rows = cells.length;
        cols = cells[0].length;
        semaphoreMap = new HashMap<>();
        addSemaphore("solve");
    }

    public boolean solve() {
        Point cell;
        if ((cell = mrv()) != null) {
            acquire("solve");
            var domain = domain(cell.x, cell.y);
            for (var e : domain) {
                if (!check(checkingMethod, e, cell))
                    continue;
                cells[cell.x][cell.y] = e;
                if (solve())
                    return true;
                domainMap.get(cell).remove(e);
            }
            cells[cell.x][cell.y] = -1;
            domainMap.put(cell, new ArrayList<>(List.of(0, 1)));
            return false;
        }
        return true;
    }

    private int countConstraints(int value, int i, int j, CheckingMethod checkingMethod) {
        var deepClone = new HashMap<Point, List<Integer>>();
        for (var kv : domainMap.entrySet())
            deepClone.put(kv.getKey(), new ArrayList<>(kv.getValue()));
        var copy = cells[i][j];
        try {
            cells[i][j] = value;
            int res = 0;
            for (var kv : deepClone.entrySet()) {
                var p = kv.getKey();
                var domain = kv.getValue();
                if (cells[p.x][p.y] != -1)
                    continue;
                int failureCount = 0;
                for (int k = 0; k < domain.size(); k++) {
                    var d = domain.get(k);
                    if (!isPossible(d, p.x, p.y)) {
                        domain.remove(d);
                        failureCount++;
                        k--;
                    }
                }
                if (domain.isEmpty())
                    return -1;
                if (checkingMethod == CheckingMethod.MAC && domain.size() == 1)
                    return countConstraints(domain.get(0), p.x, p.y, CheckingMethod.MAC);
                res += failureCount;
            }
            return res;
        } finally {
            cells[i][j] = copy;
        }
    }

    private boolean check(CheckingMethod checkingMethod, int value, Point point) {
        return countConstraints(value, point.x, point.y, checkingMethod) != -1;
    }

    public Map<Point, List<Integer>> getDomainMap() {
        return domainMap;
    }

    private Point mrv() {
        Point res = null;
        int maxSize = 3;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (cells[i][j] == -1) {
                    var tmp = domain(i, j).size();
                    if (maxSize > tmp) {
                        maxSize = tmp;
                        res = new Point(i, j);
                    }
                }
        return res;
    }

    private List<Integer> domain(int i, int j) {
        var p = new Point(i, j);
        if (!domainMap.containsKey(p))
            domainMap.put(p, new ArrayList<>(List.of(0, 1)));
        var res = new ArrayList<>(domainMap.get(p));
        res.removeIf(e -> !isPossible(e, i, j));
        return Collections.unmodifiableList(res);
    }

    private boolean isPossible(int value, int row, int col) {
        final var old = cells[row][col];
        try {
            if (isValid(row - 1, col) && value == cells[row - 1][col] && isValid(row + 1, col) && value == cells[row + 1][col]) return false;
            if (isValid(row, col - 1) && value == cells[row][col - 1] && isValid(row, col + 1) && value == cells[row][col + 1]) return false;
            if (isValid(row - 2, col) && value == cells[row - 2][col] && isValid(row - 1, col) && value == cells[row - 1][col]) return false;
            if (isValid(row, col - 1) && value == cells[row][col - 1] && isValid(row, col - 2) && value == cells[row][col - 2]) return false;
            if (isValid(row + 2, col) && value == cells[row + 2][col] && isValid(row + 1, col) && value == cells[row + 1][col]) return false;
            if (isValid(row, col + 2) && value == cells[row][col + 2] && isValid(row, col + 1) && value == cells[row][col + 1]) return false;
            cells[row][col] = value;
            if (isFilled(cells[row]))
                for (int i = 0; i < rows; i++)
                    if (i != row && isFilled(cells[i]) && Arrays.equals(cells[i], cells[row]))
                        return false;
            var column = col(col);
            if (isFilled(column))
                for (int i = 0; i < cols; i++) {
                    if (i == col)
                        continue;
                    var aCol = col(i);
                    if (isFilled(aCol) && Arrays.equals(aCol, column))
                        return false;
                }
            for (var r : cells)
                if (Arrays.stream(r).filter(e -> e == 1).count() > cols / 2
                        || Arrays.stream(r).filter(e -> e == 0).count() > cols / 2)
                    return false;
            for (int i = 0; i < cols; i++) {
                column = col(i);
                if (Arrays.stream(column).filter(e -> e == 1).count() > rows / 2
                        || Arrays.stream(column).filter(e -> e == 0).count() > rows / 2)
                    return false;
            }
            return true;
        } finally {
            cells[row][col] = old;
        }
    }

    private int[] col(int col) {
        var res = new int[rows];
        for (int i = 0; i < rows; i++)
            res[i] = cells[i][col];
        return res;
    }

    private boolean isValid(int i, int j) {
        return i < rows && i >= 0 && j < cols && j >= 0;
    }

    private static boolean isFilled(int[] row) {
        return Arrays.stream(row).noneMatch(e -> e == -1);
    }

    @Override
    public Map<String, Semaphore> getSemaphoreMap() {
        return semaphoreMap;
    }
}
