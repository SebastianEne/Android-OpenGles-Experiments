package com.example.gl_test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.learnopengles.android.common.RawResourceReader;
import com.learnopengles.android.common.ShaderHelper;
import com.learnopengles.android.common.TextureHelper;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class Plane {

	
	// This are program handles used by methods:
	private final Context mActivityContext;
	private int mProgramHandle;
	private int mTextureDataHandle;
	
	// Matrix transformation for objects rendered:
	private float[] mModelMatrix = new float[16];
	private float[] mProjectionMatrix = null;
	private float[] mMVPMatrix = new float[16];	
	private float[] mLightPosInEyeSpace = {0.0f, 0.0f, 1.0f};
	private float[] mViewMatrix = new float[16];
		
	// Here we define model data buffer:
	private FloatBuffer mPlanePosition;
	private FloatBuffer mPlaneColors;
	private FloatBuffer mPlaneNormals;
	private FloatBuffer mPlaneTextCoords;
		
	// Size of data in elements:
	private final int mPlaneDataSize = 3;
	private final int mColorDataSize = 4;
	private final int mNormalDataSize = 3;
	private final int mTextCoorsDataSz= 2;
	private int sizeofFloat = 4;

	// Handles to the GPU buffers:
	private int mMVPMatrixHandle;
	private int mMVMatrixHandle;
	private int mLightPosHandle;
	private int mTextureUniformHandle;
	private int mPositionHandle;
	private int mColorHandle;
	private int mNormalHandle;
	private int mTextureCoordinateHandle;
	
	// Shader handles:
	int vertexShaderHandle;
	int fragmentShaderHandle;
	public int mShaderHandle;
	
	public Plane(final Context activityContext) {
				
		mActivityContext = activityContext;
		
		setupBuffers();	
		setupMatrices();
		setupShaders();
		setupTextures();
	}
	
	
	private void setupBuffers() {
		
		final float [] planePostionData = 
		{			
				// Front face
				-1.0f, 1.0f, 1.0f,				
				-1.0f, -1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 
				-1.0f, -1.0f, 1.0f, 				
				1.0f, -1.0f, 1.0f,
				1.0f, 1.0f, 1.0f
		};
		
		
		final float [] planeColorData = 
		{
				// Front face white
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f
		};
		
		
		final float [] planeNormalData =
		{
				// Front face
				0.0f, 0.0f, 1.0f,				
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,				
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f
		};
		
		
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

	
	public void setupShaders() {
		final String vertexShader   = getVertexShader();
		final String fragmentShader = getFragmentShader();
		vertexShaderHandle   = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
	
		mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
				new String[] {"a_Position",  "a_Color", "a_Normal", "a_TexCoordinate"});			
	}
	
	
	public void setupTextures() {
        mTextureDataHandle = TextureHelper.loadTexture(mActivityContext, R.drawable.twinkle_star); 	
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
	
	
	public void draw() {
		
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
     
        
        mLightPosInEyeSpace[0] = 0.0f;
        mLightPosInEyeSpace[1] = 0.0f;
        mLightPosInEyeSpace[2] = 1.0f; 
        
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -3.1f);
        Matrix.scaleM(mModelMatrix, 0, 1.0f, 1.5f, 1.0f);  
         
        
        passDatatoShader();
	
	}
	
	
	private void passDatatoShader() {
		
		// Send vertex position to shader:
		mPlanePosition.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, mPlaneDataSize, GLES20.GL_FLOAT,
				false, 0, mPlanePosition);
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		
		
		// Send color to shader:
		mPlaneColors.position(0);
		GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT,
				false, 0, mPlaneColors);
		GLES20.glEnableVertexAttribArray(mColorHandle);
		
		
		// Send normal vector to shader:
		mPlaneNormals.position(0);
		GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT,
				false, 0, mPlaneNormals);
		GLES20.glEnableVertexAttribArray(mNormalHandle);
		
		
		// Send texture coords to shader:
		mPlaneTextCoords.position(0);
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextCoorsDataSz, GLES20.GL_FLOAT,
				false, 0, mPlaneTextCoords);
		GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
		
		
		
		// Now we compute MV and then we pass the matrix to the shader
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);
		
		
		// Now compute MVP matrix and send to shader:
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		
		
		// Send the light position in eye space:
		GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);
		
		
		// Draw:
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 9);	
	
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
