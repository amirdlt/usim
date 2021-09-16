package ahd.usim.engine;

import org.joml.Vector3f;

public interface Constants {
    int NANO = 1_000_000_000;
    float NANO_F = 1_000_000_000;
    int MILLION = 1_000_000;
    float MILLION_F = 1_000_000;
    int MILLI = 1_000;
    float MILLI_F = 1_000;
    int KILO = 1024;
    int MEGA = 1_024 * KILO;
    int GIGA = 1_024 * MEGA;
    long TERA = 1_024L * GIGA;
    float PI_F = (float) Math.PI;

    int DEFAULT_TARGET_FPS = 60;
    int DEFAULT_TARGET_UPS = 60;
    int DEFAULT_TARGET_IPS = 30;

    String DEFAULT_GLFW_WINDOW_NAME = "AHD:: GLFW Window";
    int DEFAULT_GLFW_WINDOW_WIDTH = 1280;
    int DEFAULT_GLFW_WINDOW_HEIGHT = 720;

    String DEFAULT_RESOURCE_ROOT_PATH = ".\\src\\main\\resources\\";

    String DEFAULT_GLFW_ICON_PATH = DEFAULT_RESOURCE_ROOT_PATH + "icons\\usim-icon.png";
    String DEFAULT_SWING_ICON_PATH = DEFAULT_RESOURCE_ROOT_PATH + "icons\\usim-icon.png";


    int GL_LOG_MAX_LENGTH = 1024;

    float DEFAULT_FIELD_OF_VIEW = (float) Math.PI / 3;
    float DEFAULT_Z_NEAR = 0.01f;
    float DEFAULT_Z_FAR = 1000f;

    float DEFAULT_MOUSE_MOVEMENT_SENSITIVITY = 0.002f;
    float DEFAULT_CAMERA_MOVEMENT_SENSITIVITY = 0.05f;

    Vector3f DEFAULT_MESH_COLOR = new Vector3f(1);
    Vector3f DEFAULT_AMBIENT_COLOR = new Vector3f(1);
    Vector3f DEFAULT_DIFFUSE_COLOR = new Vector3f(1);
    Vector3f DEFAULT_SPECULAR_COLOR = new Vector3f(1);

    String VERTEX_SHADER_FILE_RESOURCE_PATH = DEFAULT_RESOURCE_ROOT_PATH + "shaders\\vertex.vs";
    String FRAGMENT_SHADER_FILE_RESOURCE_PATH = DEFAULT_RESOURCE_ROOT_PATH + "shaders\\fragment.fs";

    String PROJECTION_MATRIX_UNIFORM_NAME = "projectionMatrix";
    String MODEL_VIEW_MATRIX_UNIFORM_NAME = "modelViewMatrix";
    String TEXTURE_SAMPLER_UNIFORM_NAME = "texture_sampler";
}
