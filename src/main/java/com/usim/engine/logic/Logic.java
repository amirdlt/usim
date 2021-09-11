package com.usim.engine.logic;

import com.usim.engine.internal.Camera;

public interface Logic {

    void init();

    void input();

    void update();

    void render();

    void cleanup();

    void saveState();

    Camera camera();
}
