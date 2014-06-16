package com.example.gl_test;

import android.opengl.Matrix;


public class Camera {

	Vec3f eye;
	Vec3f look;
	Vec3f or;	
	public float[] mView;
	
	public Camera(Vec3f eye_,
				  Vec3f look_,
				  Vec3f or_) {
		
		eye  = new Vec3f(eye_);
		look = new Vec3f(look_);
		or   = new Vec3f(or_);
		
		mView =  new float[16];
		Matrix.setLookAtM(mView, 0, eye.X,  eye.Y,  eye.Z,
	  								look.X, look.Y, look.Z,
	  								or.X,   or.Y,   or.Z);
	}
	

	
}
