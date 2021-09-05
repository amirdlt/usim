package com.usim.engine.game;

import com.usim.engine.engine.internal.Engine;
import com.usim.engine.engine.logic.DummyGame;
import com.usim.engine.engine.logic.Logic;

import java.util.ArrayList;
import java.util.Date;

public class Main {


    public static void main(String[] args) {
        try {
            var engine = Engine.get();
            engine.setLogic(new DummyGame());
            engine.turnon();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
