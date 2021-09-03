package com.usim.ulib.notmine.zhd;

import java.io.*;
import java.util.Locale;
import java.util.Scanner;

import static com.usim.ulib.notmine.zhd.Store.*;

public class InputProcessor {
    private Manager manager;
    private User user;
    private Level level = new Level();

    public InputProcessor(Manager manager) {
        this.manager = manager;
    }

    Scanner scanner = new Scanner(System.in);

    public void run(){
            String input;
            Manager.menu();

            while (!((input=scanner.nextLine().toUpperCase(Locale.ROOT).trim()).startsWith("LOGIN") ||input.startsWith("SIGNUP")))
                System.out.println("PLEASE ENTER LOGIN OR SIGNUP.");

            Manager.menu2();

            while (!((input=scanner.nextLine().toUpperCase(Locale.ROOT).trim()).startsWith("LOGIN") ||input.startsWith("SIGNUP")))
                System.out.println("PLEASE ENTER SIGNUP OR LOGIN AT THE BEGINNING OF THE SENTENCE.");
            var userInfo =input.substring(input.startsWith("LOGIN")?6:7).split("\\s");


            if (input.startsWith("SIGNUP")){
                if (Manager.accounts.containsKey(userInfo[0])){
                    System.out.println("THE USERNAME "+userInfo[0]+" IS NOT AVAILABLE.");
                    System.out.println("PLEASE ENTER AGAIN");
                    run();
                }

                this.user=new User(userInfo[0], userInfo[1]);
                Manager.accounts.put(userInfo[0], userInfo[1] );

            } else if (input.startsWith("LOGIN") && Manager.accounts.containsKey(userInfo[0]) && Manager.accounts.get(userInfo[0])
                    .equals(userInfo[1])) {
                user = new User(userInfo[0], userInfo[1]);
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("level" + userInfo[0] + ".bin"))) {
                    level = (Level) ois.readObject();
                } catch (Exception e) {
                    level = Level.levels.get(0);
                    Store.coins = level.getCoin();
                }
            } else {
                System.out.println("process failed try again");
                run();
                return;
            }
            Manager.menu3();

        while (!((input = scanner.nextLine().toUpperCase(Locale.ROOT).trim()).startsWith("START") || input.startsWith("LOG OUT")
                || input.startsWith("EXIT") || input.startsWith("SETTINGS")))
                System.out.println("INPUT IS INCORRECT.");


             if (input.startsWith("LOG OUT")){
                run();

             } else if (input.startsWith("SETTINGS")) {
                 System.out.println(":)");
             } else if (input.startsWith("START") && !(input.startsWith("EXIT"))) {

                 while (!input.startsWith("EXIT")) {

                     if ((level.checkLevel())) {
                         System.out.println("you have passed level : " + level.getLevel());
                         var t = level.getTime();
                         level = Level.levels.get(level.getLevel());
                         coins += level.getCoin() + (t <= level.getExpectedTime() ? level.getReward() : 0);
                         reset();
                     }

                     Manager.plantExist();

                     input = scanner.nextLine();
                     input = input.toUpperCase();

                     if (input.startsWith("BUY")) {
                         String[] split = input.split(" ");
                         Manager.buy(split[1]);
                         level.getAnimals().add(switch (split[1].toLowerCase()) {
                             case "cat" -> new Cat((int) (Math.random() * 6), (int) (Math.random() * 6));
                             case "dog" -> new Dog((int) (Math.random() * 6), (int) (Math.random() * 6));
                             case "chicken" -> new Chicken((int) (Math.random() * 6), (int) (Math.random() * 6), level.getTime());
                             case "buffalo" -> new Buffalo((int) (Math.random() * 6), (int) (Math.random() * 6), level.getTime());
                             case "turkey" -> new Turkey((int) (Math.random() * 6), (int) (Math.random() * 6), level.getTime());
                             default -> null;
                         });
                         level.getFactories().add(switch (split[1].toLowerCase()) {
                             case "mill" -> new Mill(level.getTime());
                             case "loom" -> new Loom(level.getTime());
                             case "milkpackage" -> new MilkPackage(level.getTime());
                             case "baking" -> new Baking(level.getTime());
                             case "tailoring" -> new Tailoring(level.getTime());
                             case "icecream" -> new IceCream(level.getTime());
                             default -> null;
                         });
                         System.out.println("ACTION DONE!");
                     } else if (input.startsWith("PICKUP")) {
                         String[] split = input.split(" ");
                         Manager.pickUp(split[1], split[2]);
                         System.out.println("ACTION DONE!");
                     } else if (input.startsWith("WELL")) {
                         Manager.well();
                         System.out.println("ACTION DONE!");
                     } else if (input.startsWith("PLANT")) {
                         String[] split = input.split(" ");
                         Manager.plant(split[1], split[2]);
                         System.out.println("ACTION DONE!");
                     } else if (input.startsWith("WORK")) {
                         String[] split = input.split(" ");
                         Manager.work(split[1]);
                         System.out.println("ACTION DONE!");
                     } else if (input.startsWith("CAGE")) {
                         String[] split = input.split(" ");
                         Manager.cage(split[1], split[2]);
                         System.out.println("ACTION DONE!");
                     } else if (input.startsWith("TURN")) {
                         var change = Integer.parseInt(input.split(" ")[1]);
                         level.time(change);
                         level.getAnimals().stream().filter(Chicken.class::isInstance).map(c -> (Chicken) c).forEach(chicken -> {
                             var numOfEggs = (level.getTime() - chicken.time) / 2;
                             chicken.time = numOfEggs != 0 ? level.getTime() : chicken.time;
                             firstProduct.put("egg", firstProduct.getOrDefault("egg", 0) + numOfEggs);
                         });

                         level.getAnimals().stream().filter(Buffalo.class::isInstance).map(c -> (Buffalo) c).forEach(buffalo -> {
                             var numOfEggs = (level.getTime() - buffalo.time) / 2;
                             buffalo.time = numOfEggs != 0 ? level.getTime() : buffalo.time;
                             firstProduct.put("milk", firstProduct.getOrDefault("milk", 0) + numOfEggs);
                         });

                         level.getAnimals().stream().filter(Turkey.class::isInstance).map(c -> (Turkey) c).forEach(turkey -> {
                             var numOfEggs = (level.getTime() - turkey.time) / 2;
                             turkey.time = numOfEggs != 0 ? level.getTime() : turkey.time;
                             firstProduct.put("fur", firstProduct.getOrDefault("fur", 0) + numOfEggs);
                         });

                         level.getFactories().stream().filter(IceCream.class::isInstance).map(e -> (IceCream) e).forEach(iceCream -> {
                             var numOfEggs = (level.getTime() - iceCream.creationTime) / 2;
                             iceCream.creationTime = numOfEggs != 0 ? level.getTime() : iceCream.creationTime;
                             for (int i = 0; i < numOfEggs; i++)
                                 IceCream.doIceCream();
                         });

                         //TODO
                         System.out.println("ACTION DONE!");
                     } else if (input.startsWith("TRUCK LOAD")) {
                         String[] split = input.split(" ");
                         Manager.truckLoad(split[1]);
                         System.out.println("ACTION DONE!");
                     } else if (input.startsWith("TRUCK UNLOAD")) {
                         String[] split = input.split(" ");
                         Manager.truckUnLoad(split[1]);
                         System.out.println("ACTION DONE!");
                     } else if (input.startsWith("TRUCK GO")) {
                         Manager.truckGo();
                         System.out.println("ACTION DONE!");
                     }

                     if (input.startsWith("LOG OUT")) {
                         try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("level" + userInfo[0] + ".bin"))) {
                             oos.writeObject(this.level);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                         run();
                     }
                 }
             }
    }
}

