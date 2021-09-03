package com.usim.ulib.notmine.zhd;

public class Loom extends Factory {
    public Loom(int time) {
        super(250, time);
    }
    public static void doLoom(){
        for(int i = 0; i< Store.firstProduct.size(); i++){
            if(Store.firstProduct.containsKey("fur")){
                if(Store.firstProduct.get("fur")>=2){
                int furOldValue = Store.firstProduct.get("fur");
                Store.firstProduct.replace("fur" , furOldValue , furOldValue-1);}
                else {
                    Store.firstProduct.remove("fur");i--;}
                for(int j = 0; j< Store.secondProduct.size(); j++){
                    if(Store.secondProduct.containsKey("tissue")){
                        int tissueOldValue = Store.secondProduct.get("tissue");
                        if(Store.spaceRemaining()>= Store.SECONDpRODUCTsPACE)
                        {
                            Store.secondProduct.replace("tissue" , tissueOldValue , tissueOldValue+1);}
                        return;
                    }
                }
                if(Store.spaceRemaining()>= Store.SECONDpRODUCTsPACE)
                {
                    Store.secondProduct.put("tissue" , 1);}
                return;
            }
        }
        System.out.println("FUR PROVIDED!");
    }
}
