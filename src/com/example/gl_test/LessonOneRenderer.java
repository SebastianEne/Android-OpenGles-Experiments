package com.example.gl_test;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.learnopengles.android.common.RawResourceReader;
import com.learnopengles.android.common.ShaderHelper;
import com.learnopengles.android.common.TextureHelper;

import android.content.Context;
import android.opengl.*;


public class LessonOneRenderer implements GLSurfaceView.Renderer{
	// This are program handles used by methods:
	private final Context mActivityContext;
	private int mProgramHandle;
	private int mTextureDataHandle;
	
	// Matrix transformation for objects rendered:
	private float[] mViewMatrix = new float[16];
	private float[] mModelMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];
	private float[] mLightModelMatrix = new float[16];
	private float[] mLightPositionInEyeSpace = new float[4];

	
	
	// Handles to the GPU buffers:
	private int mMVPMatrixHandle;
	private int mMVMatrixHandle;
	private int mLightPosHandle;
	private int mTextureUniformHandle;
	private int mPositionHandle;
	private int mColorHandle;
	private int mNormalHandle;
	private int mTextureCoordinateHandle;
	
	// Here we define model data in a local buffer:
	private final FloatBuffer mPlanePosition;
	private final FloatBuffer mPlaneColors;
	private final FloatBuffer mPlaneNormals;
	private final FloatBuffer mPlaneTextCoords;
	private int sizeofFloat = 4;
	
	
	// Size of data in elements:
	private final int mPlaneDataSize = 3;
	private final int mColorDataSize = 4;
	private final int mNormalDataSize = 3;
	private final int mTextCoorsDataSz= 2;
	
	
	public LessonOneRenderer(final Context activityContext) {
		mActivityContext = activityContext;
		
		final float[] planePostionData = 
		{			
				// Front face
				-1.0f, 1.0f, 1.0f,				
				-1.0f, -1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 
				-1.0f, -1.0f, 1.0f, 				
				1.0f, -1.0f, 1.0f,
				1.0f, 1.0f, 1.0f
		};
		
		
		final float[] planeColorData = 
		{
				// Front face (red)
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f
		};
		
		
		final float[] planeNormalData =
		{
				// Front face
				0.0f, 0.0f, 1.0f,				
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,				
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f
		};
		
		
		final float[] planeTextureCoord = 
		{
				// Front face
				0.0f, 0.0f, 				
				0.0f, 1.0f,
				1.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.0f
		};
		
		// Create Buffers in local memory and populate with data
		mPlanePosition = ByteBuffer.allocateDirect(planePostionData.length * sizeofFloat).
				order(ByteOrder.nativeOrder()).asFloatBuffer();
		mPlanePosition.put(planePostionData).position(0);
		
		
		mPlaneColors   = ByteBuffer.allocateDirect(planeColorData.length * sizeofFloat).
				order(ByteOrder.nativeOrder()).asFloatBuffer();
		mPlaneColors.put(planeColorData).position(0);
		
		
		mPlaneNormals  = ByteBuffer.allocateDirect(planeNormalData.length * sizeofFloat).
				order(ByteOrder.nativeOrder()).asFloatBuffer();
		mPlaneNormals.put(planeNormalData).position(0);
		
		
		mPlaneTextCoords = ByteBuffer.allocateDirect(planeTextureCoord.length * sizeofFloat).
				order(ByteOrder.nativeOrder()).asFloatBuffer();
		mPlaneTextCoords.put(planeTextureCoord).position(0);
	}
	

	@Override
	public void onDrawFrame(GL10 arg0) {
        
		// Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
               
        GLES20.glUseProgram(mProgramHandle);
        
        
        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix"); 
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal"); 
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
        
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        GLES20.glUniform1i(mTextureUniformHandle, 0);
        
        
        Matrix.setIdentityM(mLightModelMatrix, 0);
        //Matrix.multiplyMM(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);Log.i("!!!!!!!!!!!!!!!attemp to draw", "ok");
       // Matrix.multiplyMM(mLightPositionInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
        
        //Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        //Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);   
        
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -3.1f);
        drawBackground();
	}

	
	void drawBackground() {
		
		mPlanePosition.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, mPlaneDataSize, GLES20.GL_FLOAT,
				false, 0, mPlanePosition);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		
		
		mPlaneColors.position(0);
		GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT,
				false, 0, mPlaneColors);
		GLES20.glEnableVertexAttribArray(mColorHandle);
		
		
		mPlaneNormals.position(0);
		GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT,
				false, 0, mPlaneNormals);
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		
		
		mPlaneTextCoords.position(0);
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextCoorsDataSz, GLES20.GL_FLOAT,
				false, 0, mPlaneTextCoords);
		GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
		
		
		// Now we compute MVP and then we pass the matrix to the shader:
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);
		
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		
		// Send the light position in eye space:
		GLES20.glUniform3f(mLightPosHandle, mLightPositionInEyeSpace[0], mLightPositionInEyeSpace[1], mLightPositionInEyeSpace[2]);
		
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 9);
	}
	
	
	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		final float ratio = (float) width / height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;
		
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);		
	}

	
	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// Position the eye in front of the origin.
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
        
        
        // Get shaders code:
		final String vertexShader = getVertexShader();   		
 		final String fragmentShader = getFragmentShader();
		
 		
 		// Compile shader:
 		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);		
		final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);					
		mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
				new String[] {"a_Position",  "a_Color", "a_Normal", "a_TexCoordinate"});
				
        // Load the texture
        mTextureDataHandle = TextureHelper.loadTexture(mActivityContext, R.drawable.twinkle_star); 
	}
	
	
	protected String getVertexShader()
	{
		return RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_vertex_shader);
	}
	
	
	protected String getFragmentShader()
	{
		return RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_fragment_shader);
	}

}
