package com.buddha.physics;

import com.badlogic.gdx.math.Vector2;

public class Particle {

	public int type;
	public Vector2 pos;
	public Vector2 oldPos;
	
	public Particle(float x, float y, int type) {
		pos = new Vector2(x, y);
		oldPos = new Vector2(x, y);
		this.type = type;
	}
	
	public void addImpulse(float x, float y) {
		oldPos.sub(x, y);
	}

	public float getXV() {
		return pos.x-oldPos.x;
	}
	
	public float getYV() {
		return pos.y-oldPos.y;
	}
}
