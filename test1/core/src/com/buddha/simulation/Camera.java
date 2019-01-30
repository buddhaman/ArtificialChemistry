package com.buddha.simulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class Camera {
	
	public OrthographicCamera cam;
	public float scale = 0.3f;
	public float screenWidth;
	public float screenHeight;
	public float x;
	public float y;
	
	public Camera() {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		cam = new OrthographicCamera();
		update();
	}
	
	public void update() {
		cam.setToOrtho(false, screenWidth*scale, screenHeight*scale);
		cam.translate(x-screenWidth*scale/2, y-screenHeight*scale/2);
		cam.update();
	}
	
	public void resize(float width, float height) {
		this.screenWidth = width;
		this.screenHeight = height;
	}
	
	public Vector2 screenToWorld(float x, float y) {
		return new Vector2((x-screenWidth/2)*scale+this.x, (screenHeight/2-y)*scale+this.y);
	}
}
