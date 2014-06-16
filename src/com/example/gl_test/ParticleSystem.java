package com.example.gl_test;

import com.learnopengles.android.common.RawResourceReader;
import com.learnopengles.android.common.ShaderHelper;

import java.util.Random;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;


public class ParticleSystem {

	private int particles_nr;
	private Context app_context; 
	private int shadersHandle;
	
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private float[] mLightModelMatrix = new float[16];
	private final float[] mLightPosInModelSpace = new float[] {-1.5f, -2.0f, 0.0f, 1.0f};
	
	public ParticleSystem(int nr_particles, Context gles_context) {
		particles_nr = nr_particles;
		app_context  = gles_context;
		
		setupShaders();
		setupMatrices();
	}
	
	
	private void setupShaders() {
		
		String vertexShader   = RawResourceReader.readTextFileFromRawResource(app_context, R.raw.point_vertex_shader);
		String fragmentShader = RawResourceReader.readTextFileFromRawResource(app_context, R.raw.point_fragment_shader);
		int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader); 
		
		shadersHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
        		new String[] {"a_Position"});
		
	}
	
	
	public void drawParticle() {
		
		GLES20.glUseProgram(shadersHandle);
        Random r = new Random();
        
		for(int i = 0; i < particles_nr; i++) {
			Matrix.setIdentityM(mLightModelMatrix, 0);
	        Matrix.translateM(mLightModelMatrix, 0, r.nextFloat() *4 , r.nextFloat() *4, -3.1f);
	        
			renderParticle();
		}
	
	}
	
	void renderParticle() {
		final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(shadersHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(shadersHandle, "a_Position");
        
		// Pass in the position.
		GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);
		
		// Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);  
        		
		// Pass in the transformation matrix.
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		
		// Draw the point.
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);	
	}
	
	
	void setupMatrices() {		
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
	}
}
