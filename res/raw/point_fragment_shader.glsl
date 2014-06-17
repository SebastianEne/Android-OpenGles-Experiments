precision mediump float;

uniform sampler2D u_Texture;    // The input texture.
varying vec2 v_TexCoordinate;   // Interpolated texture coordinate per fragment 	
				          
void main()                    
{   
	vec4 col = texture2D(u_Texture, gl_PointCoord);
	if(col.r < 0.1)
		discard;                  
	gl_FragColor = col;             
}                              