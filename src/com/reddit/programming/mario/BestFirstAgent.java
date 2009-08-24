package com.reddit.programming.mario;

import java.awt.Color;
import java.util.*;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.GlobalOptions;

public final class BestFirstAgent extends HeuristicSearchingAgent implements Agent
{
	private PrioQ pq;
	private static final int maxSteps = 500;

	public BestFirstAgent() {
		super("BestFirstAgent");
		pq = new PrioQ(Tunables.MaxBreadth);
	}

	@Override
	public void reset() {
		// disable enemies for the time being
		//GlobalOptions.pauseWorld = true;
		ms = null;
		marioPosition = null;
	}

//	private static final float lookaheadDist = 9*16;
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
		// move goal back from the abyss
		//while(goal > 11 && s.ws.heightmap[goal] == 22) goal--;
		//no don't
		//
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


	public static final Comparator<MarioState> msComparator = new MarioStateComparator();

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

	@Override
	protected int searchForAction(MarioState initialState, WorldState ws) {
		pq.clear();
		GlobalOptions.MarioPosSize = 0;

		initialState.ws = ws;
		initialState.g = 0;
		initialState.dead = false;

		initialState.cost = cost(initialState, initialState);

		int a,n;
		// add initial set
		for(a=0;a<16;a++) {
			if(useless_action(a, initialState))
				continue;
			MarioState ms = initialState.next(a, ws);
			ms.root_action = a;
			ms.cost = Tunables.FactorC + cost(ms, initialState);
			pq.offer(ms);
			if(verbose2)
				System.out.printf("BestFirst: root action %d initial cost=%f\n", a, ms.cost);
		}

		MarioState bestfound = pq.peek();

		// FIXME: instead of using a hardcoded number of iterations,
		// periodically grab the system millisecond clock and terminate the
		// search after ~40ms
		int pq_siz=0;
		for(n=0;n<maxSteps && !pq.isEmpty();n++) {
			DebugPolyLine line1 = null;
			if (drawPath)
				line1 = new DebugPolyLine(Color.BLUE);
			MarioState next = pq.poll();

			if (drawPath)
			{
				int color = (int) Math.min(255, 10000*Math.abs(next.cost - next.pred.cost));
				color = color|(color<<8)|(color<<16);
				addLine(next.x, next.y, next.pred.x, next.pred.y, color);
				line1.AddPoint(next.x, next.y);
				line1.AddPoint(next.pred.x, next.pred.y);
				line1.color = new Color(color);
			}
			//System.out.printf("a*: trying "); next.print();
			for(a=0;a<16;a++) {
				if(useless_action(a, next))
					continue;
				MarioState ms = next.next(a, next.ws);
				ms.pred = next;

				if(ms.dead) {
					continue;
				}

				float h = cost(ms, initialState);
				ms.g = next.g + Tunables.GIncrement;
				ms.cost = ms.g + h + ((a/MarioState.ACT_JUMP)>0?Tunables.FeetOnTheGroundBonus:0);

				if (h < 0.1f)
					continue;
				/*
				if(h < 0.1f) {
					pq.clear();
					if(verbose1) {
						System.out.printf("BestFirst: searched %d iterations (%d states); best a=%d cost=%f lookahead=%f\n", 
								n, pq_siz, ms.root_action, ms.cost, ms.g);
					}
					MarioState s;
					if(GlobalOptions.MarioPosSize > 400-46)
						GlobalOptions.MarioPosSize = 400-46;
					if (drawPath)
					{
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
						}
					Tunables.PathFound++;
					return ms.root_action;
				}
				*/
				pq.offer(ms);
				pq_siz++;
				bestfound = marioMin(ms,bestfound);
			}
			if (drawPath)
				GlobalOptions.MarioLines.Push(line1);
		}

		if (!pq.isEmpty())
			bestfound = marioMin(pq.poll(), bestfound);
		if(verbose1) {
			System.out.printf("BestFirst: giving up on search; best root_action=%d cost=%f lookahead=%f\n",
					bestfound.root_action, bestfound.cost, bestfound.g);
		}
		// return best so far
		pq.clear();
		return bestfound.root_action;

	}
	
}
