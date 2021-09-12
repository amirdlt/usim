package ahd.usim.engine.internal;

import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public final class Input {
    private final Vector2d previousPosition;
    private final Vector2d currentPosition;
    private final Vector2f displayVector;
    private boolean inWindow;
    private boolean leftButtonPressed;
    private boolean rightButtonPressed;
    private boolean middleButtonPressed;
    private long windowHandle;

    Input() {
        previousPosition = new Vector2d(-1, -1);
        currentPosition = new Vector2d();
        displayVector = new Vector2f();
        inWindow = false;
        leftButtonPressed = false;
        rightButtonPressed = false;
        middleButtonPressed = false;
        windowHandle = 0;
    }

    void init() {
        windowHandle = Engine.getEngine().getWindow().getWindowHandle();
        glfwSetCursorPosCallback(windowHandle, (_windowHandle, xPos, yPos) -> currentPosition.set(xPos, yPos));
        glfwSetCursorEnterCallback(windowHandle, (_windowHandle, inWindow) -> this.inWindow = inWindow);
        glfwSetMouseButtonCallback(windowHandle, (_windowHandle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_RIGHT && action == GLFW_PRESS;
            middleButtonPressed = button == GLFW_MOUSE_BUTTON_MIDDLE && action == GLFW_PRESS;
        });
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
        });
//        glfwSetScrollCallback(windowHandle, )
    }

    public boolean isKeyPressed(int key) {
        return glfwGetKey(windowHandle, key) == GLFW_PRESS;
    }

    public void input() {
        displayVector.set(0, 0);
        if (inWindow && previousPosition.x > 0 && previousPosition.y > 0)
            displayVector.set(currentPosition.y - previousPosition.y, currentPosition.x - previousPosition.x);
        previousPosition.set(currentPosition);
    }

    public boolean isMouseLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isMouseRightButtonPressed() {
        return rightButtonPressed;
    }

    public boolean isMouseMiddleButtonPressed() {
        return middleButtonPressed;
    }

    public boolean isMouseInWindow() {
        return inWindow;
    }

    public Vector2f getDisplayVector() {
        return displayVector;
    }

    public Vector2d getMousePosition() {
        return currentPosition;
    }
}
