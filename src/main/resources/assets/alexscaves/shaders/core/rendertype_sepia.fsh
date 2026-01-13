#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    if (color.a < 0.1) {
        discard;
    }
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;

    float rr = .3;
    float rg = .7;
    float rb = .2;
    float ra = 0.15;

    float gr = .3;
    float gg = .6;
    float gb = .1;
    float ga = 0.15;

    float br = .2;
    float bg = .5;
    float bb = .1;
    float ba = 0.15;

    float red = (rr * color.r) + (rb * color.b) + (rg * color.g) + (ra * color.a);
    float green = (gr * color.r) + (gb * color.b) + (gg * color.g) + (ga * color.a);
    float blue = (br * color.r) + (bb * color.b) + (bg * color.g) + (ba * color.a);

    vec4 sepiaColor = vec4(red, green, blue, color.a);
    fragColor = linear_fog(sepiaColor, vertexDistance, FogStart, FogEnd, FogColor);
}
