package com.reddit.programming.mario;

import java.util.PriorityQueue;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.GlobalOptions;

public final class BudgetingBestFirstAgent extends HeuristicSearchingAgent implements Agent
{
	private static final int simultaneousSearchers = Runtime.getRuntime().availableProcessors();
	private ExecutorService searchPool = Executors.newFixedThreadPool(simultaneousSearchers);
	private StateSearcher[] searchers = new StateSearcher[simultaneousSearchers];
	private Stack<Integer> decisions = new Stack<Integer>();
	private static final int budgetPerFrame = 30; //time, in milliseconds, that we can think per frame
	private long budget = 0;  //time, in milliseconds, that we can spend planning
	
	MarioState ms = null, ms_prev = null;
	WorldState ws = null;
	float pred_x, pred_y;

	public BudgetingBestFirstAgent() {
		super("BudgetingBestFirstAgent");
		reset();
	}


//	private PriorityQueue<MarioState> prune_pq() {
		// first, swap pq2 and pq
//		PriorityQueue<MarioState> p = pq; pq = pq2; pq2 = p;
//		while(!pq2.isEmpty() && pq.size() < maxBreadth/2)
//			pq.add(pq2.remove());
//		pq2.clear();
//		return pq;
//		return null;
//	}

	@Override
	protected int searchForAction(MarioState initialState, WorldState ws) {
		budget += budgetPerFrame; //we get a frame's worth of time
		if (!decisions.isEmpty())
			return decisions.pop();	
		PriorityQueue<MarioState> pq = new PriorityQueue<MarioState>(20, msComparator);
		int i = 0;
		initialState.ws = ws;
		initialState.g = 0;
		initialState.cost = cost(initialState, initialState);
		initialState.pred = null;
		initialState.dead = false;
		// add initial set
		for(int a=1;a<16;a++) {
			if(useless_action(a, initialState))
				continue;
			MarioState ms = initialState.next(a, ws);
			ms.root_action = a;
			ms.cost = 1 + cost(ms, initialState);
			pq.add(ms);
			if(verbose2)
				System.out.printf("BestFirst: root action %d initial cost=%f\n", a, ms.cost);
		}
		Object notificationObject = new Object();
		PriorityQueue<MarioState>[] pqs = new PriorityQueue[searchers.length];
		//System.out.println("creating searchers");
		for (i = 0; i < pqs.length; i++) pqs[i] = new PriorityQueue<MarioState>(20, msComparator);
		i = 0;
		GlobalOptions.MarioPosSize = 0;
		while (!pq.isEmpty())
			pqs[i++%pqs.length].add(pq.remove());
		
		for (i = 0; i < searchers.length; i++)
			searchers[i] = new StateSearcher(initialState, ws, pqs[i], i,notificationObject);
		for (StateSearcher searcher : searchers)
			searchPool.execute(searcher);
		
		MarioState bestfound = null;
		long totalElapsed = 0;
		try {
			synchronized(notificationObject) {
				while ( //we have time left to spend
						budget > 0 &&
						//either we've found nothing, we've only searched three steps out,
						//  or the best option we can find is terrible
						(bestfound == null || bestfound.g <= 3 || bestfound.cost > 1000)
						// and we haven't spent more than three seconds on this frame
						//  (spending more is unlikely to be helpful)
						&& totalElapsed < 3000) {
					
					long startTime = System.currentTimeMillis();
					notificationObject.wait(Math.min(Math.max(5,budget), 200));
					long timeElapsed = System.currentTimeMillis() - startTime;
					totalElapsed += timeElapsed;
					budget -= timeElapsed; 
					for (StateSearcher searcher: searchers)
						bestfound = marioMin(searcher.bestfound, bestfound);
				}
			}
		} catch (InterruptedException e) {throw new RuntimeException("Interrupted from sleep searching for the best action");}
		
		
		for (StateSearcher searcher: searchers)
			searcher.stop();
		for (StateSearcher searcher: searchers)
			while(!searcher.isStopped){}
		
//			if (verbose1)
//				System.out.printf("searcher_(%d): best root_action=%s cost=%f lookahead=%f\n",
//						searcher.id, actionToString(bestfound.root_action), bestfound.cost, bestfound.g);


//		addLine(bestfound, 0xffffff);
		
		//not good enough, search again next frame
		if (bestfound.cost > 40){
			decisions.push(bestfound.action);
			return searchForAction(null,null);
		}
			
		
		while(bestfound.pred != null){
			decisions.push(bestfound.action);
			bestfound = bestfound.pred;
		}
		int desiredSize = Math.max(5, Math.min(10, decisions.size() / 2));
		while(decisions.size() > desiredSize) decisions.remove(0);
		
//		if (verbose1){
//			System.out.println("Decisions made:");
//			System.out.println(decisionsToString(decisions));
//		}
		
		if (decisions.empty()){
			if (verbose1)
				System.err.println("NO PLAN FOUND?");
			return bestfound.root_action;
		}

		// return best so far
		return searchForAction(null,null);
	}


	private class StateSearcher implements Runnable {
		private final PriorityQueue<MarioState> pq;
		private final MarioState initialState;
		private final WorldState ws;
		private final int id;
		private boolean shouldStop = false;
		public boolean isStopped = false;
		private MarioState bestfound;
		private int DrawIndex = 0;
		private Object notificationObject;
		
		public StateSearcher(MarioState initialState, WorldState ws, PriorityQueue<MarioState> pq, int id, Object notificationObject) {
			this.pq = pq; this.ws = ws; 
			this.initialState = initialState; this.bestfound = null;
			this.id = id; DrawIndex = id;
			this.notificationObject = notificationObject;
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
//				if(pq.size() > maxBreadth)
//					pq = prune_pq();
			
				MarioState next = pq.remove();

//				int color = (int) Math.min(255, 10000*Math.abs(next.cost - next.pred.cost));
//				color = color|(color<<8)|(color<<16);
//				addLine(next.x, next.y, next.pred.x, next.pred.y, color);
				// next.cost can be infinite, and still at the head of the queue,
				// if the node got marked dead
				if(next.cost == Float.POSITIVE_INFINITY) continue;


				bestfound = marioMin(next,bestfound);
				for(int a=1;a<16;a++) {
					if(useless_action(a, next))
						continue;
					MarioState ms = next.next(a, next.ws);

					if (DrawIndex >= 400)
					{
						DrawIndex = 0;
					}

					if(ms.dead) continue;
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
					if(h < 0.1f) {
						if(verbose1) {
//							System.out.printf("BestFirst: searched %d iterations; best a=%s cost=%f lookahead=%f\n", 
//									n, actionToString(ms.root_action), ms.cost, ms.g);
							MarioState s;
							for(s = ms;s != initialState;s = s.pred) {
								if(verbose2) {
									System.out.printf("state %d: ", (int)s.g);
									s.print();
								}
							}
						}
						bestfound = ms;
						synchronized(notificationObject) {notificationObject.notify();}
						return;
					}
					pq.add(ms);
				}
			}
		}

		private void addToDrawPath(MarioState mario) {
			GlobalOptions.MarioPos[DrawIndex] = new int[]{(int)mario.x, (int)mario.y, costToTransparency(mario.cost)};
			DrawIndex += simultaneousSearchers;
			if (DrawIndex >= 400)
				DrawIndex = id;
		}
	}
}
