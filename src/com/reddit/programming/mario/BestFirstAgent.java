package com.reddit.programming.mario;

import java.util.Comparator;
import java.util.PriorityQueue;

import java.io.IOException;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class BestFirstAgent extends RedditAgent implements Agent
{
	private boolean[] action;
	protected int[] marioPosition = null;
	protected Sensors sensors = new Sensors();
	private PriorityQueue<MarioState> pq, pq2;
	private static final boolean verbose1 = true;
	private static final boolean verbose2 = true;
	private static final boolean drawPath = true;
	// enable to single-step with the enter key on stdin
	private static final boolean stdinSingleStep = false;
	private int DrawIndex = 0;

	MarioState ms = null, ms_prev = null;
	float pred_x, pred_y;

	public BestFirstAgent() {
		super("BestFirstAgent");
		action = new boolean[Environment.numberOfButtons];
		reset();
		pq = new PriorityQueue<MarioState>(400, msComparator);
		pq2 = new PriorityQueue<MarioState>(400, msComparator);
	}

	@Override
	public void reset() {
		// disable enemies for the time being
		GlobalOptions.pauseWorld = true;
		ms = null;
		marioPosition = null;
	}

	private float stepsToJump(float h) {
		if(h < 26.6f) return 10*h/133;
		if(h < 64.6) return (float) (17-Math.sqrt(281-80*h/19))/2;
		return Float.POSITIVE_INFINITY;
	}

	// I LOVE ALL THIS TYPING!
	// NOT ONLY ON THE KEYBOARD, BUT ALSO THE CREATION OF NEW TYPES AND
	// SPECIFYING THEIR USE
	static interface DistanceFunction { public float value(float dx0, float n); }

	static private class FallDistance implements DistanceFunction {
		public float value(float ya0, float steps) {
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
	}
	private static final FallDistance fallDistance = new FallDistance();

	static private class RunDistance implements DistanceFunction {
		public float value(float v0, float steps) {
			// Mario's running iteration looks like this:
			//   xa'[n] = xa[n-1] + 1.2
			//   x[n] = x[n-1] + xa'[n]
			//   xa[n] = xa'[n] * 0.89
			// Working through the recurrence:
			// x[n] = x0 + xa0*Sum[d^i,{i,0,n-1}] + s*Sum[(n-i)*d^i,{i,0,n-1}]
			// where d === damping = 89/100 and s === step size = 12/10
			// if you substitute and solve you get this:

			float d_n = (float) Math.pow(0.89f, steps); // d^n
			return (1320*steps - 20*(d_n-1)*(55*v0-534))/121;
		}
	}
	private static final RunDistance runDistance = new RunDistance();

	private float secantSolve(DistanceFunction f, float distance, float dx0, float min) {
		float x0=1, x1=2, xdiff;
		float sgn = 1;
		if(distance < 0) { sgn = -1; distance = -distance; }
		do {
			float fx0 = f.value(dx0, x0);
			float fx1 = f.value(dx0, x1);
			xdiff = (fx1-distance) * (x1 - x0)/(fx1 - fx0);
			x0 = x1;
			x1 -= xdiff;
			// if our iteration takes us negative, negate and hope it doesn't loop
			if(x1 < min) x1 = 2*min-x1; // reflect about min
		} while(Math.abs(xdiff) > 1e-4);
		return x1*sgn;
	}

	// runDistance is terrible to invert, so use the secant method to solve it
	private float stepsToRun(float distance, float v0) {
		return secantSolve(runDistance, distance, v0, 0);
	}

	// as, of course, is fallDistance
	private float stepsToFall(float height, float ya0) {
		// this has too many numerical problems; let's just underestimate it
		if(ya0 < 0) {
			// if we're "falling upwards" then find where we're falling
			// downwards at the same height
			float apogee = (float) (Math.log(1-ya0/20)/Math.log(0.85));
			return secantSolve(fallDistance, height, ya0, 2*apogee);
		}
		else
			return secantSolve(fallDistance, height, ya0, 0);
	}


	private static final float lookaheadDist = 9*16;
	private float cost(MarioState s, MarioState initial) {
		if(s.dead)
			return Float.POSITIVE_INFINITY;

		int MarioX = (int)s.x/16 - s.ws.MapX;
		int goal = 21;
		// move goal back from the abyss
		while(goal > 11 && s.ws.heightmap[goal] == 22) goal--;
		float steps = Math.abs(stepsToRun((goal+s.ws.MapX)*16+8 - s.x, s.xa));

		// NOTE: despite messing around with the various hacks below, nothing
		// seems to work better than just the horizintal distance, because it's
		// the only "accurate" metric i've tried.
		//
		// instead, we need a higher-level planner that gives us a set of
		// waypoints for simple jumps, and we can compute our cost to all the
		// following waypoints more easily

		//if(!s.onGround) steps += 0.001f;

		// this is a horrid overestimate.  might not work at all.
		// it's *supposed* to figure out how many steps it takes to surmount
		// the next obstacle.  it doesn't seem to help.
		/*
		if(MarioX < 21 && MarioX >= 0) {
			int MarioY = (int)s.y/16 - s.ws.MapY;
			int h0 = s.ws.heightmap[MarioX];
			int h1 = s.ws.heightmap[MarioX+1];
			int y0 = (h0+s.ws.MapY)*16;
			int y1 = (h1+s.ws.MapY)*16;
			//System.out.printf("MarioX=%d MarioY=%d heightmap[x+1]=%d y=%d\n",
			//		MarioX, MarioY, s.ws.heightmap[MarioX+1], y);
			if(h1 < MarioY) {
				float _y = s.y;
				if(!s.onGround && s.ya>0 && y0>_y) { // fall to ground so we can jump
					//System.out.printf("MarioY=%d ledgey=%d y=%8.1f ya=%f stepstoFall=", MarioY, y0, _y, s.ya);
					float fallsteps = stepsToFall(y0 - _y, s.ya);
					//System.out.printf("%f\n", fallsteps);
					steps += fallsteps;
					_y = y0;
				}
				float jumpsteps = stepsToJump(_y - y1);
				steps += jumpsteps;
				//System.out.printf("MarioY=%d ledgey=%d y=%8.1f stepstoJump=%f\n",
				//		MarioY, y1, _y, jumpsteps);
				_y = y1;
			}
			//if(!s.onGround && MarioY < y)
			//	steps += 0.1*stepsToFall(s.y - (y+s.ws.MapY)*16, s.ya);
		}
		*/

		return steps;
	}


	public static final Comparator<MarioState> msComparator = new MarioStateComparator();
	// yay copy and paste
	private static final int ACT_SPEED = 1;
	private static final int ACT_RIGHT = 2;
	private static final int ACT_JUMP = 4;
	private static final int ACT_LEFT = 8;

	private boolean useless_action(int a, MarioState s) {
		if((a&ACT_LEFT)>0 && (a&ACT_RIGHT)>0) return true;
		if((a&ACT_JUMP)>0) {
			if(s.jumpTime == 0 && !s.mayJump) return true;
			if(s.jumpTime <= 0 && !s.onGround && !s.sliding) return true;
		}
		return false;
	}

	private void addLine(float x0, float y0, float x1, float y1, int color) {
		if(drawPath && GlobalOptions.MarioPosSize < 400) {
			GlobalOptions.MarioPos[GlobalOptions.MarioPosSize][0] = (int)x0;
			GlobalOptions.MarioPos[GlobalOptions.MarioPosSize][1] = (int)y0;
			GlobalOptions.MarioPos[GlobalOptions.MarioPosSize][2] = color;
			GlobalOptions.MarioPosSize++;
			GlobalOptions.MarioPos[GlobalOptions.MarioPosSize][0] = (int)x1;
			GlobalOptions.MarioPos[GlobalOptions.MarioPosSize][1] = (int)y1;
			GlobalOptions.MarioPos[GlobalOptions.MarioPosSize][2] = color;
			GlobalOptions.MarioPosSize++;
		}
	}

	private PriorityQueue<MarioState> prune_pq() {
		// first, swap pq2 and pq
		PriorityQueue<MarioState> p = pq; pq = pq2; pq2 = p;
		while(!pq2.isEmpty() && pq.size() < 200)
			pq.add(pq2.remove());
		pq2.clear();
		return pq;
	}

	private int searchForAction(MarioState initialState, WorldState ws) {
		pq.clear();
		initialState.ws = ws;
		initialState.g = 0;
		initialState.cost = cost(initialState, initialState);
		int a,n;
		// add initial set
		for(a=1;a<16;a++) {
			if(useless_action(a, initialState))
				continue;
			MarioState ms = initialState.next(a, ws);
			ms.root_action = a;
			ms.cost = 1 + cost(ms, initialState);
			pq.add(ms);
			if(verbose1)
				System.out.printf("BestFirst: root action %d initial cost=%f\n", a, ms.cost);
		}

		MarioState bestfound = pq.peek();

		GlobalOptions.MarioPosSize = 0;

		// FIXME: instead of using a hardcoded number of iterations,
		// periodically grab the system millisecond clock and terminate the
		// search after ~40ms
		int pq_siz=0;
		for(n=0;n<1000 && !pq.isEmpty();n++) {
			if(pq.size() > 400)
				pq = prune_pq();
			MarioState next = pq.remove();

			// next.cost can be infinite, and still at the head of the queue,
			// if the node got marked dead
			if(next.cost == Float.POSITIVE_INFINITY) continue;

			int color = (int) Math.min(255, 10000*Math.abs(next.cost - next.pred.cost));
			color = color|(color<<8)|(color<<16);
			addLine(next.x, next.y, next.pred.x, next.pred.y, color);

			//System.out.printf("a*: trying "); next.print();
			for(a=1;a<16;a++) {
				if(useless_action(a, next))
					continue;
				MarioState ms = next.next(a, next.ws);
				ms.pred = next;

				// if we die, prune our predecessor node that got us here
				if(ms.dead) {
					// removing things from a priority queue is ridiculously
					// slow, so we'll just mark it dead
					ms.pred.cost = Float.POSITIVE_INFINITY;
					continue;
				}

				float h = cost(ms, initialState);
				ms.g = next.g + 1;
				ms.cost = ms.g + h;// + ((a&ACT_JUMP)>0?0.0001f:0);
				if(h < 0.5f) {
					pq.clear();
					if(verbose1) {
						System.out.printf("BestFirst: searched %d iterations (%d states); best a=%d cost=%f lookahead=%f\n", 
								n, pq_siz, ms.root_action, ms.cost, ms.g);
					}
					MarioState s;
					if(GlobalOptions.MarioPosSize > 400-46)
						GlobalOptions.MarioPosSize = 400-46;
					for(s = ms;s != initialState;s = s.pred) {
						if(verbose2) {
							System.out.printf("state %d: ", (int)s.g);
							s.print();
						}
						// green line shows taken path
						addLine(s.x, s.y, s.pred.x, s.pred.y, 0x00ff00);
					}
					return ms.root_action;
				}
				pq.add(ms);
				pq_siz++;
				bestfound = marioMin(ms,bestfound);
			}
		}

		if (!pq.isEmpty())
			bestfound = marioMin(pq.remove(), bestfound);
		if(verbose1) {
			System.out.printf("BestFirst: giving up on search; best root_action=%d cost=%f lookahead=%f\n",
					bestfound.root_action, bestfound.cost, bestfound.g);
		}
		// return best so far
		pq.clear();
		return bestfound.root_action;
	}

	public static int costToTransparency(float cost) {
		if (cost <= 0) return 100;
		return Math.max(0, 20-(int)cost);
	}

	public static MarioState marioMin(MarioState a, MarioState b) {
		if(a == null) return b;
		if(b == null) return a;
		// compare heuristic cost only
		if(a.cost - a.g <= b.cost - b.g) return a;
		return b;
	}

	@Override
	public boolean[] getAction(Environment observation)
	{
		sensors.updateReadings(observation);
		marioPosition = sensors.getMarioPosition();
		float[] mpos = observation.getMarioFloatPos();
		if(ms == null) {
			// assume one frame of falling before we get an observation :(
			ms = new MarioState(mpos[0], mpos[1], 0.0f, 3.0f);
		} else {
			//System.out.println(String.format("mario x,y=(%5.1f,%5.1f)", mpos[0], mpos[1]));
			if(mpos[0] != pred_x || mpos[1] != pred_y) {
				// generally this shouldn't happen, unless we mispredict
				// something.  currently if we stomp an enemy then we don't
				// predict that and get confused.
				//
				// but it will happen when we win, cuz we have no idea we won
				// and it won't let us move.
				if(verbose1)
					System.out.println("mario state mismatch; attempting resync");
				resync(observation);
			}
		}

		super.UpdateMap(sensors);

		// quantize mario's position to get the map origin
		WorldState ws = new WorldState(sensors.levelScene, mpos);

		int next_action = searchForAction(ms, ws);
		ms_prev = ms;
		ms = ms.next(next_action, ws);
		pred_x = ms.x;
		pred_y = ms.y;
		//System.out.println(String.format("action: %d; predicted x,y=(%5.1f,%5.1f) xa,ya=(%5.1f,%5.1f)",
		//		next_action, ms.x, ms.y, ms.xa, ms.ya));

		action[Mario.KEY_SPEED] = (next_action&1)!=0;
		action[Mario.KEY_RIGHT] = (next_action&2)!=0;
		action[Mario.KEY_JUMP] = (next_action&4)!=0;
		action[Mario.KEY_LEFT] = (next_action&8)!=0;

		if(stdinSingleStep) {
			try {
				System.in.read();
			} catch(IOException e) {};
		}

		return action;
	}

	private void resync(Environment observation) {
		float[] mpos = observation.getMarioFloatPos();
		ms.x = mpos[0]; ms.y = mpos[1];
		ms.mayJump = observation.mayMarioJump();
		ms.onGround = observation.isMarioOnGround();
		// again, Mario's iteration looks like this:
		//   xa',ya'[n] = xa,ya[n-1] + lastmove_sx,y
		//   x,y[n] = x,y[n-1] + xa',ya'[n]
		//   xa,ya[n] = xa',ya'[n] * damp_x,y

		// lastmove_s was guessed wrong, or we wouldn't be out of sync.  we can
		// directly get the new xa and ya, as long as no collisions occurred.
		// if there *was* a collision and xa,ya are wrong, they probably will
		// be corrected by each call next()
		if(ms_prev != null) {
			ms.xa = (ms.x - ms_prev.x) * 0.89f;
			ms.ya = (ms.y - ms_prev.y) * 0.85f;
		}
	}

}
