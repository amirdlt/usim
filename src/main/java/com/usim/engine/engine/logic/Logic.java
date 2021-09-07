package com.usim.engine.engine.logic;

import com.usim.engine.engine.internal.Camera;

public interface Logic {

    void init();

    void input();

    void update();

    void render();

    void cleanup();

    Camera camera();
}
