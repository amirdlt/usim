package com.usim.ulib.notmine.zhd;

public class IceCream extends Factory{
    public IceCream(int time) {
        super(550, time);
    }
    public static void doIceCream(){
        for(int i = 0; i< Store.secondProduct.size(); i++){
            if(Store.secondProduct.containsKey("packedMilk")){
                if(Store.secondProduct.get("packedMilk")>=2){
                int packedMilkOldValue = Store.secondProduct.get("packedMilk");
                Store.secondProduct.replace("packedMilk" , packedMilkOldValue , packedMilkOldValue-1);}
                else {
                    Store.secondProduct.remove("packedMilk");i--;}
                for(int j = 0; j< Store.thirdProduct.size(); j++){
                    if(Store.thirdProduct.containsKey("iceCream")){
                        int iceCreamOldValue = Store.thirdProduct.get("iceCream");
                        if(Store.spaceRemaining()>= Store.THIRDpRODUCTsPACE)
                        {
                            Store.thirdProduct.replace("iceCream" , iceCreamOldValue , iceCreamOldValue+1);}
                        return;
                    }
                }
                if(Store.spaceRemaining()>= Store.THIRDpRODUCTsPACE)
                {
                    Store.thirdProduct.put("iceCream" , 1);}
                return;
            }
        }
        System.out.println("PACKED MILK PROVIDED!");
    }
}
