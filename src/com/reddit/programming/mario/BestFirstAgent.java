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

	private float cost(MarioState s, MarioState initial) {
		if(s.dead)
			return Float.POSITIVE_INFINITY;

		// TODO: how far right can mario go from here holding down speed+right?
		// 
		// cost = initial.x + n*16 - s.x
		//int lookahead_frames = 10;
		// how far could we conceivably go holding speed+right from the initial state?
		// xa[1] = (xa[0] + 3) * .89
		// xa[2] = (((xa[0] + 3) * .89) + 3) * .89 =  xa[0]*.89^2 + 3*(.89 + .89^2)
		// xa[n] = xa[0]*.89^N + 3*.89*(.89^n - 1)/(.89 - 1) (geometric series)
		//double speed = initial.xa*Math.pow(0.89, lookahead_frames) + 
		//  1.2*0.89*(Math.pow(0.89, lookahead_frames) - 1)/(0.89 - 1);
		//
		// 0.89^10 = 0.311817
		// the rest of that junk: 6.68163
		float speed = 0.311817f*initial.xa + 6.68163f; // this is how fast we could possibly be going in ten frames
		// what i want to know is how far we could possibly go in ten frames
		return (initial.x - s.x + 6*16)/speed;// + (s.y-initial.y)/100.0f; // height tiebreaker
	}

	// yay copy and paste
	private static final int ACT_SPEED = 1;
	private static final int ACT_RIGHT = 2;
	private static final int ACT_JUMP = 4;
	private static final int ACT_LEFT = 8;

	private boolean useless_action(int a, MarioState s) {
		if((a&ACT_JUMP)>0) {
			if(s.jumpTime == 0 && !s.mayJump) return true;
			if(s.jumpTime < 0 && !s.onGround && !s.sliding) return true; // post-walljump
		}
		return false;
	}

	public static final Comparator<MarioState> msComparator = new MarioStateComparator();
	//all actions save those where we're pressing left and right at the same time
	public static final int[] reasonableActions = new int[] {0,1,2,3,4,5,6,7,8,9,12,13};
	private int searchForAction(MarioState initialState, byte[][] map, int MapX, int MapY) {
		PriorityQueue<MarioState> pq = new PriorityQueue<MarioState>(20, msComparator);
		int n;
        pq.clear();
		// add initial set
		for(int a : reasonableActions) {
			if(useless_action(a, initialState))
				continue;
			MarioState ms = initialState.next(a, map, MapX, MapY);
			ms.root_action = a;
			ms.g = 0;
			ms.h = cost(ms, initialState);
			ms.cost = ms.h;
			pq.add(ms);
		}

		MarioState bestfound = pq.poll();
		
		for(n=0;n<70000;n++) {
			if (pq.size() == 0)
				return bestfound.root_action;
			MarioState next = pq.remove();
			bestfound = marioMax(next,bestfound);
			for(int a : reasonableActions) {
				if(useless_action(a, next))
					continue;
				MarioState ms = next.next(a, map, MapX, MapY);
				if(ms.dead) continue;
				bestfound = marioMax(next,bestfound);
				ms.h = cost(ms, initialState);
				ms.g = next.g + 1;
				ms.cost = ms.g + ms.h;
				if(ms.h <= 0) {
					pq.clear();
					System.out.printf("search terminated after %d iterations; best root_action=%d cost=%f\n", 
							n, ms.root_action, ms.cost);
					return ms.root_action;
				}
				pq.add(ms);
			}
		}

		if (pq.size() != 0)
			bestfound = marioMax(pq.remove(), bestfound);
		System.out.printf("giving up on search; best root_action=%d cost=%f lookahead=%f\n",
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
