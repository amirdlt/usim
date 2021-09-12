package ahd.usim.game;

import ahd.usim.engine.internal.Engine;
import ahd.usim.engine.logic.SampleLogic;

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
