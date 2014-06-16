uniform mat4 u_MVPMatrix;      		
attribute vec4 a_Position;     		
attribute float p_size;

void main()                    
{                              
	gl_Position = u_MVPMatrix * a_Position;   
    gl_PointSize = 3.0;         
}                              