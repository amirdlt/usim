package com.usim.engine.game;

import com.usim.engine.engine.internal.Engine;
import com.usim.engine.engine.logic.DummyGame;
import com.usim.engine.engine.logic.Logic;

public class Main {
 
    public static void main(String[] args) {
        try {
            var engine = Engine.get();
            Logic gameLogic = new DummyGame();
            engine.setLogic(gameLogic);
            engine.getWindow().init();
            engine.getWindow().setClearColor(.5f, .5f, .5f, .5f);
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
