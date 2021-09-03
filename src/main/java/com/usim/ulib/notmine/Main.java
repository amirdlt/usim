package com.usim.ulib.notmine;

import java.util.*;

public class Main {
    private static final Map<String, Integer> counts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public static void main(String[] args) {
        tokenize(new Scanner(System.in).nextLine().trim(), 1);
        counts.forEach((k, v) -> System.out.print(k + (v == 1 ? "" : v)));
    }

    private static void tokenize(String mol, int coefficient) {
        int len = mol.length();
        char[] chars = mol.toCharArray();
        if (!mol.contains("(")) {
            for (int i = 0; i < len; i++) {
                if (Character.isUpperCase(chars[i])) {
                    String atom = Character.toString(chars[i]);
                    if (i + 1 < len && Character.isLowerCase(chars[i + 1]))
                        atom += chars[++i];
                    int count = i + 1 < len && Character.isDigit(chars[i + 1]) ? 0 : 1;
                    while (i + 1 < len && Character.isDigit(chars[i + 1]))
                        count = count * 10 + Character.digit(chars[++i], 10);
                    counts.put(atom, counts.getOrDefault(atom, 0) + count * coefficient);
                }
            }
            return;
        }
        String[][] split = split(mol);
        for (String part : split[0])
            tokenize(part.substring(0, part.indexOf('*')), coefficient * Integer.parseInt(part.substring(part.indexOf('*') + 1)));
        for (String part : split[1])
            tokenize(part, coefficient);
    }

    private static String[][] split(String mol) {
        if (!mol.contains("("))
            return new String[][] {{mol}};
        final int len = mol.length();
        final char[] chars = mol.toCharArray();
        int count = 1;
        int from = mol.indexOf('(');
        List<String> with = new ArrayList<>();
        List<String> without = new ArrayList<>();
        without.add(mol.substring(0, from));
        for (int i = from + 1; i < len; i++) {
            count += chars[i] == '(' ? 1 : chars[i] == ')' ? -1 : 0;
            if (count == 0) {
                int coefficient = i + 1 < len && Character.isDigit(chars[i + 1]) ? 0 : 1;
                String part = mol.substring(from + 1, i);
                while (i + 1 < len && Character.isDigit(chars[i + 1]))
                    coefficient = coefficient * 10 + Character.digit(chars[++i], 10);
                with.add(part + "*" + coefficient);
                from = mol.substring(i).indexOf('(') + i;
                if (from < i) {
                    without.add(mol.substring(i + 1));
                    break;
                } else {
                    without.add(mol.substring(i + 1, from));
                    i = from + 1;
                    count = 1;
                }
            }
        }
        with.removeIf(String::isEmpty);
        without.removeIf(String::isEmpty);
        return new String[][] { with.toArray(new String[0]), without.toArray(new String[0]) };
    }
}
