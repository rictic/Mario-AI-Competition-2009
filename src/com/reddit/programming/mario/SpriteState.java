package com.reddit.programming.mario;

public class SpriteState
{
	static final float DAMPING_X = 0.89f;
	static final float DAMPING_Y = 0.85f;
	public int facing = 1;
	public int type = -1;
	public boolean onGround = false; // standing on ground
	public boolean dead = false;
	public float x,y,xa = 0, ya = 0;
	public float height() { return -1; }

	public SpriteState clone() { return null; }

	// returns false iff we should remove the enemy from the list
	public boolean move(WorldState ws) { return false; }

    public WorldState collideCheck(WorldState ws, MarioState ms) { return ws; }

	// you may destructively update ws here as it's fresh for the purpose of this stomp
	public SpriteState stomp(WorldState ws) { return this; }

	static public SpriteState newEnemy(float x, float y, int type) {
		switch(type) {
			case KIND_BULLET_BILL:
				return new BulletBillState(x,y);
			case KIND_FLOWER_ENEMY:
				return new FlowerEnemyState(x,y);
			case KIND_SHELL:
				return null;
			case KIND_MUSHROOM:
				return null;
		}
		return new EnemyState(x,y,type);
	}

	// enemy kinds
	public static final int KIND_GOOMBA = 2;
	public static final int KIND_GOOMBA_WINGED = 3;
	public static final int KIND_RED_KOOPA = 4;
	public static final int KIND_RED_KOOPA_WINGED = 5;
	public static final int KIND_GREEN_KOOPA = 6;
	public static final int KIND_GREEN_KOOPA_WINGED = 7;
	public static final int KIND_BULLET_BILL = 8;
	public static final int KIND_SPIKY = 9;
	public static final int KIND_SPIKY_WINGED = 10;
	public static final int KIND_FLOWER_ENEMY = 12;

	// not actually enemies
	public static final int KIND_SHELL = 13;
	public static final int KIND_MUSHROOM = 14;

	// and we won't see any of these
	public static final int KIND_FIRE_FLOWER = 15;    
	public static final int KIND_PARTICLE = 21;
	public static final int KIND_SPARCLE = 22;
	public static final int KIND_COIN_ANIM = 20;
	public static final int KIND_FIREBALL = 25;

}

