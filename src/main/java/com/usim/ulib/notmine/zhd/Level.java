package com.usim.ulib.notmine.zhd;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Level implements Serializable {
    private int level;
    private int time;
    public static List<Level> levels = new ArrayList<>();
    private final List<String> wiledAnimal = new ArrayList<>();
    private int coin = 0;
    private int expectedTime;
    private int reward;
    private final HashMap<String, Integer> tasks = new HashMap<>();
    private final List<Animal> animals = new ArrayList<>();
    private final List<Factory> factories = new ArrayList<>();

    static {
        try {
            Scanner scanner=new Scanner(new File("missions.txt"));
            int numberLevel=Integer.parseInt(scanner.nextLine());
            Level level1=new Level();
            for (int i = 0; i <numberLevel ; i++) {
                level1.level = i + 1;
                level1.time = 0;
                int[] mn = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
                level1.expectedTime = mn[0];
                level1.coin = mn[1];
                level1.reward = mn[2];
                int count = Integer.parseInt(scanner.nextLine());
                while (count-- > 0)
                    level1.wiledAnimal.add(scanner.nextLine());
                var arr = Arrays.stream(scanner.nextLine().split("\\s")).collect(Collectors.toList());
                for (int j = 0; j < arr.size() / 2; j++)
                    level1.tasks.put(arr.get(2 * j), Integer.parseInt(arr.get(2 * j + 1)));
            }
            levels.add(level1);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getLevel() {
        return level;
    }

    public List<String> getWiledAnimal() {
        return wiledAnimal;
    }

    public int getTime() {
        return time;
    }

    public void time(int change) {
        time += change;
    }

    public List<Factory> getFactories() {
        return factories;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public int getCoin() {
        return coin;
    }

    public int getReward() {
        return reward;
    }

    public int getExpectedTime() {
        return expectedTime;
    }

    public boolean checkLevel() {
        if (level == 0 && Store.firstProduct.getOrDefault("egg", 0) >= tasks.get("egg")
                && Store.firstProduct.getOrDefault("fur", 0) >= tasks.get("fur")) {
            level++;
            return true;
        } else if (level == 1 && Store.firstProduct.getOrDefault("egg", 0) >= tasks.get("egg")
                && Store.firstProduct.getOrDefault("milk", 0) >= tasks.get("milk")) {
            level++;
            return true;
        } else if (level == 2 && Store.firstProduct.getOrDefault("egg", 0) >= tasks.get("egg")
                && Store.secondProduct.getOrDefault("flour", 0) >= tasks.get("flour")
                && Store.thirdProduct.getOrDefault("bread", 0) >= tasks.get("bread")) {
            level++;
            return true;
        } else if (level == 3 && Store.firstProduct.getOrDefault("fur", 0) >= tasks.get("fur")
                && Store.secondProduct.getOrDefault("tissue", 0) >= tasks.get("tissue")
                && Store.thirdProduct.getOrDefault("dress", 0) >= tasks.get("dress")) {
            level++;
            return true;
        } else if (level == 4 && Store.firstProduct.getOrDefault("milk", 0) >= tasks.get("milk")
                && Store.secondProduct.getOrDefault("packedMilk", 0) >= tasks.get("packedMilk")
                && Store.thirdProduct.getOrDefault("iceCream", 0) >= tasks.get("iceCream")) {
            level++;
            return true;
        }

        return false;
    }
}
