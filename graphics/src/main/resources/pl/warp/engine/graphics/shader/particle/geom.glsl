#version 330

#extension GL_EXT_geometry_shader4 : enable

precision mediump float;

uniform mat4 projectionMatrix;
uniform mat4 cameraRotationMatrix;

layout (points) in;
layout (triangle_strip) out;
layout (max_vertices = 4) out;

in vData {
    mat2 rotation;
    int textureIndex;
} pointData[];

mat2 to2DRotation(mat4 rotation3D);

void main()
{
    vec4 pos = gl_in[0].gl_Position;
    mat2 particleRotation = pointData[0].rotation;
    float textureIndex = pointData[0].textureIndex;


     // Vertex 4
    gl_TexCoord[0].stp = vec3(1.0, 1.0, textureIndex);
    gl_Position = pos;
    gl_Position.xy += (particleRotation * vec2(1, 1));
    gl_Position = projectionMatrix * cameraRotationMatrix * gl_Position;
    EmitVertex();

    // Vertex 3
    gl_TexCoord[0].stp = vec3(-1.0, 1.0, textureIndex);
    gl_Position = pos;
    gl_Position.xy += (particleRotation * vec2(-1, 1));
    gl_Position = projectionMatrix * cameraRotationMatrix * gl_Position;
    EmitVertex();

    // Vertex 2
    gl_TexCoord[0].stp = vec3(1.0, -1.0, textureIndex);
    gl_Position = pos;
    gl_Position.xy += (particleRotation * vec2(1, -1));
    gl_Position = projectionMatrix * cameraRotationMatrix * gl_Position;
    EmitVertex();

    // Vertex 1
    gl_TexCoord[0].stp = vec3(-1.0, -1.0, textureIndex);
    gl_Position = pos;
    gl_Position.xy += (particleRotation * vec2(-1, -1));
    gl_Position = projectionMatrix * cameraRotationMatrix * gl_Position;
    EmitVertex();

    EndPrimitive();
}