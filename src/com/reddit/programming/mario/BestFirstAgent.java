package com.reddit.programming.mario;

import java.util.Comparator;
import java.util.PriorityQueue;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class BestFirstAgent extends RedditAgent implements Agent
{
	private boolean[] action;
	protected int[] marioPosition = null;
	protected Sensors sensors = new Sensors();
	private PriorityQueue<MarioState> pq;

	MarioState ms;
	float pred_x, pred_y;

	public BestFirstAgent() {
		super("BestFirstAgent");
		action = new boolean[Environment.numberOfButtons];
		reset();
		pq = new PriorityQueue<MarioState>(20, msComparator);
	}

	@Override
	public void reset() {
		// disable enemies for the time being
		GlobalOptions.pauseWorld = true;
	}

	private float runDistance(float v0, float steps) {
		float bleh = (float) Math.pow(0.89f, steps);
		//-78.5554 + 78.5554 0.89^J + 9.70909 J + (8.09091- 8.09091 0.89^J) v
		return 78.5554f*(bleh-1) + 9.70909f*steps + 8.09091f*(1-bleh)*v0;
	}

	// runDistance is terrible to invert, so use the secant method to solve it
	private float stepsToRun(float distance, float v0) {
		float x0 = 1,
			  x1 = 2;
		float xdiff;
		//int n=0;
		do {
			float fx0 = runDistance(v0, x0) - distance;
			float fx1 = runDistance(v0, x1) - distance;
			xdiff = fx1 * (x1 - x0)/(fx1 - fx0);
			x0 = x1;
			x1 -= xdiff;
			//System.out.printf("secantstep %d: x0:%f x1:%f fx0:%f fx1:%f xdiff:%f\n",
			//		n++, x0,x1, fx0,fx1, xdiff);
		} while(Math.abs(xdiff) > 1e-4);
		//if(x1 < 2)
		//	System.out.printf("stepstorun(%f,%f) -> %f\n", distance, v0, x1);
		return x1;
	}

	private float cost(MarioState s, MarioState initial) {
		if(s.dead)
			return Float.POSITIVE_INFINITY;

		// believe it or not, this has a derivation; i didn't pull these numbers out of my ass
		// d: damping constant = 0.89
		// s: step speed = 1.2
		// J: steps to look ahead; v: initial velocity
		// Sum[v*d^M + s*Sum[d^n, {n, 1, M}], {M, 0, J}]
		// (-d^2 s + d^(2 + J) s + d J s - d^2 J s + v - d v - d^(1 + J) v + 
		//  d^(2 + J) v)/(-1 + d)^2
		//
		// with d and s set above and J=20:
		// 123.264 + 8.30423 v
		// what i want to know is how far we could possibly go in twenty frames
//		float speed = 0.311817f*initial.xa + 6.68163f; // this is how fast we could possibly be going in 
//		float dist_to_travel = 123.264f + 8.30423f*initial.xa;
//		return (initial.x - s.x + dist_to_travel)/8;// + s.y/1000.0f; // height tiebreaker
        // we want to return #steps to goal; 20/dist_to_travel = N/(x - initial.x)
//		return (s.x - initial.x)*20/dist_to_travel;
        if(initial.x + 7*16 - s.x <= 0) return 0;
		// stepsToRun is a slight overestimate for some presently-unknown reason, so *0.9 with it
        return 0.9f*stepsToRun(initial.x + 7*16 - s.x, s.xa);
	}


	public static final Comparator<MarioState> msComparator = new MarioStateComparator();
	// yay copy and paste
	private static final int ACT_SPEED = 1;
	private static final int ACT_RIGHT = 2;
	private static final int ACT_JUMP = 4;
	private static final int ACT_LEFT = 8;

	private boolean useless_action(int a, MarioState s) {
		if((a&ACT_SPEED) == 0) return true; // haha
		//if((a&ACT_RIGHT) == 0) return true; // haha
//		if((a&ACT_LEFT)>0 && (a&ACT_RIGHT)>0) return true;
		if((a&ACT_JUMP)>0) {
			if(s.jumpTime == 0 && !s.mayJump) return true;
			if(s.jumpTime <= 0 && !s.onGround && !s.sliding) return true;
		}
		return false;
	}

	private int searchForAction(MarioState initialState, byte[][] map, int MapX, int MapY) {
		PriorityQueue<MarioState> pq = new PriorityQueue<MarioState>(20, msComparator);
        pq.clear();
		int a,n;
		// add initial set
		for(a=0;a<16;a++) {
			if(useless_action(a, initialState))
				continue;
			MarioState ms = initialState.next(a, map, MapX, MapY);
			ms.root_action = a;
			ms.pred = null;
			ms.g = 0;
			ms.cost = cost(ms, initialState);
			pq.add(ms);
			System.out.printf("BestFirst: root action %d initial cost=%f\n", a, ms.cost);
		}

		MarioState bestfound = pq.peek();
		for(n=0;n<10000 && !pq.isEmpty();n++) {
			MarioState next = pq.remove();
			//System.out.printf("a*: trying "); next.print();
			bestfound = marioMax(next,bestfound);
			for(a=0;a<16;a++) {
				if(useless_action(a, next))
					continue;
				MarioState ms = next.next(a, map, MapX, MapY);
				if(ms.dead) continue;
				ms.pred = next;
				bestfound = marioMax(next,bestfound);
				float h = cost(ms, initialState);
				ms.g = next.g + 1;
				ms.cost = ms.g + h + ((a&ACT_JUMP)>0?0.0001f:0);
				if(h <= 0) {
					pq.clear();
					System.out.printf("BestFirst: searched %d iterations; best a=%d cost=%f lookahead=%f\n", 
							n, ms.root_action, ms.cost, ms.g);
					MarioState s;
					for(s = ms;s != null;s = s.pred) {
						System.out.printf("state %d: ", (int)s.g);
						s.print();
					}
					return ms.root_action;
				}
				pq.add(ms);
			}
		}

		if (!pq.isEmpty())
			bestfound = marioMax(pq.remove(), bestfound);
		System.out.printf("BestFirst: giving up on search; best root_action=%d cost=%f lookahead=%f\n",
				bestfound.root_action, bestfound.cost, bestfound.g);
		// return best so far
		pq.clear();
		return bestfound.root_action;
	}


	public static MarioState marioMax(MarioState a, MarioState b) {
		if(a.g > b.g) return a;
		if(b.g > a.g) return b;
		return msComparator.compare(a, b) >= 0 ? a : b;
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
			System.out.println(String.format("mario x,y=(%5.1f,%5.1f)", mpos[0], mpos[1]));
			if(mpos[0] != pred_x || mpos[1] != pred_y) {
				System.out.println("mario state mismatch; aborting");
				System.exit(1);
			}
		}

		super.UpdateMap(sensors);

		// quantize mario's position to get the map origin
		int mX = (int)mpos[0]/16 - 11;
		int mY = (int)mpos[1]/16 - 11;
		int next_action = searchForAction(ms, sensors.levelScene, mX,mY);
		ms = ms.next(next_action, sensors.levelScene, mX,mY);
		pred_x = ms.x;
		pred_y = ms.y;
		System.out.println(String.format("action: %d; predicted x,y=(%5.1f,%5.1f) xa,ya=(%5.1f,%5.1f)",
				next_action, ms.x, ms.y, ms.xa, ms.ya));

		action[Mario.KEY_SPEED] = (next_action&1)!=0;
		action[Mario.KEY_RIGHT] = (next_action&2)!=0;
		action[Mario.KEY_JUMP] = (next_action&4)!=0;
		action[Mario.KEY_LEFT] = (next_action&8)!=0;

		return action;
	}

}
