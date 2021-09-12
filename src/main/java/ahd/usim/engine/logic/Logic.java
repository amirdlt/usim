package ahd.usim.engine.logic;

import ahd.usim.engine.internal.Camera;

public interface Logic {

    void init();

    void input();

    void update();

    void render();

    void cleanup();

    void saveState();

    Camera camera();
}
