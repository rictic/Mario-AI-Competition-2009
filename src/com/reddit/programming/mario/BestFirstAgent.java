package com.reddit.programming.mario;

import java.awt.Color;
import java.util.PriorityQueue;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.GlobalOptions;

public final class BestFirstAgent extends HeuristicSearchingAgent implements Agent
{
	private PriorityQueue<MarioState> pq, pq2;
	private static final int maxBreadth = 63;
	private static final int maxSteps = 500;

	public BestFirstAgent() {
		super("BestFirstAgent");
		pq = new PriorityQueue<MarioState>(maxBreadth, msComparator);
		pq2 = new PriorityQueue<MarioState>(maxBreadth, msComparator);
	}

	private PriorityQueue<MarioState> prune_pq() {
		// first, swap pq2 and pq
		PriorityQueue<MarioState> p = pq; pq = pq2; pq2 = p;
		while(!pq2.isEmpty() && pq.size() < maxBreadth/2)
			pq.add(pq2.remove());
		pq2.clear();
		return pq;
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
			DebugPolyLine line1 = new DebugPolyLine(Color.BLUE);
			MarioState next = pq.remove();

			int color = (int) Math.min(255, 10000*Math.abs(next.cost - next.pred.cost));
			color = color|(color<<8)|(color<<16);
			addLine(next.x, next.y, next.pred.x, next.pred.y, color);
			line1.AddPoint(next.x, next.y);
			line1.AddPoint(next.pred.x, next.pred.y);
			line1.color = new Color(color);

			//System.out.printf("a*: trying "); next.print();
			for(a=0;a<16;a++) {
				if(useless_action(a, next))
					continue;
				MarioState ms = next.next(a, next.ws);
				ms.pred = next;

				if(ms.dead) continue;

				float h = cost(ms, initialState);
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
				pq_siz++;
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
	
}
