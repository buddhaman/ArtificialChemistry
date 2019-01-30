package com.buddha.simulation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.buddha.physics.Particle;

public class Container {
	
	public static final int SQUARE_SHAPE = 0;
	public static final int CIRCLE_SHAPE = 1;
	
	public int shape = 0;
	public Array<Particle> particles = new Array<Particle>();
	public Array<Array<Particle>> particleTypes = new Array<Array<Particle>>();
	public Vector2 pos;
	public float width;
	public float height;
	public float radius;
	
	public Container(float x, float y, float width, float height, int numTypes) {
		this.pos = new Vector2(x, y);
		this.width = width;
		this.height = height;
		for (int i = 0; i < numTypes; i++) {
			particleTypes.add(new Array<Particle>());
		}
	}
	
	public void addParticle(float x, float y, int type) {
		Particle p = new Particle(x, y, type);
		particleTypes.get(type).add(p);
		particles.add(p);
	}
	
	public void removeParticle(Particle p) {
		particleTypes.get(p.type).removeValue(p, true);
		particles.removeValue(p, true);
	}
	
	public void changeType(Particle p, int to) {
		particleTypes.get(p.type).removeValue(p, true);
		particleTypes.get(to).add(p);
		p.type = to;
	}
	
	public void addParticle(Particle p) {
		particleTypes.get(p.type).add(p);
		particles.add(p);
	}
	
	public boolean contains(float x, float y) {
		if(x < this.pos.x)
			return false;
		if(x > this.pos.x+this.width) 
			return false;
		if(y < this.pos.y) 
			return false;
		if(y > this.pos.y+this.height)
			return false;
		return true;
	}
}
