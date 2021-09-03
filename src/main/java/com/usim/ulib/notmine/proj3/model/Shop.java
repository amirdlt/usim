package com.usim.ulib.notmine.proj3.model;

public class Shop {
    public final static int maxNumOfWeaponTypes = 80;

    private final HashTable<Weapon, Integer> weapons;

    public Shop() {
        weapons = new HashTable<>(80);
    }

    public HashTable<Weapon, Integer> getWeapons() {
        return weapons;
    }

    public boolean removeWeapon(String name) {
        var w = getWeaponByName(name);
        if (name == null)
            return false;
        weapons.remove(w);
        return true;
    }

    public boolean addWeapon(Weapon weapon, int num) {
        if (weapons.size() == maxNumOfWeaponTypes && !containsWeapon(weapon.getName()))
            return false;
        weapons.put(weapon, weapons.getOrDefault(weapon, 0) + num);
        return true;
    }

    public boolean addWeapon(String weaponName, int num) {
        var weapon = getWeaponByName(weaponName);
        if (weapon == null)
            return false;
        addWeapon(weapon, num);
        return true;
    }

    public Weapon getWeaponByName(String name) {
        for (var kv : weapons.entrySet()) {
            if (kv.getKey().getName().equalsIgnoreCase(name))
                return kv.getKey();
        }
        return null;
    }

    public Weapon getWeaponByNameAndDecrease(String name) {
        for (var kv : weapons.entrySet()) {
            if (kv.getKey().getName().equalsIgnoreCase(name) && kv.getValue() > 0)
                weapons.put(kv.getKey(), kv.getValue() - 1);
                return kv.getKey();
        }
        return null;
    }

    public boolean containsWeapon(String name) {
        return getWeaponByName(name) != null && weapons.get(getWeaponByName(name)) > 0;
    }

    @Override
    public String toString() {
        return "Shop{" + "weapons=" + weapons + '}';
    }
}
