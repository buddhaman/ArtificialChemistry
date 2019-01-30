package com.buddha.simulation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class WorldButton {
	
	public Vector2 pos;
	public float width;
	public float height;
	
	public static final int IDLE = 0;
	public static final int HOVER = 1;
	public static final int PRESSED = 2;
	public int state;
	
	public static final int SCAN = 0;
	public static final int DRAG_HORIZONTAL = 1;
	public static final int DRAG_VERTICAL = 2;
	public int function;
	
	public Array<ClickListener> listeners = new Array<ClickListener>();
	
	public WorldButton(float x, float y, float width, float height) {
		this.pos = new Vector2(x, y);
		this.width = width;
		this.height = height;
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
	
	public void clicked() {
		for(ClickListener listener : listeners) {
			listener.click();
		} 
	}
	
	public void addListener(ClickListener listener) {
		listeners.add(listener);
	}
	
	public interface ClickListener {
		public void click();
	}
}
