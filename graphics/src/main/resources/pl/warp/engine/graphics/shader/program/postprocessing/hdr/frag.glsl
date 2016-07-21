#version 330
precision highp float;

uniform sampler2D sceneTex;
uniform sampler2D bloomTex;
uniform float bloomLevel = 1.0;
uniform float exposure = 1.0;

in vec2 vTexCoord;

layout(location = 0) out vec4 fragColor;

void main(void) {
    vec4 bloom = texture(bloomTex, vTexCoord) * bloomLevel;
    vec4 scene = texture(sceneTex, vTexCoord);
    vec4 color = bloom + scene;
    fragColor = 1 - exp2(-color * exposure);
}