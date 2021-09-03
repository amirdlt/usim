package com.usim.ulib.notmine.zhd;

public class Maps {
    public static final int LENGTH = 6;
    public static final int WIDTH =6;
    public static final char[][] MAP =new char [LENGTH+1][WIDTH+1];

    public Maps() {
        init();
    }
    private  void init(){
        for (int i = 1; i <= LENGTH; i++) {
            for (int j = 1; j <= WIDTH; j++) {
                MAP[i][j] = ' ';
            }
        }

    }
    public static void render(){

        System.out.println("* ".repeat(LENGTH+2));

        for (int i = 1; i <= LENGTH; i++) {
            System.out.print("* ");
            for (int j =1; j <= WIDTH; j++) {
                System.out.print(MAP[i][j]+" ");
            }
            System.out.println("*");
        }
        System.out.println("* ".repeat(LENGTH+2));
    }
    public static void insertPlant(int x,int y){
        MAP[x][y]='G';
    }
    public  static void removePlant(int x,int y){
        MAP[x][y]='-';
    }

}
