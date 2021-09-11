package com.usim.game;

import com.usim.engine.internal.Engine;
import com.usim.engine.logic.SampleLogic;

public class Main {

    public static void main(String[] args) {
        try {
            var engine = Engine.getEngine();
            engine.setLogic(new SampleLogic());
            engine.turnon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
