package com.learnopengles.android.common;

public class Vec3f {

	public static float X;
	public static float Y;
	public static float Z;
	
	static double magnitude;
	
	
	public Vec3f(Vec3f v) {
		X = v.X;
		Y = v.Y;
		Z = v.Z;
	}
	
	
	public Vec3f(float x, float y, float z) {
		X = x;
		Y = y;
		Z = z;
	}
	
	
	public static void add(Vec3f v1, Vec3f v2) {
		
		X = v1.X + v2.X;
		Y = v1.Y + v2.Y;
		Z = v1.Z + v2.Z;
	}
	
	
	public static void substract(Vec3f v1, Vec3f v2) {
		
		X = v1.X - v2.X;
		Y = v1.Y - v2.Y;
		Z = v1.Z - v2.Z;
	}
	
	
	public double vecMagnitude() {
	
		magnitude = Math.sqrt(X * X + Y * Y + Z * Z);
		return magnitude;
	}

	
	public static double vecMagnitude(Vec3f v1) {
		
		magnitude = Math.sqrt(v1.X * v1.X + v1.Y * v1.Y + v1.Z * v1.Z);
		return magnitude;
	}

	
}
