package com.usim.ulib.notmine.zhd;

public class Baking extends Factory {
    public Baking(int time) {
        super(250, time);
    }

    public static void doBaking() {
        for(int i = 0; i< Store.secondProduct.size(); i++){
            if(Store.secondProduct.containsKey("flour")){
                if(Store.secondProduct.get("flour")>=2){
                int flourOldValue = Store.secondProduct.get("flour");
                Store.secondProduct.replace("flour" , flourOldValue , flourOldValue-1);}
                else {
                    Store.secondProduct.remove("flour"); i--;
                }
                for(int j = 0; j< Store.thirdProduct.size(); j++){
                    if(Store.thirdProduct.containsKey("bread")) {
                        int breadOldValue = Store.thirdProduct.get("bread");
                        if(Store.spaceRemaining()>= Store.THIRDpRODUCTsPACE)
                        {
                            Store.thirdProduct.replace("bread" , breadOldValue , breadOldValue+1);}
                        return;
                    }
                }
                if(Store.spaceRemaining()>= Store.THIRDpRODUCTsPACE)
                {
                    Store.thirdProduct.put("bread" , 1);}
                return;
            }
        }
        System.out.println("FLOUR PROVIDED!");
    }
}
