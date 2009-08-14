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
	private class Waypoint {
		public int x,y;
		public float cost;
	}

	// goal[x][xaIdx(xa)] is the next waypoint for tile x when running at speed xa
	Waypoint[][] goal;

	// jumpUpSteps[y] number of steps taken to jump onto a platform y blocks higher
	static final int jumpUpSteps[] = { 0,7,7,8,9 };
	private WorldState ws;

	PathPlanner() {
		goal = new Waypoint[22][19*2+1];
	}

	private static int stepsForElevationChange(int yblocks) {
		if(yblocks == 0) return 0;
		return 1;
	}

	private static float min_xa_for_jump(int xblocks, int steps) {
		float dn = (float) Math.pow(0.89, steps);
		//(120 89^(1 + n) + 100^n (-121 Dx + 120 (-89 + 11 n)))/(1100 (89^n - 100^n))
		// 9.70909+ (0.+ 1.2 n - 1.76 xblocks)/(-1. + 1. 0.89^n)
		return 9.70909f + (1.2f*steps - 1.76f*xblocks)/(dn-1);
	}

	private float moveCost(int x0, int y0, int x1, int y1, float xa) {
		if(y1 == y0) {
			// just run forward
			float g = MarioMath.stepsToRun(16*(x1-x0), xa);
			xa = MarioMath.runSpeed(xa, g);
			return g + calc(x1, MarioMath.runSpeed(xa, g));
		} else if(y1 < y0) {
			// can we jump high enough?
			if(y1-y0 < -4)
				return Float.POSITIVE_INFINITY;

			int g = jumpUpSteps[y0-y1];

			// can we jump far enough?
			if(min_xa_for_jump(x1-x0, g) > xa)
				return Float.POSITIVE_INFINITY; // we can't jump far enough

			return g + calc(x1, MarioMath.runSpeed(xa, g));
		} else {
			// falling
			float g = MarioMath.stepsToFall(16*(y1-y0), 0);

			// can we jump far enough?
			if(min_xa_for_jump(x1-x0, (int)(g+1)) > xa)
				return Float.POSITIVE_INFINITY; // we can't jump far enough

			return g + calc(x1, MarioMath.runSpeed(xa, g));
		}
	}

	private static final int xaIdx(float xa) {
		// linearize this by log(9.70909 - |xa|) * some constant
		// a scale of 8.5812 will give us one-run-timestep granularity
		// let's use 1/2 that
		float mag = (float) (8.5812*(2.27306-Math.log(9.709091-Math.abs(xa))));
		if(mag > 19) mag = 19; // cap magnitude somewhat arbitrarily; 19 timesteps of running
		return (int) (xa < 0 ? -mag : mag);
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
		Waypoint w = new Waypoint();
		w.x = x; w.y = y0; w.cost = Float.POSITIVE_INFINITY;
		for(int i=x+1;i<22;i++) {
			int y1 = ws.heightmap[i];
			if(y1 > 14) // mind the gap
				continue;
			float c = moveCost(x,y0, i,y1, xa);
			if(c < w.cost) {
				w.cost = c;
				w.x = i;
				w.y = y1;
			}
			// FIXME: make this try two elevation changes
			//    _2
			// 1__||__3
			if(y1 != y0) // once we've seen an elevation change, we're done
				break;
		}
		goal[x][xaidx] = w;
		return w.cost;
	}

	public void reset(WorldState _ws) {
		ws = _ws;
		for(int x=0;x<22;x++)
			for(int xa=0;xa<19;xa++)
				goal[x][xa] = null;
	}

}

