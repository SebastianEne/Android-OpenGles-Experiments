package com.example.gl_test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.Context;

public class Plane {

	// This are program handles used by methods:
	private final Context mActivityContext;
	private int mProgramHandle;
	private int mTextureDataHandle;
	
	// Matrix transformation for objects rendered:
	private float[] mViewMatrix = new float[16];
	private float[] mModelMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];	
	
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
	
	public Plane(final Context activityContext) {
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
	
	
	
}
