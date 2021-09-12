package ahd.usim.engine.internal;

import java.util.HashMap;
import java.util.Map;

public final class WindowManager {
    private final Map<String, Window> windowsMap;

    WindowManager() {
        windowsMap = new HashMap<>();
    }

    public void createWindow(String name, String title, int width, int height, boolean vSync) {
        windowsMap.put(name, new Window(title, width, height, vSync));
    }

    public Window getWindow(String name) {
        return windowsMap.get(name);
    }

    public void update() {
        windowsMap.values().forEach(Window::update);
    }

    public void cleanup() {
        windowsMap.values().forEach(Window::cleanup);
    }
}
