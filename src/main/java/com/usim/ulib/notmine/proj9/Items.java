package com.usim.ulib.notmine.proj9;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Items {
    private static final Item[] items;

    static {
        Item[] items_;
        try {
            Scanner scanner = new Scanner(new File("itemFile.txt"));
            items_ = new Item[Integer.parseInt(scanner.nextLine().trim())];
            int count = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("*"))
                    continue;
                String name = scanner.nextLine().trim().split(" ")[1];
                if (line.split(" ")[1].equalsIgnoreCase("book")) {
                    String wName = scanner.nextLine().trim().split(" ")[1];
                    int numOfPages = Integer.parseInt(scanner.nextLine().trim().split(" ")[1]);
                    items_[count++] = new Book(name, wName, numOfPages);
                } else {
                    int id = Integer.parseInt(scanner.nextLine().trim().split(" ")[1]);
                    items_[count++] = new DVD(id, name);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            items_ = new Item[0];
        }

        items = items_;

    }

    public static Item getItemByName(String name) {
        for (Item item : items)
            if (item.getName().equalsIgnoreCase(name))
                return item;
        return null;
    }
}
