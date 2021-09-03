package com.usim.ulib.notmine.zhd;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Manager manager = new Manager();
        InputProcessor inputProcessor = new InputProcessor(manager);
        inputProcessor.run();
        Manager.save();
    }
}
