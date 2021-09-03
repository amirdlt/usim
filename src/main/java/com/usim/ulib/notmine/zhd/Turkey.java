package com.usim.ulib.notmine.zhd;

public class Turkey extends DomesticAnimal{

    public Turkey(int x, int y, int time) {
        super(x, y,1, 200,time);
    }

    public static void produceFur(){
        for(int i=0;i<Store.firstProduct.size();i++){
            if(Store.firstProduct.containsKey("fur")){
                int furOldValue = Store.firstProduct.get("fur");
                if(Store.spaceRemaining()>=Store.FIRSTpRODUCTsPACE)
                {Store.firstProduct.replace("fur" , furOldValue , furOldValue+1);}
                return;
            }
        }
        if(Store.spaceRemaining()>=Store.FIRSTpRODUCTsPACE)
        {Store.firstProduct.put("fur" , 1);}
    }
}
