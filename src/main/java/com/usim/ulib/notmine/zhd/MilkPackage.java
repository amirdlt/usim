package com.usim.ulib.notmine.zhd;

public class MilkPackage extends Factory {
    public MilkPackage(int time) {
        super(400, time);
    }
    public static void doMilkPackage(){
        for(int i = 0; i< Store.firstProduct.size(); i++){
            if(Store.firstProduct.containsKey("milk")){
                if(Store.firstProduct.get("milk")>=2){
                int milkOldValue = Store.firstProduct.get("milk");
                Store.firstProduct.replace("milk" , milkOldValue , milkOldValue-1);}
                else {
                    Store.firstProduct.remove("milk");i--;}
                for(int j = 0; j< Store.secondProduct.size(); j++){
                    if(Store.secondProduct.containsKey("packedMilk")){
                        int packedMilkOldValue = Store.secondProduct.get("packedMilk");
                        if(Store.spaceRemaining()>= Store.SECONDpRODUCTsPACE)
                        {
                            Store.secondProduct.replace("packedMilk" , packedMilkOldValue , packedMilkOldValue+1);}
                        return;
                    }
                }
                if(Store.spaceRemaining()>= Store.SECONDpRODUCTsPACE)
                {
                    Store.secondProduct.put("packedMilk" , 1);}
                return;
            }
        }
        System.out.println("MILK PROVIDED!");
    }
}
