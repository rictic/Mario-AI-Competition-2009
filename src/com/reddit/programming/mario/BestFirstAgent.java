package com.reddit.programming.mario;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

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
	
	MarioState ms;
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
			// if our iteration takes us negative, negate and hope it doesn't loop
			if(x1 < 0) x1 = -x1;
		} while(Math.abs(xdiff) > 1e-4);
		return x1;
	}

	private static final float lookaheadDist = 11*16;
	private float cost(MarioState s, MarioState initial) {
		if(s.dead)
			return Float.POSITIVE_INFINITY;

        if(initial.x + lookaheadDist - s.x <= 0) return 0;
        return stepsToRun(initial.x + lookaheadDist - s.x, s.xa);
	}


	public static final Comparator<MarioState> msComparator = new MarioStateComparator();
	// yay copy and paste
	private static final int ACT_SPEED = 1;
	private static final int ACT_RIGHT = 2;
	private static final int ACT_JUMP = 4;
	private static final int ACT_LEFT = 8;

	private boolean useless_action(int a, MarioState s) {
	//	if((a&ACT_SPEED) == 0) return true; // our heuristic is good enough that we can let go of speed now
//		if((a&ACT_LEFT)>0 && (a&ACT_RIGHT)>0) return true;
		if((a&ACT_JUMP)>0) {
			if(s.jumpTime == 0 && !s.mayJump) return true;
			if(s.jumpTime <= 0 && !s.onGround && !s.sliding) return true;
		}
		return false;
	}

	private int searchForAction(MarioState initialState, byte[][] map, int MapX, int MapY) {
		PriorityBlockingQueue<MarioState> pq = new PriorityBlockingQueue<MarioState>(20, msComparator);
		
		// add initial set
		for(int a=0;a<16;a++) {
			if(useless_action(a, initialState))
				continue;
			MarioState ms = initialState.next(a, map, MapX, MapY);
			ms.root_action = a;
			ms.pred = null;
			ms.g = 0;
			ms.cost = cost(ms, initialState);
			pq.add(ms);
			//System.out.printf("BestFirst: root action %d initial cost=%f\n", a, ms.cost);
		}
		MarioState bestfound = pq.peek();
		for (int i = 0; i < searchers.length; i++){
			searchers[i] = new StateSearcher(initialState, map, MapX, MapY, pq, bestfound);
			searchPool.execute(searchers[i]);
		}
		try {
			Thread.sleep(40);
		} catch (InterruptedException e) {throw new RuntimeException("Interrupted from sleep searching for the best action");}
		
		for (StateSearcher searcher: searchers)
			searcher.stop();
		for (StateSearcher searcher: searchers)
			while(!searcher.isStopped){}
		
		for (StateSearcher searcher: searchers)
			if (searcher.bestfound != null)
				bestfound = marioMax(searcher.bestfound, bestfound);
		
		if (!pq.isEmpty())
			bestfound = marioMax(pq.remove(), bestfound);
		//System.out.printf("BestFirst: giving up on search; best root_action=%d cost=%f lookahead=%f\n",
		//		bestfound.root_action, bestfound.cost, bestfound.g);
		// return best so far
		return bestfound.root_action;
	}

	private class StateSearcher implements Runnable {
		private final PriorityBlockingQueue<MarioState> pq;
		private final MarioState initialState;
		private final byte[][] map;
		private final int MapX;
		private final int MapY;
		private boolean shouldStop = false;
		public boolean isStopped = false;
		private MarioState bestfound;
		
		public StateSearcher(MarioState initialState, byte[][] map, int MapX, int MapY, PriorityBlockingQueue<MarioState> pq, MarioState bestfound) {
			this.pq = pq; this.map = map; this.MapX = MapX; this.MapY = MapY; 
			this.initialState = initialState; this.bestfound = bestfound;
		}
		
		public void stop() {
			this.shouldStop = true;
		}
		
		public void run() {
			while(true){
				//repeat if an interrupted exception occurs, break otherwise
				try {
					doRun();
					break;
				} catch (InterruptedException e) {e.printStackTrace();}
			}
			isStopped = true;
		}
		
		private void doRun() throws InterruptedException {
			while((!shouldStop) && (!pq.isEmpty())) {
				MarioState next = pq.take();
				//System.out.printf("a*: trying "); next.print();
				bestfound = marioMax(next,bestfound);
				for(int a = 0;a<16;a++) {
					if(useless_action(a, next))
						continue;
					MarioState ms = next.next(a, map, MapX, MapY);
					if(ms.dead) continue;
					ms.pred = next;
					float h = cost(ms, initialState);
					ms.g = next.g + 1;
					ms.cost = ms.g + h + ((a&ACT_JUMP)>0?0.0001f:0);
					if(h <= 0) {
						//System.out.printf("BestFirst: searched %d iterations; best a=%d cost=%f lookahead=%f\n", 
						//		n, ms.root_action, ms.cost, ms.g);
						//MarioState s;
						//for(s = ms;s != null;s = s.pred) {
						//	System.out.printf("state %d: ", (int)s.g);
						//	s.print();
						//}
						bestfound = ms;
						return;
					}
					pq.add(ms);
				}
			}
		}
		
	}

	public static MarioState marioMax(MarioState a, MarioState b) {
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
			//System.out.println(String.format("mario x,y=(%5.1f,%5.1f)", mpos[0], mpos[1]));
			if(mpos[0] != pred_x || mpos[1] != pred_y) {
				System.out.println("mario state mismatch; attempting resync");
				ms.x = mpos[0]; ms.y = mpos[1];
				// we also need some guess for xa and ya here, ideally.
				//
				// generally this shouldn't happen, unless we mispredict
				// something.  currently if we stomp an enemy then we don't
				// predict that and get confused.
				//
				// but it will happen when we win, cuz we have no idea we won
				// and it won't let us move.
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
		//System.out.println(String.format("action: %d; predicted x,y=(%5.1f,%5.1f) xa,ya=(%5.1f,%5.1f)",
		//		next_action, ms.x, ms.y, ms.xa, ms.ya));

		action[Mario.KEY_SPEED] = (next_action&1)!=0;
		action[Mario.KEY_RIGHT] = (next_action&2)!=0;
		action[Mario.KEY_JUMP] = (next_action&4)!=0;
		action[Mario.KEY_LEFT] = (next_action&8)!=0;

		return action;
	}

}
