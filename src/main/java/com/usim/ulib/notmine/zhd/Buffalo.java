package com.usim.ulib.notmine.zhd;

public class Buffalo extends DomesticAnimal {
    public Buffalo(int x, int y, int time) {
        super(x, y, 1,400, time);
    }

    public static void produceMilk(){
        for(int i = 0; i< Store.firstProduct.size(); i++){
            if(Store.firstProduct.containsKey("milk")){
                int milkOldValue = Store.firstProduct.get("milk");
                if(Store.spaceRemaining()>= Store.FIRSTpRODUCTsPACE)
                {
                    Store.firstProduct.replace("milk" , milkOldValue , milkOldValue+1);}
                return;
            }
        }
        if(Store.spaceRemaining()>= Store.FIRSTpRODUCTsPACE)
        {
            Store.firstProduct.put("milk" , 1);}
    }
}
