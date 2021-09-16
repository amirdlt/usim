#version 330

in vec2 outTexCoord;
in vec4 outColor;

in vec3 mvVertexNormal;
in vec3 mvVertexPos;

out vec4 fragColor;

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct PointLight {
    vec3 color;
    vec3 position;
    float intensity;
    Attenuation attenuation;
};

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

uniform sampler2D texture_sampler;

uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 textureCoordinates) {
    if (material.hasTexture == 1) {
        ambientC = texture(texture_sampler, textureCoordinates);
        diffuseC = ambientC;
        specularC = ambientC;
    } else {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

vec4 calculatePointLight(PointLight light, vec3 position, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    vec3 lightDirection = light.position - position;
    vec3 toLightSource = normalize(lightDirection);
    float diffuseFactor = max(dot(normal, toLightSource), 0);
    diffuseColor = diffuseC * vec4(light.color, 1) * light.intensity * diffuseFactor;

    vec3 cameraDirection = normalize(-position);
    vec3 fromLightSource = -toLightSource;
    vec3 reflectedLight = normalize(reflect(fromLightSource, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0);
    specularFactor = pow(specularFactor, specularPower);
    specularColor = specularC * specularFactor * material.reflectance * vec4(light.color, 1);

    float distance = length(lightDirection);
    float attenuationInv = light.attenuation.constant * light.attenuation.linear * distance +
            light.attenuation.exponent * distance * distance;
    return (diffuseColor + specularColor) / attenuationInv;
}

void main() {
    setupColors(material, outTexCoord);
    vec4 diffuseSpecularComp = calculatePointLight(pointLight, mvVertexPos, mvVertexNormal);
    fragColor = ambientC * vec4(ambientLight, 1) + diffuseSpecularComp;
}
