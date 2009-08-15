package com.reddit.programming.mario;

import java.util.Comparator;
import java.util.PriorityQueue;

import java.io.IOException;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public final class BestFirstAgent extends RedditAgent implements Agent
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
	private static final int maxBreadth = 4000;
	private static final int maxSteps = 500;
	private int DrawIndex = 0;

	MarioState ms = null, ms_prev = null;
	float pred_x, pred_y;

	public BestFirstAgent() {
		super("BestFirstAgent");
		action = new boolean[Environment.numberOfButtons];
		reset();
		pq = new PriorityQueue<MarioState>(maxBreadth, msComparator);
		pq2 = new PriorityQueue<MarioState>(maxBreadth, msComparator);
	}

	@Override
	public void reset() {
		// disable enemies for the time being
		GlobalOptions.pauseWorld = true;
		ms = null;
		marioPosition = null;
	}

	private static final float lookaheadDist = 9*16;
	private float cost(MarioState s, MarioState initial) {
		if(s.dead)
			return Float.POSITIVE_INFINITY;

		int MarioX = (int)s.x/16 - s.ws.MapX;
		int goal = 21;
		// move goal back from the abyss
		//while(goal > 11 && s.ws.heightmap[goal] == 22) goal--;
		//no don't
		float steps = Math.abs(MarioMath.stepsToRun((goal+s.ws.MapX)*16+8 - s.x, s.xa));
		if(!s.onGround) steps += 0.001*s.y;

		return steps;
	}


	public static final Comparator<MarioState> msComparator = new MarioStateComparator();

	// so we use 0, 1, 2, 4, 7 jump frames, depending on the value of action>>3
	private boolean useless_action(int a, MarioState s) {
		if((a&MarioState.ACT_LEFT)>0 && (a&MarioState.ACT_RIGHT)>0) return true;
		if((a/MarioState.ACT_JUMP)>0) {
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
		while(!pq2.isEmpty() && pq.size() < maxBreadth/2)
			pq.add(pq2.remove());
		pq2.clear();
		return pq;
	}

	private int searchForAction(MarioState initialState, WorldState ws) {
		pq.clear();
		GlobalOptions.MarioPosSize = 0;

		initialState.ws = ws;
		initialState.g = 0;

		initialState.cost = cost(initialState, initialState);

		int a,n;
		// add initial set
		for(a=1;a<40;a++) {
			if(useless_action(a, initialState))
				continue;
			MarioState ms = initialState.next(a, ws);
			ms.root_action = a;
			ms.cost = 1 + cost(ms, initialState);
			pq.add(ms);
			if(verbose2)
				System.out.printf("BestFirst: root action %d initial cost=%f\n", a, ms.cost);
		}

		MarioState bestfound = pq.peek();

		// FIXME: instead of using a hardcoded number of iterations,
		// periodically grab the system millisecond clock and terminate the
		// search after ~40ms
		int pq_siz=0;
		for(n=0;n<maxSteps && !pq.isEmpty();n++) {
			if(pq.size() > maxBreadth)
				pq = prune_pq();
			MarioState next = pq.remove();

			// next.cost can be infinite, and still at the head of the queue,
			// if the node got marked dead
			if(next.cost == Float.POSITIVE_INFINITY) continue;

			int color = (int) Math.min(255, 10000*Math.abs(next.cost - next.pred.cost));
			color = color|(color<<8)|(color<<16);
			addLine(next.x, next.y, next.pred.x, next.pred.y, color);

			//System.out.printf("a*: trying "); next.print();
			for(a=1;a<40;a++) {
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
				ms.cost = ms.g + h;// + ((a/MarioState.ACT_JUMP)>0?0.0001f:0);
				if(h < 0.1f) {
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
		if(next_action/MarioState.ACT_JUMP > 0)
			next_action = (next_action&7) + 8;
		ms_prev = ms;
		ms = ms.next(next_action, ws);
		pred_x = ms.x;
		pred_y = ms.y;
		//System.out.println(String.format("action: %d; predicted x,y=(%5.1f,%5.1f) xa,ya=(%5.1f,%5.1f)",
		//		next_action, ms.x, ms.y, ms.xa, ms.ya));

		action[Mario.KEY_SPEED] = (next_action&MarioState.ACT_SPEED)!=0;
		action[Mario.KEY_RIGHT] = (next_action&MarioState.ACT_RIGHT)!=0;
		action[Mario.KEY_LEFT] = (next_action&MarioState.ACT_LEFT)!=0;
		action[Mario.KEY_JUMP] = (next_action&MarioState.ACT_JUMP)!=0;

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
