package com.usim.ulib.notmine.zhd;

import java.io.Serializable;
import java.util.HashMap;

public class Store implements Serializable {
    public static final int MAXsPACE=30;
    public static final int FIRSTpRODUCTsPACE = 1;
    public static final int SECONDpRODUCTsPACE = 2;
    public static final int THIRDpRODUCTsPACE = 4;
    public static final int WILDaNIMALsPACE = 15;

    public static int coins;
    public static HashMap<String, Integer> wildAnimals = new HashMap<>();
    public static HashMap<String, Integer> animals = new HashMap<>();
    public static HashMap<String, Integer> firstProduct = new HashMap<>();
    public static HashMap<String, Integer> secondProduct = new HashMap<>();
    public static HashMap<String, Integer> thirdProduct = new HashMap<>();


    public static void reset() {
        animals.clear();
        wildAnimals.clear();
        firstProduct.clear();
        secondProduct.clear();
        thirdProduct.clear();
    }

    private Store() {}

    public static int spaceRemaining(){
        return MAXsPACE - ((firstProduct.size() * FIRSTpRODUCTsPACE) + (secondProduct.size() * SECONDpRODUCTsPACE) + (thirdProduct.size() * THIRDpRODUCTsPACE) + (wildAnimals.size() * WILDaNIMALsPACE));
    }

}
