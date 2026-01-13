#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {
    float animation = GameTime * 2000.0;
    float animation1 = sin(animation) + 1;
    vec4 defaultColor = texture(Sampler0, texCoord0) * vertexColor;
    if (defaultColor.a < 0.1) {
        discard;
    }
    vec4 color = vec4(0, animation1 * 0.15 + 0.85, animation1 * 0.15 + 0.85, defaultColor.a);
    color *= ColorModulator;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}