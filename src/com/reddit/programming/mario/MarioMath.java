package com.reddit.programming.mario;

final class MarioMath {

	public static float stepsToJump(float h) {
		if(h < 26.6f) return 10*h/133;
		if(h < 64.6) return (float) (17-Math.sqrt(281-80*h/19))/2;
		return stepsToJump(64) + stepsToJump(h-64);
	}

	// I LOVE ALL THIS TYPING!
	// NOT ONLY ON THE KEYBOARD, BUT ALSO THE CREATION OF NEW TYPES AND
	// SPECIFYING THEIR USE
	static interface DistanceFunction { public float value(float dx0, float n); }

	static private class FallDistance implements DistanceFunction {
		public float value(float ya0, float steps) {
			return fallDistance(ya0, steps);
		}
	}

	static private class RunDistance implements DistanceFunction {
		public float value(float v0, float steps) {
			return runDistance(v0, steps);
		}
	}

	// converges quadratically but not always numerically stable
	static private float secantSolve(DistanceFunction f, float distance, float dx0, float min) {
		float x0=min, x1=min+30, xdiff;
		do {
			float fx0 = f.value(dx0, x0);
			float fx1 = f.value(dx0, x1);
			xdiff = (fx1-distance) * (x1 - x0)/(fx1 - fx0);
			x0 = x1;
			x1 -= xdiff;
			// if our iteration takes us negative, negate and hope it doesn't loop
			//if(x1 < min) x1 = 2*min-x1; // reflect about min
		} while(Math.abs(xdiff) > 1e-4);
		return x1;
	}

	// converges logarithmically, always works
	static private float bisect(DistanceFunction f, float distance, float dx0, float min, float max) {
		while(max-min > 1e-3) {
			float mid = (min+max)/2;
			float x = f.value(dx0, mid);
			if(x < distance)
				min = mid;
			else
				max = mid;
		}
		return (min+max)/2;
	}

	///////////////////////////////////////////////////////////////
	// public mathods (ha), finally

	static private final RunDistance _runDistance = new RunDistance();

	static private final FallDistance _fallDistance = new FallDistance();

	static public float runDistance(float v0, float steps) {
		// Mario's running iteration looks like this:
		//   xa'[n] = xa[n-1] + s
		//   x[n] = x[n-1] + xa'[n]
		//   xa[n] = xa'[n] * d
		// Working through the recurrence:
		// x[n] = x0 + xa0*Sum[d^i,{i,0,n-1}] + s*Sum[(n-i)*d^i,{i,0,n-1}]
		// where d === damping = 89/100 and s === step size = 12/10
		// if you substitute and solve you get this:

		float d_n = (float) Math.pow(0.89f, steps); // d^n
		return (1320*steps - 20*(d_n-1)*(55*v0-534))/121;
	}

	static public float fallDistance(float ya0, float steps) {
		// Mario's falling iteration looks like this:
		//   y[n] = y[n-1] + ya[n-1]
		//   ya[n] = (ya[n-1] * d) + s
		// Solving the recurrence:
		//   ya[n] = ya0*d^n + s*Sum[d^i, {i,0,n-1}]
		//   y[n] = y0 + ya0*Sum[d^i, {i,0,n-1}] + s*(Sum[(n-1-i)*d^i, {i,0,n-2}])
		//
		float d_n = (float) Math.pow(0.85f, steps); // d^n
		return 20*steps - 20*(d_n-1)*(ya0-20)/3;
	}

	static public float runSpeed(float xa0, float steps) {
		float d_n = (float) Math.pow(0.89f, steps);
		return -9.70909f * (d_n-1) + d_n*xa0;
	}

	// runDistance is terrible to invert, so use the secant method to solve it
	public static float stepsToRun(float distance, float v0) {
		float min = 0;
		if(distance < 0) {
			distance = -distance;
			v0 = -v0;
		}
		if(v0 < 0)
			min = (float) (-Math.log(1+v0/9.70909)/Math.log(0.89));
		//return secantSolve(_runDistance, distance, v0, min);
		// secantSolve isn't necessarily stable; just use bisection (and assume
		// 30 steps gets us as far as we ever want to go)
		return bisect(_runDistance, distance, v0, min, 30);
	}

	// as, of course, is fallDistance
	public static float stepsToFall(float height, float ya0) {
		// this has too many numerical problems; let's just underestimate it
		if(height < 0) {
			height = -height;
			ya0 = -ya0;
		}
		return secantSolve(_fallDistance, height, ya0, 0);
	}

	// how long will it take us to stomp enemy e?
	public static float stepsToStomp(MarioState ms, SpriteState e) {
		float dx = stepsToRun(e.x - ms.x, ms.xa);
		float dy = 0;
		if(e.y < ms.y) {
			if(ms.onGround) {
				dy = stepsToJump(ms.y - e.y);
			} else {
				// ugh.  steps to fall, then to jump?
			}
		} else if(e.y > ms.y) {
			dy = stepsToFall(e.y - ms.y, ms.ya);
		}
		return (float) Math.sqrt(dx*dx + dy*dy);
	}

	// can mario reach a ledge from his current jump trajectory
	public static boolean canReachLedge(float x0, float xa, 
			int apogeesteps, float apogeey, float x1, float y1) {
		// how long will it take us to get from x0 to x1 at our current speed,
		// regardless of the jump?
		float nsteps = stepsToRun(x1-x0, xa);

		// how far will we fall in that time?
		float ya = 2.8045f; // after a full height jump, mario's ya will be 2.8045 at the apogee
		float y = apogeey - fallDistance(ya, nsteps-apogeesteps);

		// do we end up higher than the ledge at that point?  if so, we can make it.
		return y <= y1;
	}
}

