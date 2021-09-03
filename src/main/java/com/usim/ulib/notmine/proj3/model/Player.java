package com.usim.ulib.notmine.proj3.model;

public class Player {
    private String name;
    private double money;
    private final Backpack backpack;

    public Player(String name) {
        this.name = name;
        money = 45;
        backpack = new Backpack();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public boolean decreaseMoney(double value) {
        var changed = money > value;
        money -= changed ? value : 0;
        return changed;
    }

    public boolean addWeapon(Weapon weapon) {
        if (weapon == null)
            return false;
        if (backpack.addWeapon(weapon) && money >= weapon.getCost()) {
            money -= weapon.getCost();
            return true;
        }
        return false;
    }

    public Backpack getBackpack() {
        return backpack;
    }

    @Override
    public String toString() {
        return "Player{" + "name='" + name + '\'' + ", money=" + money + ", backpack=" + backpack + '}';
    }
}
