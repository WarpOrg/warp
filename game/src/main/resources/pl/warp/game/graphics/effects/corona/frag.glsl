#version 400
precision highp float;

uniform sampler1D colors;
uniform vec3 brightness;
uniform float temperature;

uniform float time = 1;

smooth in vec2 coord;

layout(location = 0) out vec4 fragColor;

#include "util/noise4d"

void main() {
    vec3 position = vec3(coord, 0.1);

    float t = (time * 0.05) - length(position);

    // Offset normal with noise
    float frequency = 1.5;
    float ox = snoise(vec4(position, t) * frequency);
    float oy = snoise(vec4((position + 2000.0), t) * frequency);
    float oz = snoise(vec4((position + 4000.0), t) * frequency);

    // Store offsetVec since we want to use it twice.
    vec3 offsetVec = vec3(ox, oy, oz) * 0.1;

    // Get the distance vector from the center
    vec3 nDistVec = normalize(position + offsetVec);

    // Get noise with normalized position to offset the original position

    vec3 offsetPos = position + noise(vec4(nDistVec, t), 5, 2.0, 0.7) * 0.1;

    float dist = length(offsetPos) * 4.0;
    float brightness = (1.0 / (dist * dist) - 0.1) * 1.2;

    fragColor.rgba = vec4(1, 1, 1, max(brightness, 0));
}
