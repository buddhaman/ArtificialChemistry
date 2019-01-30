package com.buddha.simulation;

import com.badlogic.gdx.math.Vector2;

public class ChemistryBook {
	
	public float width;
	public float height;
	public Vector2 pos;
	public int[] types;
	public Reaction[][] reactions;
	public int numTypes;
	public Vector2 vel;
	public float friction = 0.92f;
	
	public static final int X_POS = -100;
	public static final int Y_POS = -100;
	public static final int WIDTH = 98;
	public static final int HEIGHT = 98;
	
	public ChemistryBook() {
		this(X_POS, Y_POS, WIDTH, HEIGHT);
	}
	
	public ChemistryBook(float x, float y, float width, float height) {
		pos = new Vector2(x, y+height);
		this.width = width;
		this.height = height;
		vel = new Vector2(0,-(height+2)*(1-friction));
	}
	
	public void displayTypes(int[] types, Reaction[][] reactions, int numTypes) {
		this.types = types;
		this.reactions = reactions;
		this.numTypes = numTypes;
	}
	
	public void update() {
		pos.add(vel);
		vel.scl(friction);
	}
}
