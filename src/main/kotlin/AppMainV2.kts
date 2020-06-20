@file:Suppress("UNUSED_LAMBDA_EXPRESSION")

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.isolated
import org.openrndr.draw.shadeStyle
import org.openrndr.math.Vector4
import org.openrndr.text.writer
import java.lang.Math.sin
import kotlin.math.abs

{ program: LiveCodingCameraProgram ->
    program.apply {
        extend {
            shader.parameters["time"] = seconds
            realCamera.draw(drawer, blind = true)
            realCamera.colorBuffer?.let { cameraBuffer ->
                shader.apply(cameraBuffer, virtualCameraBuffer)
            }
            drawer.image(virtualCameraBuffer)
            drawer.isolated {
                shadeStyle = shadeStyle {
                    // taken from https://www.shadertoy.com/view/XsfGRn by Inigo Quilez
                    fragmentTransform = """
            vec2 p = (.5 - c_boundsPosition.xy) * 2;
            float dist = length(p);
    vec3 bcol = vec3(0);

    // animate
    float tt = mod(p_time, 1.5)/1.5;
    float ss = pow(tt,.2)*0.5 + 0.5;
    ss = 1.0 + ss*0.5*sin(tt*6.2831*3.0 + p.y*0.5)*exp(-tt*4.0);
    p *= vec2(0.5,1.5) + ss*vec2(0.5,-0.5);

    // shape
#if 0
    p *= 0.8;
    p.y = -0.1 - p.y*1.2 + abs(p.x)*(1.0-abs(p.x));
    float r = length(p);
	float d = 0.5;
#else
	p.y -= 0.25;
    float a = atan(p.x,p.y)/3.141593;
    float r = length(p);
    float h = abs(a);
    float d = (13.0*h - 22.0*h*h + 10.0*h*h*h)/(6.0-5.0*h);
#endif
    
	// color
	float s = 0.75 + 0.75*p.x;
	s *= 1.0-0.4*r;
	s = 0.3 + 0.7*s;
	s *= 0.5+0.5*pow( 1.0-clamp(r/d, 0.0, 1.0 ), 0.1 );
    float force = (sin(p_time) + 1) * .5;
    vec3 baseCol = vec3(force, 0, 1 - force);
	vec3 hcol = baseCol*s;
    vec3 col = mix( bcol, hcol, smoothstep( -0.01, 0.01, d-r) );            
            x_fill = vec4(col, smoothstep(.47, .48, s));
          """
                    parameter("time", seconds)
                }
                stroke = null
                for (i in 1..20) {
                    val shift = i * sin(seconds * .02)
                    rectangle(
                        width * .2 + sin(seconds * 1.2 + shift) * width * .2,
                        height * .2 + sin(seconds * 1.3 + shift) * height * .4,
                        height * .5 * abs(sin(shift)),
                        height * .5 * abs(sin(shift))
                    )
                }
            }
            drawer.fontMap = font
            drawer.isolated {
                fill = ColorRGBa.fromVector(
                    Vector4(
                        (sin(seconds) + 1.0) * .5,
                        (sin(seconds * 1.1) + 1.0) * .5,
                        (sin(seconds * 1.2) + 1.0) * .5,
                        1.0
                    )
                )
                writer {
                    code.lines().forEach { line ->
                        text(line)
                        newLine()
                    }
                }
            }
            drawer.isolated {
                writer {
                    move(-4.0, -4.0)
                    code.lines().forEach { line ->
                        text(line)
                        newLine()
                    }
                }
            }
        }
    }
}
