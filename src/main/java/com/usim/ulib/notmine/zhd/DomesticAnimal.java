package com.usim.ulib.notmine.zhd;

public class DomesticAnimal extends Animal {
    protected double lives;
    protected int price;
    protected int time;

    public DomesticAnimal(int x, int y, int step, int price, int time) {
        super(x, y, 1);
        this.price = price;
        this.lives = 100;
        this.time = time;
    }

    public void passAge(double lives){
        lives = lives * 0.9;
        if (lives <= 50)
            eat();
        if (lives <= 50) {
            lives = 0;
            //TODO remove animal from store class
        }
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    private  void eat() {
        if (Maps.MAP[x][y] == 'G') {
            Maps.removePlant(x, y);
            lives = 100;
        }
    }
}
