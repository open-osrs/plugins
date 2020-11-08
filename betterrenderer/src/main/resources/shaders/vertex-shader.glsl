#version 150

uniform mat4 transform;
uniform mat4 projection;
uniform float view_distance;
uniform vec3 camera_position;
in vec3 position;
in vec3 normal;
in int color;
in float priority;

out vec4 f_color;
out vec3 f_normal;
out vec3 f_position;
out float f_fog;

void main(void) {
    vec4 pos = transform * vec4(position, 1);

    float z_offset = priority / 100;
    if (pos.z + z_offset < 0) {
        float old_z = pos.z;
        pos += vec4(0, 0, z_offset, 0);
        pos.xy /= old_z/pos.z;
    }

    pos = projection * pos;

    gl_Position = pos;

    f_position = position;
    f_normal = normal;
    f_color = vec4((color >> 16) & 0xff, (color >> 8) & 0xff, (color >> 0) & 0xff, (color >> 24) & 0xff) / 0xff;

    float fog_start = 0.9 * view_distance;
    float fog_end = view_distance;
    f_fog = (length((position - camera_position).xy) - fog_start) / (fog_end - fog_start);

    if (f_fog > 1) {
        f_fog = 1;
    }

    if (f_fog < 0) {
        f_fog = 0;
    }
}
