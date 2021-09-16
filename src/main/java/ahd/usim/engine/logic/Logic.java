package ahd.usim.engine.logic;

import ahd.usim.engine.internal.Camera;
import ahd.usim.engine.internal.Renderer;

public interface Logic {

    void init();

    void input();

    void update();

    void render();

    void cleanup();

    void saveState();

    Renderer renderer();

    Camera camera();
}
