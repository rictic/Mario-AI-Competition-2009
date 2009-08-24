package com.reddit.programming.mario;

import java.io.IOException;
import java.util.Comparator;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public abstract class HeuristicSearchingAgent extends RegisterableAgent implements Agent
{
	public static final Comparator<MarioState> msComparator = new MarioStateComparator();

	protected final boolean[] action = new boolean[Environment.numberOfButtons];
	protected int[] marioPosition = null;
	protected Sensors sensors = new Sensors();
	
	protected static final boolean verbose1 = false;
	protected static final boolean verbose2 = false;
	protected static final boolean drawPath = true;
	// enable to single-step with the enter key on stdin
	protected static final boolean stdinSingleStep = false;

	MarioState ms = null, ms_prev = null;
	WorldState ws = null;
	float pred_x, pred_y;
	boolean won = false;

	public HeuristicSearchingAgent(String name) {
		super(name);
		reset();
	}

	@Override
	public void reset() {
		ms = null;
		marioPosition = null;
		won = false;
	}

	protected float cost(MarioState s, MarioState initial) {
		float steps = 0;
		if(s.dead)
			steps += Tunables.DeadCost;
		steps += Tunables.HurtCost * s.hurt();

		int MarioX = (int)s.x/16 - s.ws.MapX;
		if (MarioX < 0)
		{
			System.out.println("Whuh ?");
			MarioX = 0;
		}
		int goal = 21;
		steps += Tunables.FactorA * MarioMath.stepsToRun((goal+s.ws.MapX)*16+8 - s.x, s.xa);
		// if we're standing in front of some thing, give the heuristic a
		// little help also adds a small penalty for walking up to something in
		// the first place
		if(MarioX < 21) {
			int thisY = s.ws.heightmap[MarioX];
			if(thisY == 22) { // we're either above or inside a chasm
				float edgeY = (22+s.ws.MapY)*16;
				// find near edge
				for(int i=MarioX-1;i>=0;i--) {
					if(s.ws.heightmap[i] != 22) {
						edgeY = (s.ws.heightmap[i]+s.ws.MapY)*16;
						break;
					}
				}
				if(s.y > edgeY+1) { // we're inside a chasm; don't waste time searching for a way out
					steps += Tunables.ChasmPenalty;
				}
			}
			float nextColY = (s.ws.heightmap[MarioX+1] + s.ws.MapY)*16;
			if(nextColY < s.y)
				steps += Tunables.FactorB *MarioMath.stepsToJump(s.y-nextColY);
		}

		return steps;
	}

	static final public boolean useless_action(int a, MarioState s) {
		// speed without left or right: useless
		if((a&MarioState.ACT_SPEED)>0 && 
		   !((a&MarioState.ACT_LEFT)>0 || (a&MarioState.ACT_RIGHT)>0))
			return true;
		// left and right at the same time: useless
		if((a&MarioState.ACT_LEFT)>0 && (a&MarioState.ACT_RIGHT)>0) return true;
		// jumping when the jump button doesn't do anything: useless
		if((a/MarioState.ACT_JUMP)>0) {
			if(s.jumpTime == 0 && !s.mayJump) return true;
			if(s.jumpTime <= 0 && !s.onGround && !s.sliding) return true;
		}
		// standing next to something that we're going to collide with by
		// moving right: useless (except for walljumps actually)
		int ix = (int) s.x;
		if((a&MarioState.ACT_RIGHT)>0 && s.xa == 0 && (s.x - ix) == 0 && (ix&15) == (16-5)) {
			// ok, we are exactly at a brick boundary.  is there something there?
			if(s.ws.isBlocking((ix+5)/16, (int)(s.y/16), 1,s.ya) ||
			   s.ws.isBlocking((ix+5)/16, (int)(s.y/16)-1, 1,s.ya) ||
			   s.ws.isBlocking((ix+5)/16, (int)(s.y/16)-2, 1,s.ya))
				return true;
		}
		return false;
	}

	protected void addLine(float x0, float y0, float x1, float y1, int color) {
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

	protected abstract int searchForAction(MarioState initialState, WorldState ws);
	
	public static int costToTransparency(float cost) {
		if (cost <= 0) return 80;
		return Math.max(0, 40-(int)cost);
	}

	public static MarioState marioMin(MarioState a, MarioState b) {
		if(a == null) return b;
		if(b == null) return a;
		// compare heuristic cost only
		if((a.cost - a.g) <= (b.cost - b.g)) return a;
		return b;
	}

	@Override
	public boolean[] getAction(Environment observation)
	{
		if (won)
			return action;
		sensors.updateReadings(observation);
		marioPosition = sensors.getMarioPosition();
		float[] mpos = observation.getMarioFloatPos();
		if(ms == null) {
			// assume one frame of falling before we get an observation :(
			ms = new MarioState(mpos[0], mpos[1], 0.0f, 3.0f);
			ws = new WorldState(sensors.levelScene, ms, observation.getEnemiesFloatPos());
		} else {
			ws = ws.update(sensors.levelScene, ms, observation.getEnemiesFloatPos());

			//System.out.println(String.format("mario x,y=(%5.1f,%5.1f)", mpos[0], mpos[1]));
			if(mpos[0] != pred_x || mpos[1] != pred_y) {
				if (!epsilon(mpos[0],pred_x)||!epsilon(mpos[1], pred_y))
				{
					// generally this shouldn't happen, unless we mispredict
					// something.  currently if we stomp an enemy then we don't
					// predict that and get confused.
	
					// but it will happen when we win, cuz we have no idea we won
					// and it won't let us move.  well, let's guess whether we won:
					if(mpos[0] > 4000 && mpos[0] == ms_prev.x && mpos[1] == ms_prev.y) {
						if (verbose1)
							System.out.println("ack, can't move.  assuming we just won");
						won = true;
						return action;
					}
					if(verbose1) {
						System.out.printf("mario state mismatch (%f,%f) -> (%f,%f); attempting resync\n",
								ms.x,ms.y, mpos[0], mpos[1]);
						if (stdinSingleStep)
						{
							try {
								System.in.read();
							} catch(IOException e) {}
						}
					}
				}
			}
			resync(observation, !epsilon(mpos[0],pred_x), !epsilon(mpos[1],pred_y));
		}
		// resync these things all the time
		ms.mayJump = observation.mayMarioJump();
		ms.onGround = observation.isMarioOnGround();
		ms.big = observation.getMarioMode() > 0;

		if(verbose2) {
			float[] e = observation.getEnemiesFloatPos();
			for(int i=0;i<e.length;i+=3) {
				System.out.printf(" e %d %f,%f\n", (int)e[i], e[i+1], e[i+2]);
			}
		}

		int next_action = searchForAction(ms, ws);
		if(next_action/MarioState.ACT_JUMP > 0)
			next_action = (next_action&7) + 8;
		ms_prev = ms;
		ms = ms.next(next_action, ws);
		pred_x = ms.x;
		pred_y = ms.y;
		if(verbose2) {
			System.out.printf("MarioState (%f,%f,%f,%f) -> action %d -> (%f,%f,%f,%f)\n",
				ms_prev.x, ms_prev.y, ms_prev.xa, ms_prev.ya,
				next_action,
				ms.x, ms.y, ms.xa, ms.ya);
		}

		action[Mario.KEY_SPEED] = (next_action&MarioState.ACT_SPEED)!=0;
		action[Mario.KEY_RIGHT] = (next_action&MarioState.ACT_RIGHT)!=0;
		action[Mario.KEY_LEFT] = (next_action&MarioState.ACT_LEFT)!=0;
		action[Mario.KEY_JUMP] = (next_action&MarioState.ACT_JUMP)!=0;

		if(stdinSingleStep) 
		{
			try {
				System.in.read();
			} catch(IOException e) {}
		}

		return action;
	}
	
	private static boolean epsilon(float a, float b)
	{
		return Math.abs(a-b) < 0.01;
	}

	private void resync(Environment observation, boolean x, boolean y) {
		float[] mpos = observation.getMarioFloatPos();
		ms.x = mpos[0];
		ms.y = mpos[1];

		// lastmove_s was guessed wrong, or we wouldn't be out of sync.  we can
		// directly get the new xa and ya, as long as no collisions occurred.
		// if there *was* a collision and xa,ya are wrong, they probably will
		// be corrected by each call next()
		if(ms_prev != null) {
			if (x)
				ms.xa = (ms.x - ms_prev.x) * 0.89f;
			if (y)
				ms.ya = (ms.y - ms_prev.y) * 0.85f;
		}
	}
}
