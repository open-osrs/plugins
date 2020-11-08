#version 150

in vec4 f_color;
in vec3 f_normal;
in vec3 f_position;
in float f_fog;
uniform vec3 fog_color;
uniform float gamma;

uniform vec3 light_position;
const vec3 light_color = vec3(1, 1, 1);
const float ambient_light_strength = 0.4;
const float diffuse_light_strength = 0.6;

void main(void) {
    float light_strength = ambient_light_strength + diffuse_light_strength * abs(dot(normalize(f_normal), normalize(light_position - f_position)));
    vec3 light = light_strength * light_color;
    vec4 color = f_color;

    color = vec4(pow(color.r, gamma), pow(color.g, gamma), pow(color.b, gamma), color.a);
    color = vec4(light, 1) * color;
    color = color * (1 - f_fog) + vec4(fog_color, 1) * f_fog;
    gl_FragColor = color;
}
