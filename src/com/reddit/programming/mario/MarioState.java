package com.reddit.programming.mario;

public class MarioState
{
  private static float GROUND_INERTIA = 0.89f;
  private static float AIR_INERTIA = 0.89f;

  public float x,y,xa = 0, ya = 0;
  public int facing = 1, jumpTime = 0;
  public boolean big = true,  // mario is big
         fire = true, // mario can throw fireballs
         dead = false, // dead
         onGround = false, // standing on ground
         mayJump = true,  // yep
         sliding = false; // sliding down the side of a wall
  public float xJumpSpeed = 0, yJumpSpeed = 0;

  public MarioState(float _x, float _y) {
    x = _x; y = _y;
  }

  private static final int ACT_SPEED = 1;
  private static final int ACT_RIGHT = 2;
  private static final int ACT_JUMP = 4;
  private static final int ACT_LEFT = 8;

  public MarioState next(int action, byte map[][] /*, EnemyState enemies[]*/) {
    // action bits:
    //  0: speed
    //  1: right
    //  2: jump
    //  3: left
    //  4: duck not implemented; waste of search time
    MarioState n = new MarioState(x,y);

    boolean ducking = false; // just... we aren't doing this
    float sideWaysSpeed = (action&ACT_SPEED) != 0 ? 1.2f : 0.6f;

    if (xa > 2) n.facing = 1;
    else if (xa < -2) n.facing = -1;

    // jumping logic
    // note: jumpTime ranges from 7 downto 0 during a standard upware jump
    // acceleration phase, after which the regular falling code takes over
    // jumpTime ranges from -6 upto 0 in the rare case of walljumps
    if ((action&ACT_JUMP) != 0 || (jumpTime < 0 && !onGround && !sliding)) {
      if (jumpTime < 0) { // post-walljump
        n.xa = xJumpSpeed;
        n.ya = -jumpTime * yJumpSpeed;
        n.jumpTime = jumpTime+1;

      } else if (onGround && mayJump) {
        n.xJumpSpeed = 0;
        n.yJumpSpeed = -1.9f;
        n.jumpTime = 7;
        n.ya = jumpTime * yJumpSpeed;
        n.onGround = false;
        n.sliding = false;

      } else if (sliding && mayJump) { // walljump?
        n.xJumpSpeed = -facing * 6.0f;
        n.yJumpSpeed = -2.0f;
        n.jumpTime = -6;
        n.xa = xJumpSpeed;
        n.ya = -jumpTime * yJumpSpeed;
        n.onGround = false;
        n.sliding = false;
        n.facing = -facing;

      } else if (jumpTime > 0) { // post-peak downward jump trajectory
        // apparently this acts different than falling?
        n.xa = xa + xJumpSpeed;
        n.ya = jumpTime * yJumpSpeed;
        n.jumpTime = n.jumpTime-1;
      }
    } else {
      n.jumpTime = 0;
    }

    // ducking code elided

    if ((action&ACT_LEFT) != 0 && !ducking) {
      if (facing == 1) n.sliding = false;
      n.xa = xa - sideWaysSpeed;
      if (jumpTime >= 0) n.facing = -1;
    }

    if ((action&ACT_RIGHT) != 0 && !ducking)
    {
      if (n.facing == -1) n.sliding = false;
      n.xa = xa + sideWaysSpeed;
      if (jumpTime >= 0) n.facing = 1;
    }

    if (((action&ACT_LEFT) == 0 && (action&ACT_RIGHT) == 0) || ducking || ya < 0 || onGround) {
      n.sliding = false;
    }

//    if ((action&ACT_SPEED) != 0 && canShoot && fire && world.fireballsOnScreen<2) {
//      // in theory: add fireball to our state
//    }
//    n.canShoot = (action&ACT_SPEED) == 0;

    n.mayJump = (onGround || sliding) && (action&ACT_JUMP) == 0;

    if (Math.abs(xa) < 0.5f) { n.xa = 0; }

    // friction while sliding against a wall
    if (sliding)
      n.ya = ya * 0.5f;

    n.onGround = false;
    n.move(xa, 0, map);
    n.move(0, ya, map);

    // world.level.height hardcoded as 15
    if (y > 15*16 + 16) { n.dead = true; }

    if (x < 0) { n.x = 0; n.xa = 0; }

    n.ya = ya * 0.85f; // downward air friction

    if (onGround) { // ground friction
      n.xa = xa * GROUND_INERTIA;
      // maximum speed is GROUND_INERTIA/(1-GROUND_INERTIA) * impulse_per_step
      // impulse_per_step = 0.6 if walking, 1.2 if running
      // max speed = 4.85 walking, 9.7 running
    } else { // falling
      n.xa = xa * AIR_INERTIA;
      n.ya = ya + 3;
    }

    return n;
  }

  private boolean move(float xa, float ya, byte map[][])
  {
    while (xa > 8) {
      if (!move(8, 0, map)) return false;
      xa -= 8;
    }
    while (xa < -8) {
      if (!move(-8, 0, map)) return false;
      xa += 8;
    }
    while (ya > 8) {
      if (!move(0, 8, map)) return false;
      ya -= 8;
    }
    while (ya < -8) {
      if (!move(0, -8, map)) return false;
      ya += 8;
    }

    boolean collide = false;
    float width = 8;
    float height = big ? 24 : 12;
    if (ya > 0)
    {
      if (isBlocking(x + xa - width, y + ya, xa, 0, map)) collide = true;
      else if (isBlocking(x + xa + width, y + ya, xa, 0, map)) collide = true;
      else if (isBlocking(x + xa - width, y + ya + 1, xa, ya, map)) collide = true;
      else if (isBlocking(x + xa + width, y + ya + 1, xa, ya, map)) collide = true;
    }
    if (ya < 0)
    {
      if (isBlocking(x + xa, y + ya - height, xa, ya, map)) collide = true;
      else if (collide || isBlocking(x + xa - width, y + ya - height, xa, ya, map)) collide = true;
      else if (collide || isBlocking(x + xa + width, y + ya - height, xa, ya, map)) collide = true;
    }
    if (xa > 0)
    {
      sliding = true;
      if (isBlocking(x + xa + width, y + ya - height, xa, ya, map)) collide = true;
      else sliding = false;
      if (isBlocking(x + xa + width, y + ya - height / 2, xa, ya, map)) collide = true;
      else sliding = false;
      if (isBlocking(x + xa + width, y + ya, xa, ya, map)) collide = true;
      else sliding = false;
    }
    if (xa < 0)
    {
      sliding = true;
      if (isBlocking(x + xa - width, y + ya - height, xa, ya, map)) collide = true;
      else sliding = false;
      if (isBlocking(x + xa - width, y + ya - height / 2, xa, ya, map)) collide = true;
      else sliding = false;
      if (isBlocking(x + xa - width, y + ya, xa, ya, map)) collide = true;
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

  private boolean isBlocking(float _x, float _y, float xa, float ya, byte map[][])
  {
    int x = (int) (_x / 16);
    int y = (int) (_y / 16);
    if (x == (int) (this.x / 16) && y == (int) (this.y / 16)) return false;

    float width = 8;
    float height = big ? 24 : 12;

    // FIXME: if we run off the edge of our map fragment here we're in trouble
    byte block = map[x][y];
    if(block == -11) return ya > 0;
    return block != 0;

    // ugh.  if we're simulating enemy state, we need to propagate here.
    // if (blocking && ya < 0)
    // {
    //   world.bump(x, y, large);
    // }
  }

}

