package com.reddit.programming.mario;

import java.awt.Color;
import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.GlobalOptions;

public final class BestFirstAgent extends HeuristicSearchingAgent implements Agent
{
	private PrioQ pq;
	private static final int maxSteps = 1000;

	public BestFirstAgent() {
		super("Tuned BestFirstAgent");
	}

	@Override
	public void reset() {
		super.reset();
		pq = new PrioQ(Tunables.MaxBreadth);
	}



	@Override
	protected int searchForAction(MarioState initialState, WorldState ws) {
		DebugPolyLine line1;
		pq.clear();
		GlobalOptions.MarioPosSize = 0;

		initialState.ws = ws;
		initialState.g = 0;
		initialState.dead = false;
		
		float threshold = 1e10f;

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
		boolean goalFound = false;
		int pq_siz=0;
		for(n=0;n<maxSteps && !pq.isEmpty();n++) {
			MarioState next = pq.poll();

			if (drawPath) {
				line1 = new DebugPolyLine(Color.BLUE);
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

				if(ms.dead) continue;

				float h = cost(ms, initialState);
				ms.g = next.g + Tunables.GIncrement;
				ms.cost = ms.g + h + ((a/MarioState.ACT_JUMP)>0?Tunables.FeetOnTheGroundBonus:0);

				bestfound = marioMin(ms,bestfound);

				if (h < 0.1f)
				{
					if (!goalFound)
						Tunables.PathFound++;
					goalFound = true;
					if (h < threshold)
						threshold = h;
					continue;
				}				
				if (ms.cost < threshold)
					pq.offer(ms);
				pq_siz++;
			}
			if (drawPath)
				GlobalOptions.MarioLines.Push(line1);
		}

		if (!pq.isEmpty())
			bestfound = marioMin(pq.poll(), bestfound);
		if(verbose2) {
			System.out.printf("BestFirst: giving up on search; best root_action=%d cost=%f lookahead=%f\n",
					bestfound.root_action, bestfound.cost, bestfound.g);
		}
		// return best so far
		pq.clear();
		return bestfound.root_action;
	}
}
