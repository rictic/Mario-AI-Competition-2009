package com.reddit.programming.mario;

public final class MarioState extends SpriteState
{
	// FIXME: try to minimize the sizes of these fields as much as possible; we
	// allocate a huge number of MarioState objects.
	public int facing = 1, jumpTime = 0;
	public boolean big = true,  // mario is big
		   fire = true, // mario can throw fireballs
		   dead = false, // dead
		   onGround = false, // standing on ground
		   mayJump = true,  // yep
		   sliding = false; // sliding down the side of a wall
	public float xJumpSpeed = 0, yJumpSpeed = 0; // can we get rid of this somehow?

	// fields used by the search stuff
	public float g, cost; // heuristic costs
	public int root_action;
	public WorldState ws;

	// these two are only needed for showing the paths taken
	public int action;
	public MarioState pred; // predecessor

	public MarioState(float _x, float _y, float _xa, float _ya) {
		x = _x; y = _y; xa = _xa; ya = _ya;
	}

	public void print() {
		System.out.printf("g=%d a:%d x:(%f,%f) v:(%f,%f) %s%s%s cost=%f\n", (int)g, action,x,y,xa,ya,
				onGround ? "G":"g",
				mayJump ? "J":"j",
				sliding ? "S":"s", cost);
	}

	private static final int ACT_SPEED = 1;
	private static final int ACT_RIGHT = 2;
	private static final int ACT_JUMP = 4;
	private static final int ACT_LEFT = 8;

	public MarioState next(int action, WorldState ws) {
		// this is what passes for clone()
		MarioState n = new MarioState(x,y, xa,ya);
		n.facing = facing; n.jumpTime = jumpTime;
		n.big = big; n.fire = fire;
		n.dead = dead;
		n.onGround = onGround;
		n.mayJump = mayJump;
		n.sliding = sliding;
		n.xJumpSpeed = xJumpSpeed; n.yJumpSpeed = yJumpSpeed;
		n.root_action = root_action;
		n.action = action;
		n.ws = ws;
		n.pred = this;
		n.g = g + 1;

		n.move(action);

		return n;
	}

	private void move(int action) {
		// action bits:
		//  0: speed
		//  1: right
		//  2: jump
		//  3: left
		//  4: duck not implemented; waste of search time
		boolean ducking = false; // just... we aren't doing this
		float sideWaysSpeed = (action&ACT_SPEED) != 0 ? 1.2f : 0.6f;
		//System.out.println("move: sidewaysspeed = " + sideWaysSpeed);
		//System.out.println(String.format("move: xy=%5.1f,%5.1f", x, y));

		if (xa > 2) facing = 1;
		else if (xa < -2) facing = -1;

		// jumping logic
		// note: jumpTime ranges from 7 downto 0 during a standard upward jump
		// acceleration phase, after which the regular falling code takes over
		// jumpTime ranges from -6 upto 0 in the rare case of walljumps
		if ((action&ACT_JUMP) != 0 || (jumpTime < 0 && !onGround && !sliding)) {
			if (jumpTime < 0) { // post-walljump
				xa = xJumpSpeed;
				ya = -jumpTime * yJumpSpeed;
				jumpTime++;

			} else if (onGround && mayJump) { // initial jump phase
				xJumpSpeed = 0;
				yJumpSpeed = -1.9f;
				jumpTime = 7;
				ya = jumpTime * yJumpSpeed;
				onGround = false;
				sliding = false;

			} else if (sliding && mayJump) { // walljump?
				xJumpSpeed = -facing * 6.0f;
				yJumpSpeed = -2.0f;
				jumpTime = -6;
				xa = xJumpSpeed;
				ya = -jumpTime * yJumpSpeed;
				onGround = false;
				sliding = false;
				facing = -facing;

			} else if (jumpTime > 0) { // post-peak downward jump trajectory
				// apparently this acts different than falling?
				xa = xa + xJumpSpeed;
				ya = jumpTime * yJumpSpeed;
				jumpTime = jumpTime-1;
			}
		} else {
			jumpTime = 0;
		}

		// ducking code elided

		if ((action&ACT_LEFT) != 0 && !ducking) {
			if (facing == 1) sliding = false;
			xa -= sideWaysSpeed;
			if (jumpTime >= 0) facing = -1;
		}

		if ((action&ACT_RIGHT) != 0 && !ducking)
		{
			if (facing == -1) sliding = false;
			xa += sideWaysSpeed;
			if (jumpTime >= 0) facing = 1;
		}

		if (((action&ACT_LEFT) == 0 && (action&ACT_RIGHT) == 0) || ducking || ya < 0 || onGround) {
			sliding = false;
		}

		//    if ((action&ACT_SPEED) != 0 && canShoot && fire && world.fireballsOnScreen<2) {
		//      // in theory: add fireball to our state
		//    }
		//    canShoot = (action&ACT_SPEED) == 0;

		mayJump = (onGround || sliding) && (action&ACT_JUMP) == 0;

		if (Math.abs(xa) < 0.5f) { xa = 0; }

		// friction while sliding against a wall
		if (sliding)
			ya = ya * 0.5f;

		//System.out.println("move: (xa,ya)1 = " + xa + "," + ya);

		onGround = false;
		move(xa, 0);
		//System.out.println("move: (x,y,xa,ya)2 = " + x + "," + y + "," + xa + "," + ya);
		move(0, ya);
		//System.out.println("move: (x,y,xa,ya)3 = " + x + "," + y + "," + xa + "," + ya);

		// world.level.height hardcoded as 15
		if (y > 15*16 + 16) { dead = true; }

		if (x < 0) { x = 0; xa = 0; }

		//System.out.println("move: (x,y,xa,ya)4 = " + x + "," + y + "," + xa + "," + ya);

		// maximum speed is DAMPING_X/(1-DAMPING_X) * impulse_per_step
		// impulse_per_step = 0.6 if walking, 1.2 if running
		// max speed = 4.85 walking, 9.7 running
		xa *= DAMPING_X;
		ya *= DAMPING_Y;

		// falling?
		if (!onGround)
			ya += 3;

		//System.out.println("move: (xa,ya)5 = " + xa + "," + ya);
	}

