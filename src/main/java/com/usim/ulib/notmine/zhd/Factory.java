package com.usim.ulib.notmine.zhd;

public class Factory {
    int price;
    int creationTime;

    public Factory(int price, int creationTime) {
        this.price = price;
        this.creationTime = creationTime;
    }

    public static void work(String workShopName) {
        if(workShopName.equalsIgnoreCase("mill")){
            Mill.doMill();}
        if(workShopName.equalsIgnoreCase("loom")){
            Loom.doLoom();}
        if(workShopName.equalsIgnoreCase("milkPackage")){
            MilkPackage.doMilkPackage();}
        if(workShopName.equalsIgnoreCase("baking")){Baking.doBaking();}
        if(workShopName.equalsIgnoreCase("tailoring")){
            Tailoring.doTailoring();}
        if(workShopName.equalsIgnoreCase("iceCream")){
            IceCream.doIceCream();}
    }
}
