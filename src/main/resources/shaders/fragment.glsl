#version 330 core

in vec2 fragTexCoord;  // Incoming texture coordinates from the vertex shader

uniform sampler2D textureSampler;  // Texture sampler

out vec4 fragColor;  // Final color output

void main() {
    fragColor = texture(textureSampler, fragTexCoord);  // Fetch the texture color
}