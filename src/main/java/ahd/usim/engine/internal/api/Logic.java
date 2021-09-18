package ahd.usim.engine.internal.api;

import ahd.usim.engine.internal.*;

public interface Logic extends Rebuild, Visible, Updatable {

    void input();

    void saveState();

    Camera camera();
}
