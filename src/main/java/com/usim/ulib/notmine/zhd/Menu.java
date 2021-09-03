package com.usim.ulib.notmine.zhd;

import java.util.List;

public class Menu {



    public static void showMenu(List<String> cols, List<List<Object>> rows, List<Integer> width) {
        var sb = new StringBuilder();
        int colNumber = cols.size();
        for (int i = 0; i < colNumber; i++)
            sb.append('+').append("-".repeat(width.get(i)));
        sb.append('+').append('\n');
        int counter = 0;
        for (var col : cols)
            sb.append(String.format("| %-"+ (width.get(counter++ % colNumber) - 1) + "s", col));
        sb.append('|').append('\n');
        sb.append(sb.substring(0, sb.indexOf("\n") + 1));
        for (var row : rows) {
            for (var cell : row)
                sb.append(String.format("| %-"+ (width.get(counter++ % colNumber) - 1) + "s", cell));
            sb.append('|').append('\n');
        }
        sb.append(sb.substring(0, sb.indexOf("\n") + 1));
        System.out.print(sb);
    }
}
