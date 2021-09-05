package com.usim.engine.engine.internal;

import static com.usim.engine.engine.Constants.DEFAULT_GLFW_ICON_PATH;
import static com.usim.engine.engine.util.Utils.ioResourceToByteBuffer;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;

import javax.swing.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
    private String title;
    private int width;
    private int height;
    private long windowHandle;
    private boolean resized;
    private boolean vSync;
    private boolean isInitialized;

    Window(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        resized = false;
        isInitialized = false;
    }

    public void init() {
        if (isInitialized)
            return;
        isInitialized = true;

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL)
            throw new RuntimeException("AHD:: Failed to create the GLFW window");

        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            resized = true;
        });

        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);
        });

        var videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (videoMode != null)
            glfwSetWindowPos(
                    windowHandle,
                    (videoMode.width() - width) / 2,
                    (videoMode.height() - height) / 2
            );

        glfwMakeContextCurrent(windowHandle);

        glfwSwapInterval(vSync ? 1 : 0);

        glfwShowWindow(windowHandle);

        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);

        setIcon(DEFAULT_GLFW_ICON_PATH);
    }

    public void setIcon(String path) {
        IntBuffer w = memAllocInt(1);
        IntBuffer h = memAllocInt(1);
        IntBuffer comp = memAllocInt(1);

        ByteBuffer icon16;
        ByteBuffer icon32;
        try {
            icon16 = ioResourceToByteBuffer(path, 2048);
            icon32 = ioResourceToByteBuffer(path, 4096);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try ( GLFWImage.Buffer icons = GLFWImage.malloc(2) ) {
            ByteBuffer pixels16 = STBImage.stbi_load_from_memory(icon16, w, h, comp, 4);
            assert pixels16 != null;
            icons
                    .position(0)
                    .width(w.get(0))
                    .height(h.get(0))
                    .pixels(pixels16);

            ByteBuffer pixels32 = STBImage.stbi_load_from_memory(icon32, w, h, comp, 4);
            assert pixels32 != null;
            icons
                    .position(1)
                    .width(w.get(0))
                    .height(h.get(0))
                    .pixels(pixels32);

            icons.position(0);
            glfwSetWindowIcon(windowHandle, icons);

            STBImage.stbi_image_free(pixels32);
            STBImage.stbi_image_free(pixels16);
        }
        memFree(comp);
        memFree(h);
        memFree(w);
    }

    public void setClearColor(float r, float g, float b, float alpha) {
        glClearColor(r, g, b, alpha);
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(windowHandle, this.title = title);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isVSync() {
        return vSync;
    }

    public void setVSync(boolean vSync) {
        this.vSync = vSync;
        glfwSwapInterval(vSync ? 1 : 0);
    }

    public void update() {
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }

    public void cleanup() {
        glfwDestroyWindow(windowHandle);
    }
}
