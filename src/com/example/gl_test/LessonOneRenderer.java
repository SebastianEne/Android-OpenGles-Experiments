package com.example.gl_test;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.*;
import android.os.SystemClock;


public class LessonOneRenderer extends MainActivity implements GLSurfaceView.Renderer {
	
	
	private Plane  plane = null;
	private ParticleSystem particles = null;
	private Context mactivityContext = null;
	
	public LessonOneRenderer(Context activityContext) {
		mactivityContext = activityContext;
	}
	

	@Override
	public void onDrawFrame(GL10 arg0) {
        
		// Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		@SuppressWarnings("unused")
		long time = SystemClock.uptimeMillis() % 10000L;        
        
        plane.draw();
        particles.drawParticle();
	}

		
	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
	}

	
	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
	
        // Set the background frame color       
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        
        plane = new Plane(mactivityContext);
        particles = new ParticleSystem(10, mactivityContext);				       
	}
	
}
