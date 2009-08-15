package com.reddit.programming.mario;

import java.util.SortedMap;
import java.util.TreeMap;

// the path planner internally represents a list of waypoints for each tile on
// the ground.  Different paths are admissible for different running speeds
// (platforms become out of reach when not going fast enough, etc), so we keep
// track of the range of xa (horizontal speed) values each waypoint is valid
// for.
public final class PathPlanner
{
	static public class Waypoint {
		public int x,y,ymin;
		public float cost;
	}

	// goal[x][xaIdx(xa)] is the next waypoint for tile x when running at speed xa
	Waypoint[][] goal;

	// jumpUpSteps[y] number of steps taken to jump onto a platform y blocks higher
	static final int jumpUpSteps[] = { 0,7,7,8,9 };
	static final int xaSteps = 19;
	private WorldState ws;

	PathPlanner() {
		goal = new Waypoint[22][xaSteps*2+1];
	}

	private static int stepsForElevationChange(int yblocks) {
		if(yblocks == 0) return 0;
		return 1;
	}

	private static float min_xa_for_jump(int xblocks, float steps) {
		float dn = (float) Math.pow(0.89, steps);
		//(120 89^(1 + n) + 100^n (-121 Dx + 120 (-89 + 11 n)))/(1100 (89^n - 100^n))
		// 9.70909+ (0.+ 1.2 n - 1.76 xblocks)/(-1. + 1. 0.89^n)
		return 9.70909f + (1.2f*steps - 1.76f*xblocks)/(dn-1);
	}

	// compute cost of move, assuming the map looks like this:
	//   _b
	// a_||
	//    |c_   a = x0,y0, b=x1-1,min_y, c=x1,y1
	//
	// so the jump must be at least ay-by high and the fall will be at least
	// cy-by high
	private float moveCost(int x0, int y0, int x1, int y1, int min_y, float xa) {
		if(y1 == y0 && min_y == y0) {
			// just run forward
			float g = MarioMath.stepsToRun(16*(x1-x0), xa);
			xa = MarioMath.runSpeed(xa, g);
			return g + calc(x1, MarioMath.runSpeed(xa, g));
		} else if(min_y < y0) { // do we need to jump?
			// can we jump high enough?
			if(min_y-y0 < -4)
				return Float.POSITIVE_INFINITY;

			float g = jumpUpSteps[y0-min_y];
			if(y1 != min_y)
				g += MarioMath.stepsToFall(16*(y1-min_y), 0);

			// can we jump far enough?
			if(min_xa_for_jump(x1-x0, g) > xa)
				return Float.POSITIVE_INFINITY; // we can't jump far enough

			// approximate velocity as the average needed to go from x0 to x1
			// in g timesteps
			float est_xa = (x1-x0)*16/g;

			return g + calc(x1, est_xa);
		} else {
			// falling
			float g = MarioMath.stepsToFall(16*(y1-y0), 0);

			// can we jump far enough?
			if(min_xa_for_jump(x1-x0, (int)(g+1)) > xa)
				return Float.POSITIVE_INFINITY; // we can't jump far enough

			float est_xa = (x1-x0)*16/g;

			return g + calc(x1, est_xa);
		}
	}

	private static final int xaIdx(float xa) {
		// linearize this by log(9.70909 - |xa|) * some constant
		// a scale of 8.5812 will give us one-run-timestep granularity
		// let's use 1/2 that
		float mag = (float) (8.5812*(2.27306-Math.log(9.709091-Math.abs(xa))));
		if(mag > xaSteps) mag = xaSteps; // cap magnitude somewhat arbitrarily; 19 timesteps of running
		return (int) (xa < 0 ? -mag : mag) + xaSteps;
	}

	private static final float from_xaIdx(int xaidx) {
		if(xaidx == 0)
			return 0;
		float mag = (float) (9.70909 * (1-Math.exp(-0.116534*Math.abs(xaidx))));
		return xaidx < 0 ? -mag : mag;
	}

	// generate a plan for the level using dynamic programming which is a fancy
	// way of saying we memoize partial results while searching for a global optimum
	private float calc(int x, float xa) {
		if(x >= 22) return 0;
		int xaidx = xaIdx(xa); // i bet i could make this even more confusing if i tried
		if(goal[x][xaidx] != null)
			return goal[x][xaidx].cost;

		xa = from_xaIdx(xaidx); // canonicalize

		int y0 = ws.heightmap[x];
		int min_y = y0;
		Waypoint w = null;
		for(int i=x+1;i<22;i++) {
			int y1 = ws.heightmap[i];
			if(y1 < min_y)
				min_y = y1;

			if(y0-min_y > 4) // there's no way we can reach past a >4 block high jump
				break;

			if(y1 == 22) // mind the gap
				continue;

			float c = moveCost(x,y0, i,y1, min_y, xa);
			if(c == Float.POSITIVE_INFINITY)
				continue;

			if(w == null) {
				w = new Waypoint();
				w.cost = Float.POSITIVE_INFINITY;
			}
			if(c < w.cost) {
				w.cost = c;
				w.x = 16*(i+ws.MapX)+8;
				w.y = 16*(y1+ws.MapY)-1;
				w.ymin = 16*(min_y+ws.MapY)-1;
			}
		}
		goal[x][xaidx] = w;
		if(w == null) // end of plan?
			return 0;
		else {
			System.out.printf("goal[%d][%d] -> (%d,%d) cost=%f\n",
					x,xaidx, w.x,w.y, w.cost);
		}
		return w.cost;
	}

	public Waypoint getNextWaypoint(float _x, float xa) {
		if(ws == null)
			return null;

		int x = (int)(_x+8)/16 - ws.MapX;
		calc(x, xa);
		return goal[x][xaIdx(xa)];
	}

	public void reset(WorldState _ws) {
		ws = _ws;
		for(int x=0;x<22;x++)
			for(int xa=0;xa<(xaSteps*2+1);xa++)
				goal[x][xa] = null;
	}

	static public Waypoint defaultWaypoint(MarioState s) {
		Waypoint w = new Waypoint();
		w.x = (int)s.x + 160;
		w.y = (int)s.y;
		w.ymin = (int)s.y - 64;
		return w;
	}
}

