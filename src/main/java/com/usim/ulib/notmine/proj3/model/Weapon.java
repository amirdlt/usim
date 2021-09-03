package com.usim.ulib.notmine.proj3.model;

import java.util.Objects;

public class Weapon {
    private String name;
    private int damage;
    private int range;
    private double weight;
    private double cost;

    public Weapon(String name, int damage, int range, double weight, double cost) {
        this.name = name;
        this.damage = damage;
        this.range = range;
        this.weight = weight;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Weapon weapon = (Weapon) o;
        return Objects.equals(name, weapon.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Weapon{" + "name='" + name + '\'' + ", damage=" + damage + ", range=" + range + ", weight=" + weight
                + ", cost=" + cost + '}';
    }
}
