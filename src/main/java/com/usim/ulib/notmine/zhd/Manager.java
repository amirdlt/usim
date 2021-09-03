package com.usim.ulib.notmine.zhd;

import java.io.*;
import java.util.*;
import java.util.Map;

import static com.usim.ulib.notmine.zhd.Maps.MAP;
import static com.usim.ulib.notmine.zhd.Store.*;

public class Manager {
    public static final Map<String, String> accounts;

    static {
        Map<String, String> tmp1 = new HashMap<>();
        //        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("accounts.bin"))) {
        //                oos.writeObject(tmp1);
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //             System.out.println("sjhnc");

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("accounts.bin"))) {
            //noinspection unchecked
            tmp1 = (Map<String, String>) ois.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        accounts = tmp1;
    }

    public static int price(String itemName) {
        int price = 0;
        if(itemName.equalsIgnoreCase("cat")){price = 150; }
        if(itemName.equalsIgnoreCase("dog")){price =100; }
        if(itemName.equalsIgnoreCase("chicken")){price =100;}
        if(itemName.equalsIgnoreCase("buffalo")){price =400; }
        if(itemName.equalsIgnoreCase("turkey")){price =200; }
        if(itemName.equalsIgnoreCase("mill")){price = 150; }
        if(itemName.equalsIgnoreCase("loom")){price = 250; }
        if(itemName.equalsIgnoreCase("milkPackage")){price = 400; }
        if(itemName.equalsIgnoreCase("baking")){price = 250; }
        if(itemName.equalsIgnoreCase("tailoring")){price = 400; }
        if(itemName.equalsIgnoreCase("iceCream")){price = 550; }
        return price;
    }

    public static int price2(String itemName){
        int price =0;
        if(itemName.equalsIgnoreCase("tiger")){price = 500; }
        if(itemName.equalsIgnoreCase("lion")){price =300; }
        if(itemName.equalsIgnoreCase("bear")){price =400;}
        if(itemName.equalsIgnoreCase("egg")){price =15; }
        if(itemName.equalsIgnoreCase("fur")){price =20; }
        if(itemName.equalsIgnoreCase("milk")){price = 25; }
        if(itemName.equalsIgnoreCase("flour")){price = 40; }
        if(itemName.equalsIgnoreCase("tissue")){price = 50; }
        if(itemName.equalsIgnoreCase("packedMilk")){price = 60; }
        if(itemName.equalsIgnoreCase("bread")){price = 80; }
        if (itemName.equalsIgnoreCase("dress"))
            price = 100;
        if (itemName.equalsIgnoreCase("iceCream"))
            price = 120;
        return price;
    }

    public static void buy(String animalName) {
        if (coins < price(animalName)) {
            System.out.println("You can't buy this animal.");
        } else {
            coins -= price(animalName);
            animals.put(animalName, animals.getOrDefault(animalName, 0) + 1);
        }
    }

    public static void pickUp(String X , String Y){
        int x = Integer.parseInt(X);
        int y = Integer.parseInt(Y);
        if(Maps.WIDTH>=y&& Maps.LENGTH>=x&& MAP[x][y]!='-'){
            MAP[x][y] = '-';}
        else if(MAP[x][y] == '-'){
            System.out.println("NO PRODUCT!");}
        else System.out.println("INVALID COORDINATE!");
    }

    public static void well() {
        if(FoodAndWater.isEmpty){
            FoodAndWater.isEmpty = false;
            FoodAndWater.spaceRemaining = 100;}
        else System.out.println("BUCKET IS NOT EMPTY!");
    }

    public static void plant(String X, String Y) {
        if(FoodAndWater.spaceRemaining<20){
            System.out.println("THERE IS NOT ENOUGH WATER IN BUCKET!");
            return;}
        else{
        int x=Integer.parseInt(X);
        int y=Integer.parseInt(Y);
        FoodAndWater.spaceRemaining-=20;
        Maps.insertPlant(x,y);
        Maps.render();
        }
    }

    public static void work(String workShopName){
      if(coins>=price(workShopName)){
          coins-=price(workShopName);
          Factory.work(workShopName);
      }
    }

    public static void cage(String X , String Y) {
        int x = Integer.parseInt(X);
        int y = Integer.parseInt(Y);
        if (MAP[x][y] == 'L') {
            Lion.stop -= 1;
            if (Lion.stop <= 0) {
                for (int i = 0; i < Store.wildAnimals.size(); i++) {
                    if (Store.wildAnimals.containsKey("lion")) {
                        int lionOldValue = Store.wildAnimals.get("lion");
                        if (Store.spaceRemaining() >= Store.WILDaNIMALsPACE) {
                            Store.wildAnimals.replace("lion", lionOldValue, lionOldValue + 1);
                        }
                        return;
                    }
                }
                if (Store.spaceRemaining() >= Store.WILDaNIMALsPACE) {
                    Store.wildAnimals.put("lion", 1);
                }
            }
        }
        if (MAP[x][y] == 'T') {
            Tiger.stop -= 1;
            if (Tiger.stop <= 0) {
                for (int i = 0; i < Store.wildAnimals.size(); i++) {
                    if (Store.wildAnimals.containsKey("tiger")) {
                        int tigerOldValue = Store.wildAnimals.get("tiger");
                        if (spaceRemaining() >= WILDaNIMALsPACE)
                            Store.wildAnimals.replace("tiger", tigerOldValue, tigerOldValue + 1);
                        return;
                    }
                }
                if (Store.spaceRemaining() >= Store.WILDaNIMALsPACE)
                    Store.wildAnimals.put("tiger", 1);
            }
        }
        if(MAP[x][y] == 'B'){
            Bear.stop -= 1;
            if (Bear.stop == 0) {
                for (int i = 0; i < Store.wildAnimals.size(); i++) {
                    if (Store.wildAnimals.containsKey("bear")) {
                        int bearOldValue = Store.wildAnimals.get("bear");
                        if (Store.spaceRemaining() >= Store.WILDaNIMALsPACE) {
                            Store.wildAnimals.replace("bear", bearOldValue, bearOldValue + 1);
                        }
                        return;
                    }
                }
                if (Store.spaceRemaining() >= Store.WILDaNIMALsPACE)
                    Store.wildAnimals.put("bear", 1);
            }
        }
        else System.out.println("NO WILD ANIMAL DETECTED!");
    }

    public static void truckLoad(String itemName){
        if(itemName.equalsIgnoreCase("tiger")||itemName.equalsIgnoreCase("bear")||itemName.equalsIgnoreCase("lion")){truckWildAnimal(itemName);}
        if(itemName.equalsIgnoreCase("egg")||itemName.equalsIgnoreCase("milk")||itemName.equalsIgnoreCase("fur")){truckFirstProduct(itemName);}
        if(itemName.equalsIgnoreCase("flour")||itemName.equalsIgnoreCase("packedMilk")||itemName.equalsIgnoreCase("tissue")){truckSecondProduct(itemName);}
        if(itemName.equalsIgnoreCase("bread")||itemName.equalsIgnoreCase("iceCream")||itemName.equalsIgnoreCase("dress")){truckThirdProduct(itemName);}
    }

    private static void truckWildAnimal(String itemName){
//        if(Vehicle.spaceRemaining>=15) {
//            Vehicle.productsLoaded.add(itemName);
//            Vehicle.spaceRemaining -= 15;
//            for (int i = 0; i < Store.wildAnimals.size(); i++) {
//                if (Store.wildAnimals.containsKey(itemName) && Store.wildAnimals.get(itemName) >= 2) {
//                    int oldValue = Store.wildAnimals.get(itemName);
//                    Store.wildAnimals.replace(itemName, oldValue, oldValue - 1);
//                    break;
//                } else if (Store.wildAnimals.containsKey(itemName) && Store.wildAnimals.get(itemName) == 1) {
//                    Store.wildAnimals.remove(itemName);
//                    break;
//                }
//            }
//        }
//        else System.out.println("NOT ENOUGH SPACE!");
    }
    private static void truckFirstProduct(String itemName){
//        if(Vehicle.spaceRemaining>=1) {
//            Vehicle.productsLoaded.add(itemName);
//            Vehicle.spaceRemaining -= 1;
//            for (int i = 0; i < Store.firstProduct.size(); i++) {
//                if (Store.firstProduct.containsKey(itemName) && Store.firstProduct.get(itemName) >= 2) {
//                    int oldValue = Store.firstProduct.get(itemName);
//                    Store.firstProduct.replace(itemName, oldValue, oldValue - 1);
//                    break;
//                } else if (Store.firstProduct.containsKey(itemName) && Store.firstProduct.get(itemName) == 1) {
//                    Store.firstProduct.remove(itemName);
//                    break;
//                }
//            }
//        }
//        else System.out.println("NOT ENOUGH SPACE!");
    }
    private static void truckSecondProduct(String itemName) {
//        if (Vehicle.spaceRemaining >= 2) {
//            Vehicle.productsLoaded.add(itemName);
//            Vehicle.spaceRemaining -= 2;
//            for (int i = 0; i < Store.secondProduct.size(); i++) {
//                if (Store.secondProduct.containsKey(itemName) && Store.secondProduct.get(itemName) >= 2) {
//                    int oldValue = Store.secondProduct.get(itemName);
//                    Store.secondProduct.replace(itemName, oldValue, oldValue - 1);
//                    break;
//                } else if (Store.secondProduct.containsKey(itemName) && Store.secondProduct.get(itemName) == 1) {
//                    Store.secondProduct.remove(itemName);
//                    break;
//                }
//            }
//        }
//        else System.out.println("NOT ENOUGH SPACE!");
    }
    private static void truckThirdProduct(String itemName){
//        if (Vehicle.spaceRemaining >= 4) {
//            Vehicle.productsLoaded.add(itemName);
//            Vehicle.spaceRemaining -= 4;
//            for (int i = 0; i < Store.thirdProduct.size(); i++) {
//                if (Store.thirdProduct.containsKey(itemName) && Store.thirdProduct.get(itemName) >= 2) {
//                    int oldValue = Store.thirdProduct.get(itemName);
//                    Store.thirdProduct.replace(itemName, oldValue, oldValue - 1);
//                    break;
//                } else if (Store.thirdProduct.containsKey(itemName) && Store.thirdProduct.get(itemName) == 1) {
//                    Store.thirdProduct.remove(itemName);
//                    break;
//                }
//            }
//        }
//        else System.out.println("NOT ENOUGH SPACE!");
    }

    public static void truckUnLoad(String itemName){
        if(itemName.equalsIgnoreCase("tiger")||itemName.equalsIgnoreCase("bear")||itemName.equalsIgnoreCase("lion")){unTruckWildAnimal(itemName);}
        if(itemName.equalsIgnoreCase("egg")||itemName.equalsIgnoreCase("milk")||itemName.equalsIgnoreCase("fur")){unTruckFirstProduct(itemName);}
        if(itemName.equalsIgnoreCase("flour")||itemName.equalsIgnoreCase("packedMilk")||itemName.equalsIgnoreCase("tissue")){unTruckSecondProduct(itemName);}
        if(itemName.equalsIgnoreCase("bread")||itemName.equalsIgnoreCase("iceCream")||itemName.equalsIgnoreCase("dress")){unTruckThirdProduct(itemName);}
    }

    private static void unTruckWildAnimal(String itemName){
//            Vehicle.productsLoaded.remove(itemName);
//            Vehicle.spaceRemaining += 15;
//            for (int i = 0; i < Store.wildAnimals.size(); i++) {
//                if (Store.wildAnimals.containsKey(itemName)) {
//                    int oldValue = Store.wildAnimals.get(itemName);
//                    if(Store.spaceRemaining()>=Store.WILDaNIMALsPACE)
//                    {Store.wildAnimals.replace(itemName, oldValue, oldValue + 1);}
//                    break;
//                }
//            }
//            if(Store.spaceRemaining()>=Store.WILDaNIMALsPACE)
//            {Store.wildAnimals.put(itemName , 1);}
        }
    private static void unTruckFirstProduct(String itemName){
//        Vehicle.productsLoaded.remove(itemName);
//        Vehicle.spaceRemaining += 1;
//        for (int i = 0; i < Store.firstProduct.size(); i++) {
//            if (Store.firstProduct.containsKey(itemName)) {
//                int oldValue = Store.firstProduct.get(itemName);
//                if(Store.spaceRemaining()>=Store.FIRSTpRODUCTsPACE)
//                {Store.firstProduct.replace(itemName, oldValue, oldValue + 1);}
//                break;
//            }
//        }
//        if(Store.spaceRemaining()>=Store.FIRSTpRODUCTsPACE)
//        {Store.firstProduct.put(itemName , 1);}
    }
    private static void unTruckSecondProduct(String itemName) {
//        Vehicle.productsLoaded.remove(itemName);
//        Vehicle.spaceRemaining += 2;
//        for (int i = 0; i < Store.secondProduct.size(); i++) {
//            if (Store.secondProduct.containsKey(itemName)) {
//                int oldValue = Store.secondProduct.get(itemName);
//                if(Store.spaceRemaining()>=Store.SECONDpRODUCTsPACE)
//                {Store.secondProduct.replace(itemName, oldValue, oldValue + 1);}
//                break;
//            }
//        }
//        if(Store.spaceRemaining()>=Store.SECONDpRODUCTsPACE)
//        {Store.secondProduct.put(itemName , 1);}
    }
    private static void unTruckThirdProduct(String itemName){
//        Vehicle.productsLoaded.remove(itemName);
//        Vehicle.spaceRemaining += 4;
//        for (int i = 0; i < Store.thirdProduct.size(); i++) {
//            if (Store.thirdProduct.containsKey(itemName)) {
//                int oldValue = Store.thirdProduct.get(itemName);
//                if(Store.spaceRemaining()>=Store.THIRDpRODUCTsPACE)
//                {Store.thirdProduct.replace(itemName, oldValue, oldValue + 1);}
//                break;
//            }
//        }
//        if(Store.spaceRemaining()>=Store.THIRDpRODUCTsPACE)
//        {Store.thirdProduct.put(itemName , 1);}
    }


    public static void truckGo(){
//        for(int i=0;i<Vehicle.productsLoaded.size();i++){
//            Store.coins+= price2(Vehicle.productsLoaded.get(i));
//        }
//        for(int i=0;i<Vehicle.productsLoaded.size();i++){
//            Vehicle.productsLoaded.remove(i);
//            i--;
//        }
//        Vehicle.spaceRemaining = Vehicle.MAXsPACE;
    }

    public static void save() {

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("accounts.bin"))) {
            oos.writeObject(accounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void menu() {
        var rows = new ArrayList<List<Object>>();

            rows.add(List.of("   login"));
            rows.add(List.of("   signup"));

        Menu.showMenu(List.of("    MENU "),rows,List.of(15,10, 10));
    }

    public static void menu2() {
        var rows = new ArrayList<List<Object>>();

        rows.add(List.of("   UserName"));
        rows.add(List.of("   Password"));

        Menu.showMenu(List.of("    MENU "),rows,List.of(15,10, 10));
    }

    public static void menu3() {
        var rows = new ArrayList<List<Object>>();
        rows.add(List.of("    START"));
        rows.add(List.of("   LOG OUT"));
        rows.add(List.of("   SETTINGS"));
        rows.add(List.of("    EXIT"));
        Menu.showMenu(List.of("    MENU"),rows,List.of(15,10, 10));
    }

    public static void plantExist() {
        int c=0;
        for(int i = 0; i< Maps.LENGTH; i++){
            for(int j = 0; j< Maps.WIDTH; j++){
                if(MAP[i][j] == '-'){
                    c+=1;
                }
            }
        }
        if(c== Maps.LENGTH* Maps.WIDTH){
            System.out.println("NO PLANT!");
        }
    }
}
