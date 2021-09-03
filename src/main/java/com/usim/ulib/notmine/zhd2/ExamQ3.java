package com.usim.ulib.notmine.zhd2;

import java.util.*;

public class ExamQ3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line;
        HashMap<String, Database> db = new HashMap<>();
        while (!(line = scanner.nextLine().trim().toLowerCase()).equals("done")) {
            String[] queries = line.split("\\s");
            switch (queries[0]) {
                case "create":
                    if (queries[1].equals("user")) {
                        db.put(queries[2], new Database());
                    } else if (queries[1].equals("table")) {
                        db.get(queries[3]).addTable(queries[2]);
                    }
                    break;
                case "delete":
                    db.get(queries[3]).removeTable(queries[2]);
                    break;
                case "add":
                    db.get(queries[5]).addCol(queries[2], queries[3], queries[4].equals("string"));
                    break;
                case "remove":
                    db.get(queries[4]).removeCol(queries[2], queries[3]);
                    break;
                case "print":
                    db.get(queries[2]).showTable(queries[1]);
                    break;
                case "search":
                    db.get(queries[3]).search(queries[1], queries[2]);
                    break;
                case "change":
                    db.get(queries[5]).update(queries[1], queries[2], Integer.parseInt(queries[3]) - 1, queries[4]);
                    break;
            }
        }
    }
}

class Database {
    private final Map<String, Table> tables;

    public Database() {
        tables = new HashMap<>();
    }

    public void addTable(String tableName) {
        tables.put(tableName, new Table());
    }

    public void removeTable(String name) {
        if (!tables.containsKey(name)) {
            System.out.println("Table is not found!");
            return;
        }
        tables.remove(name);
    }

    public void showTable(String name) {
        if (!tables.containsKey(name)) {
            System.out.println("Table is not found!");
            return;
        }
        tables.get(name).showTable(name);
    }

    public void search(String name, String v) {
        if (!tables.containsKey(name)) {
            System.out.println("Table is not found!");
            return;
        }
        tables.get(name).search(v);
    }

    public void removeCol(String table, String col) {
        if (!tables.containsKey(table)) {
            System.out.println("Table is not found!");
            return;
        }
        tables.get(table).removeCol(col);
    }

    public void update(String table, String col, int row, String value) {
        if (!tables.containsKey(table)) {
            System.out.println("Table is not found!");
            return;
        }
        tables.get(table).update(col, row, value);
    }

    public void addCol(String table, String col, boolean isString) {
        if (!tables.containsKey(table)) {
            System.out.println("Table is not found!");
            return;
        }
        tables.get(table).addCol(col, isString);
    }
}

class Table {
    private static final int numOfRows = 5;
    private final Map<String, List<String>> data;
    private final Map<Integer, String> order;
    private final Map<String, Integer> order2;

    public Table() {
        data = new HashMap<>();
        order = new TreeMap<>();
        order2 = new HashMap<>();
    }

    public void search(String value) {
        StringBuilder sb = new StringBuilder();
        data.forEach((k, v) -> {
            for (int i = 0; i < numOfRows; i++)
                if (v.get(i).equals(value))
                    sb.append('(').append(k).append(',').append(i + 1).append(')').append(',');
        });
        System.out.println(sb.length() == 0 ? "Nothing is found!" : sb.substring(0, sb.length() - 1));
    }

    public void showTable(String name) {
        if (!data.isEmpty())
            System.out.println(name + ":");
        System.out.println(this);
    }

    public void addCol(String name, boolean isString) {
        data.put(name, new ArrayList<>(isString ? Arrays.asList("null", "null", "null", "null", "null") : Arrays.asList("0", "0", "0", "0", "0")));
        order2.put(name, order.size());
        order.put(order.size(), name);
    }

    public void removeCol(String name) {
        if (!data.containsKey(name)) {
            System.out.println("Column is not found!");
            return;
        }
        order.remove(order2.get(name));
        order2.remove(name);
        data.remove(name);
    }

    public void update(String col, int row, String v) {
        if (!data.containsKey(col)) {
            System.out.println("Column is not found!");
            return;
        }
        data.get(col).set(row, v);
    }

    @Override
    public String toString() {
        if (data.isEmpty())
            return "Table is empty!";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numOfRows; i++) {
            for (String col : order.values())
                sb.append(data.get(col).get(i)).append(' ');
            sb.append('\n');
        }
        return sb.substring(0, sb.length() - 1);
    }
}


