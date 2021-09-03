package com.usim.ulib.notmine.tmp12;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class FileUtils {
    public static double calculateAverage(String path, String id) {
        var f = new File(path);
        if (!f.exists() || !f.isDirectory())
            throw new IllegalArgumentException("Bad Path specified");
        return extractGrades(path, id).stream().mapToDouble(e -> e).average().orElse(-1);
    }

    private static List<Double> extractGrades(String path, String id) {
        var res = new ArrayList<Double>();
        var dir = new File(path);
        for (var f : Objects.requireNonNull(dir.listFiles()))
            if (f.isFile() && f.getName().trim().toLowerCase().endsWith(".csv")) {
                try {
                    var scanner = new Scanner(f);
                    while (scanner.hasNextLine()) {
                        var line = scanner.nextLine().replace(" ", "").split(",");
                        try {
                            if (line[0].equals(id)) {
                                res.add(Double.parseDouble(line[1]));
                                break;
                            }
                        } catch (IndexOutOfBoundsException | NumberFormatException ignore) {}
                    }
                } catch (FileNotFoundException ignore) {
                    // This exception never can happen
                }
            } else if (f.isDirectory()) {
                res.addAll(extractGrades(f.getPath(), id));
            }
        return res;
    }
}
