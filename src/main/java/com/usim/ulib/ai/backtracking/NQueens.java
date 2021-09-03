package com.usim.ulib.ai.backtracking;

import com.usim.ulib.swingutils.MainFrame;
import com.usim.ulib.utils.supplier.StringSupplier;
import com.usim.ulib.visualization.canvas.CoordinatedScreen;
import com.usim.ulib.visualization.canvas.Graph3DCanvas;
import com.usim.ulib.visualization.shapes.shape2d.grid2d.GridPlain2D;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static com.usim.ulib.utils.Utils.unsafeExecutor;

public class NQueens extends GridPlain2D {

    private final int tableSize;
    private final List<Integer> initialDomain;
    private final int[] queens;

    private final Map<Integer, List<Integer>> domainMap;

    public NQueens(CoordinatedScreen cs, int tableSize) {
        super(cs, tableSize, tableSize);
        if (tableSize < 0)
            throw new IllegalArgumentException();
        this.tableSize = tableSize;

        domainMap = new HashMap<>();
        initialDomain = List.copyOf(new ArrayList<>(tableSize) {{
            for (int i = 0; i < tableSize; i++)
                add(i);
        }});

        queens = new int[tableSize];
        Arrays.fill(queens, -1);


        setGridColor(Color.white);

        for (int i = 0; i < tableSize; i++)
            for (int j = 0; j < tableSize; j++) {
                var tile = getTile(i, j);
                final var finalI = i;
                final var finalJ = j;
                tile.setColorFunc(() -> finalI == queens[finalJ] ? Color.GRAY : Color.DARK_GRAY);
                tile.setTextFunction(new StringSupplier() {
                    @Override
                    public String getText() {
                        return finalI == queens[finalJ] ? "Q" : "";
                    }

                    @Override
                    public CoordinatedScreen cs() {
                        return cs;
                    }
                });
            }
    }

    private List<Integer> domain(int col) {
        var res = new ArrayList<Integer>();
        if (!domainMap.containsKey(col))
            domainMap.put(col, new ArrayList<>(initialDomain));
        for (var i : domainMap.get(col)) {
            var possible = true;
            for (int j = 0; j < tableSize; j++) {
                if (j == col || queens[j] == -1)
                    continue;
                if (queens[j] == i || Math.abs(col - j) == Math.abs(queens[j] - i)) {
                    possible = false;
                    break;
                }
            }
            if (possible)
                res.add(i);
        }
        return res;
    }

    private int empty() {
        for (int i = 0; i < tableSize; i++)
            if (queens[i] == -1)
                return i;
        return -1;
    }

    private int count = 0;

    private boolean solve() {
        int i;
        while ((i = empty()) > -1) {
            count++;
//            waitToSee(1);
            var domain = domain(i);
            if (domain.isEmpty()) {
                domainMap.replace(i, new ArrayList<>(initialDomain));
                return false;
            }
            for (var row : domain) {
                queens[i] = row;
                if (solve())
                    return true;
                domainMap.get(i).remove(row);
            }
            queens[i] = -1;
        }
        return true;
    }

    private void waitToSee(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (cs instanceof JComponent component)
            component.repaint();
    }

    public static void main(String[] args) {
        var f = new MainFrame();

        var gp = new Graph3DCanvas();
        f.add(gp);

        var panel = new NQueens(gp, 24);

        unsafeExecutor.execute(() -> {
            System.err.println(panel.solve());
            System.err.println(panel.count);
        });

        gp.addRender(panel);

        SwingUtilities.invokeLater(f);
    }
}
