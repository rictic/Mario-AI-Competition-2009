package com.reddit.programming.mario;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class BestFirstAgent extends RedditAgent implements Agent
{
	private boolean[] action;
	protected int[] marioPosition = null;
	protected Sensors sensors = new Sensors();
	private static final int simultaneousSearchers = Runtime.getRuntime().availableProcessors();
	private ExecutorService searchPool = Executors.newFixedThreadPool(simultaneousSearchers);
	private StateSearcher[] searchers = new StateSearcher[simultaneousSearchers];
	
	private static final boolean verbose1 = true;
	private static final boolean verbose2 = true;
	private static final boolean drawPath = true;

	MarioState ms = null, ms_prev = null;
	float pred_x, pred_y;

	public BestFirstAgent() {
		super("BestFirstAgent");
		action = new boolean[Environment.numberOfButtons];
		reset();
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

	private int searchForAction(MarioState initialState, WorldState ws) {
		PriorityQueue<MarioState> pq = new PriorityQueue<MarioState>(20, msComparator);
		int i = 0;
		initialState.ws = ws;
		initialState.g = 0;
		// add initial set
		for(int a=1;a<16;a++) {
			if(useless_action(a, initialState))
				continue;
			MarioState ms = initialState.next(a, ws);
			ms.root_action = a;
			ms.cost = 1 + cost(ms, initialState);
			pq.add(ms);
			if(verbose1)
				System.out.printf("BestFirst: root action %d initial cost=%f\n", a, ms.cost);
		}
		PriorityQueue<MarioState>[] pqs = new PriorityQueue[searchers.length];
		//System.out.println("creating searchers");
		for (i = 0; i < pqs.length; i++) pqs[i] = new PriorityQueue<MarioState>(20, msComparator);
		i = 0;
		while (!pq.isEmpty())
			pqs[i++%pqs.length].add(pq.remove());
		
		for (i = 0; i < searchers.length; i++){
			searchers[i] = new StateSearcher(initialState, ws, pqs[i], i);
			searchPool.execute(searchers[i]);
		}
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {throw new RuntimeException("Interrupted from sleep searching for the best action");}
		for (StateSearcher searcher: searchers)
			searcher.stop();
		for (StateSearcher searcher: searchers)
			while(!searcher.isStopped){}
		
		MarioState bestfound = null;
		for (StateSearcher searcher: searchers) {
			bestfound = marioMin(searcher.bestfound, bestfound);

			if (verbose1)
				System.out.printf("searcher_(%d): best root_action=%d cost=%f lookahead=%f\n",
						searcher.id, bestfound.root_action, bestfound.cost, bestfound.g);
		}
		
		// return best so far
		return bestfound.root_action;
	}

	private class StateSearcher implements Runnable {
		private final PriorityQueue<MarioState> pq;
		private final MarioState initialState;
		private final WorldState ws;
		private final int id;
		private boolean shouldStop = false;
		public boolean isStopped = false;
		private MarioState bestfound;
		private int drawIndex;
		
		public StateSearcher(MarioState initialState, WorldState ws, PriorityQueue<MarioState> pq, int id) {
			this.pq = pq; this.ws = ws; 
			this.initialState = initialState; this.bestfound = null;
			this.id = id; drawIndex = id;
		}

		public void stop() {
			this.shouldStop = true;
		}
		
		public void run() {
			doRun();
			isStopped = true;
		}
		
		private void doRun() {
			int n = 0;
			bestfound = pq.peek();
			while((!shouldStop) && (!pq.isEmpty())) {
				MarioState next = pq.remove();

				// next.cost can be infinite, and still at the head of the queue,
				// if the node got marked dead
				if(next.cost == Float.POSITIVE_INFINITY) continue;

				if(drawPath)
					addToDrawPath(next.pred);

				bestfound = marioMin(next,bestfound);
				for(int a=1;a<16;a++) {
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
						if(verbose1) {
							System.out.printf("BestFirst: searched %d iterations; best a=%d cost=%f lookahead=%f\n", 
									n, ms.root_action, ms.cost, ms.g);
						}
						if(verbose2) {
							MarioState s;
							for(s = ms;s != initialState;s = s.pred) {
								System.out.printf("state %d: ", (int)s.g);
								s.print();
							}
						}
						bestfound = ms;
						return;
					}
					pq.add(ms);
				}
			}
		}

		private void addToDrawPath(MarioState mario) {
			GlobalOptions.MarioPos[drawIndex] = new int[]{(int)mario.x, (int)mario.y, costToTransparency(mario.cost), mario.marioMode()};
			drawIndex += simultaneousSearchers;
			if (drawIndex >= 400)
				drawIndex = id;
		}
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
