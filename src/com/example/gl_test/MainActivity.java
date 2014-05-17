package com.example.gl_test;

import android.opengl.GLSurfaceView.Renderer;


public class MainActivity extends OpenGLES2WallpaperService{

	@Override
	Renderer getNewRenderer() {
		return new LessonOneRenderer();
	}
}