	private boolean move(float xa, float ya)
	{
		while (xa > 8) {
			if (!move(8, 0)) return false;
			xa -= 8;
		}
		while (xa < -8) {
			if (!move(-8, 0)) return false;
			xa += 8;
		}
		while (ya > 8) {
			if (!move(0, 8)) return false;
			ya -= 8;
		}
		while (ya < -8) {
			if (!move(0, -8)) return false;
			ya += 8;
		}

		boolean collide = false;
		int width = 4;
		int height = big ? 24 : 12;
		if (ya > 0)
		{
			if (isBlocking(x + xa - width, y + ya, xa, 0)) collide = true;
			else if (isBlocking(x + xa + width, y + ya, xa, 0)) collide = true;
			else if (isBlocking(x + xa - width, y + ya + 1, xa, ya)) collide = true;
			else if (isBlocking(x + xa + width, y + ya + 1, xa, ya)) collide = true;
		}
		if (ya < 0)
		{
			if (isBlocking(x + xa, y + ya - height, xa, ya)) collide = true;
			else if (collide || isBlocking(x + xa - width, y + ya - height, xa, ya)) collide = true;
			else if (collide || isBlocking(x + xa + width, y + ya - height, xa, ya)) collide = true;
		}
		if (xa > 0)
		{
			sliding = true;
			if (isBlocking(x + xa + width, y + ya - height, xa, ya)) collide = true;
			else sliding = false;
			if (isBlocking(x + xa + width, y + ya - height / 2, xa, ya)) collide = true;
			else sliding = false;
			if (isBlocking(x + xa + width, y + ya, xa, ya)) collide = true;
			else sliding = false;
		}
		if (xa < 0)
		{
			sliding = true;
			if (isBlocking(x + xa - width, y + ya - height, xa, ya)) collide = true;
			else sliding = false;
			if (isBlocking(x + xa - width, y + ya - height / 2, xa, ya)) collide = true;
			else sliding = false;
			if (isBlocking(x + xa - width, y + ya, xa, ya)) collide = true;
			else sliding = false;
		}

		if (collide)
		{
			if (xa < 0) {
				x = (int) ((x - width) / 16) * 16 + width;
				this.xa = 0;
			} else if (xa > 0) {
				x = (int) ((x + width) / 16 + 1) * 16 - width - 1;
				this.xa = 0;
			}

			if (ya < 0) {
				y = (int) ((y - height) / 16) * 16 + height;
				jumpTime = 0;
				this.ya = 0;
			} else if (ya > 0) {
				y = (int) ((y - 1) / 16 + 1) * 16 - 1;
				onGround = true;
			}
			return false;
		}
		else
		{
			x += xa;
			y += ya;
			return true;
		}
	}

	private boolean isBlocking(float _x, float _y, float xa, float ya)
	{
		int x = (int) (_x / 16); // block's quantized pos
		int y = (int) (_y / 16);
		int Mx = (int) (this.x / 16); // mario's quantized pos
		int My = (int) (this.y / 16);
		if (x == Mx && y == My) return false;

		//System.out.println(String.format("move: hitcheck %f,%f -> %d,%d M@%d,%d", _x,_y,x,y,Mx,My));

		// move x,y world coordinates to the 22x22 reference frame surrounding mario
		x -= ws.MapX;
		y -= ws.MapY;
		//System.out.println(String.format("move: hitcheck maporigin=%d,%d xy=%d,%d", MapX,MapY,x,y));

		// if we run off the edge of our map fragment here we're... blocking, i guess?
		// no, because we start intersecting the top edge of the map.  awesome!
		if(x < 0 || x >= 22 || y < 0 || y >= 22)
			return false;

		byte block = ws.map[y][x];
		if(block == 1) return false; // that's mario's previous position; ignore
		if(block == 34) { // coin
			// yay for crazy side effects: pick up coin
			ws = ws.removeTile(x,y);
			return false;
		}
		if(block == -11) return ya > 0; // platform
		//if(block != 0) {
		//	System.out.println("collision w/ " + _x + "," + _y + "map coords " + x + "," + y + ": " + block);
		//}
		return block != 0;

		// ugh.  if we're simulating enemy state, we need to propagate here.
		// if (blocking && ya < 0)
		// {
		//   world.bump(x, y, large);
		// }
	}
}

