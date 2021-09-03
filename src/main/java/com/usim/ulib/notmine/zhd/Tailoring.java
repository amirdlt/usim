package com.usim.ulib.notmine.zhd;

public class Tailoring extends Factory{

    public Tailoring(int time) {
        super(400, time);
    }
    public static void doTailoring(){
        for(int i=0;i<Store.secondProduct.size();i++){
            if(Store.secondProduct.containsKey("tissue")){
                if(Store.secondProduct.get("tissue")>=2){
                int tissueOldValue = Store.secondProduct.get("tissue");
                Store.secondProduct.replace("tissue" , tissueOldValue , tissueOldValue-1);}
                else {Store.secondProduct.remove("tissue");i--;}
                for(int j=0;j<Store.thirdProduct.size();j++){
                    if(Store.thirdProduct.containsKey("dress")){
                        int dressOldValue = Store.thirdProduct.get("dress");
                        if(Store.spaceRemaining()>=Store.THIRDpRODUCTsPACE)
                        {Store.thirdProduct.replace("dress" , dressOldValue , dressOldValue+1);}
                        return;
                    }
                }
                if(Store.spaceRemaining()>=Store.THIRDpRODUCTsPACE)
                {Store.thirdProduct.put("dress" , 1);}
                return;
            }
        }
        System.out.println("TISSUE PROVIDED!");
    }
}
