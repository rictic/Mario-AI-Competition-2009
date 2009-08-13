package com.reddit.programming.mario;

import java.util.Comparator;
import java.util.PriorityQueue;

import java.awt.Color;
import java.awt.Point;
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
	private PriorityQueue<MarioState> pq;
	private static final boolean verbose1 = true;
	private static final boolean verbose2 = true;
	private static final boolean drawPath = true;
	private int DrawIndex = 0;

	MarioState ms = null, ms_prev = null;
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
		ms = null;
		marioPosition = null;
	}

	private float runDistance(float v0, float steps) {
		// Mario's running iteration looks like this:
		//   xa'[n] = xa[n-1] + 1.2
		//   x[n] = x[n-1] + xa'[n]
		//   xa[n] = xa'[n] * 0.89
		// Working through the recurrence:
		// x[n] = x0 + xa0*Sum[d^i,{i,0,n-1}] + s*Sum[(n-i)*d^i,{i,0,n-1}]
		// where d === damping = 0.89 and s === step size = 1.2
		// if you substitute and solve you get this:

		float d_n = (float) Math.pow(0.89f, steps); // d^n
		return 88.2645f*(d_n-1) + 10.9091f*steps + 9.09091f*(1-d_n)*v0;

		// each of these constants is deliberately rounded upwards; we need to
		// slightly overestimate runDistance so that we slightly underestimate
		// our heuristic cost to goal
	}

	// runDistance is terrible to invert, so use the secant method to solve it
	private float stepsToRun(float distance, float v0) {
		float x0=1, x1=2, xdiff;
		float sgn = 1;
		if(distance < 0) { sgn = -1; distance = -distance; }
		do {
			float fx0 = runDistance(v0, x0) - distance;
			float fx1 = runDistance(v0, x1) - distance;
			xdiff = fx1 * (x1 - x0)/(fx1 - fx0);
			x0 = x1;
			x1 -= xdiff;
			// if our iteration takes us negative, negate and hope it doesn't loop
			if(x1 < 0) x1 = -x1;
		} while(Math.abs(xdiff) > 1e-4);
		return x1*sgn;
	}

	private static final float lookaheadDist = 10*16;
	private float cost(MarioState s, MarioState initial) {
		if(s.dead)
			return Float.POSITIVE_INFINITY;

		float tiebreaker = 0;
		// add a height tiebreaker iff there is an object in front of us
		// if we always add the tiebreaker, we end up taking unnecessary leaps
		// of faith down holes.  this just helps us get unstuck faster when we
		// land in front of something.
		if(initial.ws.map[11][12] != 0) // technically we can skip it if it's -11 (platform) as well
			tiebreaker += s.y*0.0001f;

		// GET COINS!
		boolean coingoal = false;
		for(int j=0;j<22;j++)
			for(int i=0;i<22;i++)
				if(s.ws.map[j][i] == 34) { // i really need to get rid of these magic numbers.  this = coin
					if(!coingoal) tiebreaker = Float.POSITIVE_INFINITY;
					tiebreaker = Math.min(tiebreaker, 
										  (Math.abs(stepsToRun(16*(s.ws.MapX+i)+8 - s.x, s.xa)) +
										   0.5f*Math.abs(16*(s.ws.MapY+j)+8 - s.y)));
					coingoal = true;
				}
		if(coingoal)
			return tiebreaker;

		// if we're falling into a hole, we get a huge penalty.  perhaps we can walljump out.
		// ...but this heuristic blows.  we need a better approach to falling
		// down holes in general.
//		if(s.y > 208)
//			tiebreaker += s.y;
		
		return stepsToRun(initial.x + lookaheadDist - s.x, s.xa) + tiebreaker;
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
		if (drawPath && GlobalOptions.MarioPosSize < 400) {
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

	private int searchForAction(MarioState initialState, WorldState ws) {
		pq.clear();
		initialState.ws = ws;
		initialState.g = 0;
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

		// FIXME: instead of using a hardcoded number of iterations,
		// periodically grab the system millisecond clock and terminate the
		// search after ~40ms
		for(n=0;n<4000 && !pq.isEmpty();) {
			DebugPolyLine line1 = new DebugPolyLine(Color.BLUE);
			MarioState next = pq.remove();

			// next.cost can be infinite, and still at the head of the queue,
			// if the node got marked dead
			if(next.cost == Float.POSITIVE_INFINITY) continue;

			if (drawPath)
				addToDrawPath(next);
			
			int color = (int) Math.min(255, 10000*Math.abs(next.cost - next.pred.cost));
			color = color|(color<<8)|(color<<16);
			addLine(next.x, next.y, next.pred.x, next.pred.y, color);
			line1.AddPoint(next.x, next.y);
			line1.AddPoint(next.pred.x, next.pred.y);
			line1.color = new Color(color);

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
				n++;
				if(h <= 0) {
					pq.clear();
					if(verbose1) {
						System.out.printf("BestFirst: searched %d iterations; best a=%d cost=%f lookahead=%f\n", 
								n, ms.root_action, ms.cost, ms.g);
					}
					MarioState s;
					if(GlobalOptions.MarioPosSize > 400-46)
						GlobalOptions.MarioPosSize = 400-46;
					
					DebugPolyLine line2 = new DebugPolyLine(Color.YELLOW);
					for(s = ms;s != initialState;s = s.pred) {
						if(verbose2) {
							System.out.printf("state %d: ", (int)s.g);
							s.print();
						}
						// green line shows taken path
						line2.AddPoint(s.x, s.y);
						line2.AddPoint(s.pred.x, s.pred.y);
						//addLine(s.x, s.y, s.pred.x, s.pred.y, 0x00ff00);
					}
					GlobalOptions.MarioLines.PushFront(line2);
					return ms.root_action;
				}
				pq.add(ms);
				bestfound = marioMin(ms,bestfound);
			}
			GlobalOptions.MarioLines.Push(line1);
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

	private void addToDrawPath(MarioState mario) {
		GlobalOptions.MarioPos[DrawIndex] = new int[]{(int)mario.x, (int)mario.y, costToTransparency(mario.cost), mario.marioMode()};
		DrawIndex++;
		if (DrawIndex >= 400)
			DrawIndex = 0;
	}

	
	public static int costToTransparency(float cost) {
		if (cost <= 0) return 80;
		return Math.max(0, 40-(int)cost);
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
