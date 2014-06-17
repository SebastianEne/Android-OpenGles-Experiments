package com.example.gl_test;

import com.learnopengles.android.common.RawResourceReader;
import com.learnopengles.android.common.ShaderHelper;
import com.learnopengles.android.common.TextureHelper;
import com.learnopengles.android.common.Vec4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

@SuppressWarnings("unused")
class Particle {
	
	Vec4f posParticle = new Vec4f();
	Vec4f velocityParticle = new Vec4f();
	Vec4f accParticle = new Vec4f();
	
	public float[] mParticleModelMatrix = new float[16];	
	float[] limit = new float[4];
	
	public Particle() {
		
		Matrix.setIdentityM(mParticleModelMatrix, 0);
		
		posParticle.x = 0;
		posParticle.y = 0;
		posParticle.z = 0;
		
		velocityParticle.x = 0;
		velocityParticle.y = 0;
		velocityParticle.z = 0;		
		
		accParticle.x = 0;
		accParticle.y = 0;
		accParticle.z = 0;
	}
	
	public Particle(float []initial_position) {
		
		Matrix.setIdentityM(mParticleModelMatrix, 0);
		
		posParticle.x = initial_position[0];
		posParticle.y = initial_position[1];
		posParticle.z = initial_position[2];
		
		velocityParticle.x = 0;
		velocityParticle.y = 0;
		velocityParticle.z = 0;	
		
		limit[0] = 1.5f;
		limit[1] = -1.5f;
		limit[2] = 2.4f;
		limit[3] = -2.0f;
	}
	
	public void updatePos() {
				
		posParticle.x += velocityParticle.x; 
		posParticle.y += velocityParticle.y; 
		posParticle.z += velocityParticle.z; 
	
		velocityParticle.x += accParticle.x;
		velocityParticle.y += accParticle.y;
		velocityParticle.z += accParticle.z;
		
		colisionCheck();
				
	}
	
	private void colisionCheck() {
		
		if(posParticle.x > limit[0] ) {
			posParticle.x -= 0.1f;
			velocityParticle.x = 0.0f;
			accParticle.x *= -1.0f;
		}
		if(posParticle.y < limit[3] ) {
			posParticle.y += 0.1f;
			velocityParticle.y = 0.0f;
			accParticle.y *= -1.0f;
		}	
		if( posParticle.x < limit[1]) {
			posParticle.x += 0.1f;
			velocityParticle.x = 0.0f;
			accParticle.x *= -1.0f;
		}
		if( posParticle.y > limit[2]) {
			posParticle.y -= 0.1f;
			velocityParticle.y = 0.0f;
			accParticle.y *= -1.0f;
		}		
	}
	
	public void setAcceleration(float []particleAcc) {
		accParticle.x = particleAcc[0];
		accParticle.y = particleAcc[1];
		accParticle.z = particleAcc[2];
	}

	public void setLimit(float right, float left,
						 float up,	  float down) {
		
		limit[0] = right;	limit[1] = left;
		limit[2] = up;		limit[3] = down;
	}
}


public class ParticleSystem {
	
	private Context app_context; 
	
	private int particles_nr;
	Particle []particleSystem;	
	private int shadersHandle;
	private int mTextureDataHandle;
	private FloatBuffer mPlaneTextCoords;
	private int mTextureUniformHandle;
	int mTextureCoordinateHandle;
	int pointMVPMatrixHandle;
	int pointPositionHandle;
	
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private final float[] emitterPos = new float[] {0.0f, 0.0f, -3.1f, 0.0f};
	
	public ParticleSystem(int nr_particles, Context gles_context) {
		particles_nr = nr_particles;
		app_context  = gles_context;

		setupParticleSystem();
		setupShaders();
		setupMatrices();	
		setupTextures();
	}
	
	
	private void setupShaders() {
		
		String vertexShader   = RawResourceReader.readTextFileFromRawResource(app_context, R.raw.point_vertex_shader);
		String fragmentShader = RawResourceReader.readTextFileFromRawResource(app_context, R.raw.point_fragment_shader);
		int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader); 
		
		shadersHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
        		new String[] {"a_Position", "a_TexCoordinate"});
		
	}
	
	
	private void setupMatrices() {		
		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) 480/800;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;
		
		mProjectionMatrix = new float[16];
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
		
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = -0.5f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;
		
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        
		final float [] planeTextureCoord = 
		{
				// Front face
				0.0f, 0.0f, 				
				0.0f, 1.0f,
				1.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.0f
		};
		
		
		mPlaneTextCoords = ByteBuffer.allocateDirect(planeTextureCoord.length * 4).
				order(ByteOrder.nativeOrder()).asFloatBuffer();
		mPlaneTextCoords.put(planeTextureCoord).position(0);
        
	}
	
	
	private void setupParticleSystem() {
		
		particleSystem = new Particle[particles_nr];
		Random r = new Random();
		for(int i = 0; i < particles_nr; i++) {
			particleSystem[i] = new Particle(emitterPos);
			
			float []random = new float[3];	
			random[0] = r.nextFloat()/1000;
			random[1] = r.nextFloat()/1000;
			random[2] = 0;
			
			particleSystem[i].setAcceleration(random);
		}
	}
	
	
	private void renderParticle(Particle p) {
        
		// Send texture coords to shader:
		mPlaneTextCoords.position(0);
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT,
				false, 0, mPlaneTextCoords);
		GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
		
		// Pass in the position.
		GLES20.glVertexAttrib3f(pointPositionHandle, p.posParticle.x, p.posParticle.y, p.posParticle.z);
		GLES20.glDisableVertexAttribArray(pointPositionHandle);
		                 		
		// Pass in the transformation matrix.
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, p.mParticleModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		
		// Draw the point.
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);	
	
	}
	
	
	public void setEmiterPos(float x, float y, float z) {
		
		emitterPos[0] = x;
		emitterPos[1] = y;
		emitterPos[2] = z;
	}
	
	
	public void setupTextures() {
        mTextureDataHandle = TextureHelper.loadTexture(app_context, R.drawable.light); 
	}
	
	public void drawParticle() {
		
		GLES20.glUseProgram(shadersHandle);

		pointMVPMatrixHandle = GLES20.glGetUniformLocation(shadersHandle, "u_MVPMatrix");
        pointPositionHandle = GLES20.glGetAttribLocation(shadersHandle, "a_Position");        
		mTextureCoordinateHandle= GLES20.glGetAttribLocation(shadersHandle, "a_TexCoordinate");
		mTextureUniformHandle = GLES20.glGetUniformLocation(shadersHandle, "u_Texture");
		
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        
        
		for(int i = 0; i < particles_nr; i++) {
			
	        particleSystem[i].updatePos();
			renderParticle(particleSystem[i]);
		}
	

	}
	

	

}
