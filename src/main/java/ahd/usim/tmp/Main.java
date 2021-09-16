package ahd.usim.tmp;

import ahd.usim.engine.internal.Engine;
import ahd.usim.engine.logic.SampleLogic;

public class Main {

    public static void main(String[] args) {
        var engine = Engine.getEngine();
        engine.setLogic(new SampleLogic());
        engine.turnon();
    }
}
