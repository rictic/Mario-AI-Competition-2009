package com.reddit.programming.mario;

public class SpriteState
{
	static final float DAMPING_X = 0.89f;
	static final float DAMPING_Y = 0.85f;
	public int facing = 1;
	public boolean onGround = false; // standing on ground
	public boolean dead = false;
	public float x,y,xa = 0, ya = 0;
}

