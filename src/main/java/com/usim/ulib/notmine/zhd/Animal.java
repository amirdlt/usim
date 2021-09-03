package com.usim.ulib.notmine.zhd;

import java.util.Random;

public class Animal {

    protected int step;
    protected int x;
    protected int y;

    public Animal(int x,int y,int step) {
        this.x=x;
        this.y=y;
        this.step=step;
    }

    public static void move(int x, int y , int step){
        Random random = new Random();
        int a =random.nextInt(4);
        if(a==0){x+=step;
            if(x>= Maps.LENGTH) { a =random.nextInt(4);move(x , y, step );}
        }
        else if(a==1){x-=step;
            if(x<= Maps.LENGTH) { a =random.nextInt(4);move(x , y, step );}
        }
        else if(a==2){y+=step;
            if(y>= Maps.WIDTH) { a =random.nextInt(4);move(x , y, step );}
        }
        else if(a==3){y-=step;
            if(y>= Maps.WIDTH) { a =random.nextInt(4);move(x , y, step );}
        }

    }
}
