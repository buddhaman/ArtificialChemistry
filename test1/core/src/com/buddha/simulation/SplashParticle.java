package com.buddha.simulation;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class SplashParticle {
	public Vector2 pos;
	public Vector2 vel;
	public float time;
	public float lifeTime;
	public boolean remove;
	public float radius = 1;
	public int type;
	
	public SplashParticle(float x, float y, float xv, float yv, float dev, float time, int type) {
		pos = new Vector2(x, y);
		vel = new Vector2(xv+MathUtils.randomTriangular()*dev, yv+MathUtils.randomTriangular()*dev);
		this.lifeTime = time;
		this.type = type;
	}
	
	public void update() {
		time+=1f/60;
		if(time > lifeTime) {
			remove = true;
		}
		pos.add(vel);
		vel.scl(0.98f);
	}
	
	public float getRadius() {
		return radius*(1-time/lifeTime);
	}
}
