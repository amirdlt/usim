package com.usim.ulib.notmine.zhd;

public class Mill extends Factory{

    public Mill(int time) {
        super(150, time);
    }

    public static void doMill() {
        for (int i = 0; i < Store.firstProduct.size(); i++) {
            if (Store.firstProduct.containsKey("egg")) {
                if (Store.firstProduct.get("egg") >= 2) {
                    int eggOldValue = Store.firstProduct.get("egg");
                    Store.firstProduct.replace("egg", eggOldValue, eggOldValue - 1);
                } else {
                    Store.firstProduct.remove("egg");
                    i--;
                }
                for (int j = 0; j < Store.secondProduct.size(); j++) {
                    if (Store.secondProduct.containsKey("flour")) {
                        int flourOldValue = Store.secondProduct.get("flour");
                        if (Store.spaceRemaining() >= Store.SECONDpRODUCTsPACE) {
                            Store.secondProduct.replace("flour", flourOldValue, flourOldValue + 1);
                        }
                        return;
                    }
                }
                if (Store.spaceRemaining() >= Store.SECONDpRODUCTsPACE) {
                    Store.secondProduct.put("flour", 1);
                }
                return;
            }
        }
         System.out.println("EGG PROVIDED!");
    }
}
