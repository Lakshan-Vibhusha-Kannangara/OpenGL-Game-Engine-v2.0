#version 330 core

layout(location = 0) in vec3 position;  // Vertex position
layout(location = 1) in vec2 texCoord;  // Texture coordinates

uniform mat4 viewMatrix;         // View matrix
uniform mat4 projectionMatrix;   // Projection matrix

out vec2 fragTexCoord;           // Output texture coordinates to the fragment shader

void main() {
    fragTexCoord = texCoord;    // Pass texture coordinates to fragment shader
    gl_Position = projectionMatrix * viewMatrix * vec4(position, 1.0);  // Apply transformations
}