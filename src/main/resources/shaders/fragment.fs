#version 330

in  vec2 outTexCoord;
in vec4 outColor;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec3 colour;
uniform int useColour;

void main()
{
    if ( useColour == 1 )
    {
//         fragColor = vec4(colour, 1);
        fragColor = outColor * vec4(colour, 1);
    }
    else
    {
        fragColor = texture(texture_sampler, outTexCoord);
    }
}
