package com.usim.ulib.notmine.proj3.model;

public class Backpack {
    private static final double maxWeight = 90;
    private static final int maxItemCount = 30;

    private final HashTable<Weapon, Integer> weapons;
    private double currentWeight;

    public Backpack() {
        currentWeight = 0;
        weapons = new HashTable<>(30);
    }

    public int getNumOfItems() {
        int counter = 0;
        for (var v : weapons.values())
            counter += v;
        return counter;
    }

    public boolean addWeapon(Weapon weapon) {
        if (getNumOfItems() >= maxItemCount && currentWeight + weapon.getWeight() >= maxWeight)
            return false;
        weapons.put(weapon, weapons.getOrDefault(weapon, 0) + 1);
        currentWeight += weapon.getWeight();
        return true;
    }

    public HashTable<Weapon, Integer> getWeapons() {
        return weapons;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    @Override
    public String toString() {
        return "Backpack{" + "weapons=" + weapons + ", currentWeight=" + currentWeight + '}';
    }
}
