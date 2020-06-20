#version 330

uniform vec2      resolution;
uniform float     time;

uniform sampler2D tex0;
uniform sampler2D tex1;

out vec3 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution;
    fragColor = texture(tex0, uv).rgb;
    // alien transmission glitch
    fragColor = texture(tex0, uv - vec2(fragColor.x, fragColor.y) * vec2(sin(time * .1), cos(time * .2)) * .15).rgb;
    fragColor = texture(tex0, uv - vec2(sin(fragColor.x * time), cos(fragColor.y * time)) * (sin(time) + 1) * .05).rgb;
}
