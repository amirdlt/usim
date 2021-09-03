package com.usim.ulib.notmine.proj3;

import com.usim.ulib.notmine.proj3.model.HashTable;
import com.usim.ulib.notmine.proj3.model.Player;
import com.usim.ulib.notmine.proj3.model.Shop;
import com.usim.ulib.notmine.proj3.model.Weapon;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private final static Scanner in = new Scanner(System.in);
    private static Player player;
    private final static Shop shop = new Shop();

    public static void main(String[] args) {
        driver();
    }

    private static void driver() {
        handlePlayer();
        loop: while (true)
            switch (showMenu()) {
                case 1:
                    addItemsToShop();
                    break;
                case 2:
                    deleteItemsFromShop();
                    break;
                case 3:
                    buyFromShop();
                    break;
                case 4:
                    System.out.println("The backpack contains: ");
                    showItems(player.getBackpack().getWeapons());
                    break;
                case 5:
                    System.out.println("The player Info.:");
                    System.out.println("name: " + player.getName());
                    System.out.println("coins: " + (int) player.getMoney());
                    System.out.println("The backpack contains: ");
                    showItems(player.getBackpack().getWeapons());
                    break;
                case 6:
                    break loop;
                default:
                    System.out.println("You've entered wrong option");
            }
        System.out.println("Goodbye!");
    }

    private static int showMenu() {
        System.out.println("Please choose one of these operations(1 upto 6): ");
        System.out.println(
                "1) Add Items to the shop\n" + "2) Delete Items from the shop\n" + "3) Buy from the Shop\n"
                + "4) View backpack\n" + "5) View Player\n" + "6) Exit\n");
        return Integer.parseInt(in.nextLine());
    }

    private static void handlePlayer() {
        System.out.println("****** Welcome to Weapon shop game ******");
        System.out.println("Please enter your name: ");
        player = new Player(in.nextLine());
    }

    private static void showItems(HashTable<Weapon, Integer> map) {
        AtomicInteger counter = new AtomicInteger(1);
        map.forEach((weapon, quantities) -> {
            if (quantities < 1)
                return;
            System.out.println(counter.getAndIncrement() + ") " + weapon.getName() + " : " + quantities + " Item(s)");
        });
    }

    private static void addItemsToShop() {
        System.out.println("Please enter name of weapon: ");
        var name = in.nextLine().trim();
        if (shop.containsWeapon(name)) {
            System.out.println("Please enter number of items to add: ");
            System.out.println(shop.addWeapon(name, Integer.parseInt(in.nextLine())) ? "Added successfully." : "Couldn't add.");
        } else {
            System.out.println("This weapon is new...");
            if (shop.getWeapons().size() == Shop.maxNumOfWeaponTypes) {
                System.out.println("The shop can not have more kind of weapons.");
            } else {
                System.out.print("Please enter range of the weapon: ");
                var range = Integer.parseInt(in.nextLine());
                System.out.print("Please enter damage of the weapon: ");
                var damage = Integer.parseInt(in.nextLine());
                System.out.print("Please enter cost of the weapon: ");
                var cost = Double.parseDouble(in.nextLine());
                System.out.print("Please enter weight of the weapon: ");
                var weight = Double.parseDouble(in.nextLine());
                System.out.print("Please enter number of the weapon: ");
                var num = Integer.parseInt(in.nextLine());
                System.out.println(
                        shop.addWeapon(new Weapon(name, damage, range, weight, cost), num) ? "Added successfully" : "couldn't add");
            }
        }
    }

    private static void deleteItemsFromShop() {
        System.out.println("Please enter name of which item you want to delete from shop: ");
        showItems(shop.getWeapons());
        System.out.println(shop.removeWeapon(in.nextLine()) ? "removed successfully" : "couldn't remove");
    }

    private static void buyFromShop() {
        System.out.println("Please enter name of the weapon you want to buy: ");
        showItems(shop.getWeapons());
        var name = in.nextLine();
        System.out.println(player.addWeapon(shop.getWeaponByNameAndDecrease(name)) ? "You bought this gun successfully" : "You couldn't buy that gun");
    }
}
