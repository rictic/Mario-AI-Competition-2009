package com.reddit.programming.mario;

import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.idsia.ai.agents.Agent;
import ch.idsia.mario.engine.GlobalOptions;

public class ThreadedBestFirstAgent extends HeuristicSearchingAgent implements Agent {
	public static final int simultaneousSearchers = Runtime.getRuntime().availableProcessors();
	private ExecutorService searchPool = Executors.newFixedThreadPool(simultaneousSearchers);
	protected StateSearcher[] searchers = new StateSearcher[simultaneousSearchers];
	
	public ThreadedBestFirstAgent(String name) {
		super(name);
	}
	
	public ThreadedBestFirstAgent() {
		super("ThreadedBestFirstAgent");
	}

	protected final PriorityQueue<MarioState> getInitialPriorityQueue(MarioState initialState, WorldState ws) {
		bestfound = null;
		PriorityQueue<MarioState> pq = new PriorityQueue<MarioState>(20, msComparator);
		initialState.ws = ws;
		initialState.g = 0;
		initialState.dead = false;
		initialState.pred = null;
		// add initial set
		for(int a=0;a<16;a++) {
			if(useless_action(a, initialState))
				continue;
			MarioState ms = initialState.next(a, ws);
			ms.root_action = a;
			ms.cost = 1 + cost(ms, initialState);
			pq.add(ms);
			if(verbose2)
				System.out.printf("BestFirst: root action %d initial cost=%f\n", a, ms.cost);
		}
		return pq;
	}
	protected final void initializeSearchers(MarioState initialState, WorldState ws, Object notificationObject) {
		PriorityQueue<MarioState> pq = getInitialPriorityQueue(initialState, ws);
		PriorityQueue<MarioState>[] pqs = new PriorityQueue[searchers.length];
		//System.out.println("creating searchers");
		for (int i = 0; i < pqs.length; i++) pqs[i] = new PriorityQueue<MarioState>(20, msComparator);
		int i = 0;
		while (!pq.isEmpty())
			pqs[i++%pqs.length].add(pq.remove());
		
		for (i = 0; i < searchers.length; i++){
			searchers[i] = new StateSearcher(initialState, ws, pqs[i], i, notificationObject);
			searchPool.execute(searchers[i]);
		}
	}
	
	protected MarioState bestfound;
	protected final MarioState getBestFound() {
		for (StateSearcher searcher: searchers) {
			bestfound = marioMin(searcher.bestfound, bestfound);

			if (verbose1)
				System.out.printf("searcher_(%d): best root_action=%d cost=%f lookahead=%f\n",
						searcher.id, bestfound.root_action, bestfound.cost, bestfound.g);
		}
		return bestfound;
	}
	
	protected final void stopSearchers() {
		for (StateSearcher searcher: searchers)
			searcher.stop();
		for (StateSearcher searcher: searchers)
			while(!searcher.isStopped){}
	}
	
	protected int searchForAction(MarioState initialState, WorldState ws) {
		Object notificationObject = new Object();
		initializeSearchers(initialState, ws, notificationObject);
		try {
			synchronized(notificationObject){
				notificationObject.wait(25);
			}
		} catch (InterruptedException e) {throw new RuntimeException("Interrupted from sleep searching for the best action");}
		stopSearchers();
			
		// return best so far
		return getBestFound().root_action;
	}
	
	private class StateSearcher implements Runnable {
		private final PriorityQueue<MarioState> pq;
		private final MarioState initialState;
//		private final WorldState ws;
		final int id;
		private boolean shouldStop = false;
		public  volatile boolean isStopped = false;
		MarioState bestfound;
		private int DrawIndex = 0;
		private Object notificationObject;
		
		public StateSearcher(MarioState initialState, WorldState ws, PriorityQueue<MarioState> pq, int id, Object notificationObject) {
			this.pq = pq; //this.ws = ws; 
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

				// next.cost can be infinite, and still at the head of the queue,
				// if the node got marked dead
				if(next.cost == Float.POSITIVE_INFINITY) continue;

				if(ThreadedBestFirstAgent.drawPath)
					addToDrawPath(next.pred);

				bestfound = ThreadedBestFirstAgent.marioMin(next,bestfound);
				for(int a=0;a<16;a++) {
					if(HeuristicSearchingAgent.useless_action(a, next))
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
					if(h <= 0) {
						if(ThreadedBestFirstAgent.verbose1) {
							System.out.printf("BestFirst: searched %d iterations; best a=%d cost=%f lookahead=%f\n", 
									n, ms.root_action, ms.cost, ms.g);
						}
						if(ThreadedBestFirstAgent.verbose2) {
							MarioState s;
							for(s = ms;s != initialState;s = s.pred) {
								System.out.printf("state %d: ", (int)s.g);
								s.print();
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
			GlobalOptions.MarioPos[DrawIndex] = new int[]{(int)mario.x, (int)mario.y, ThreadedBestFirstAgent.costToTransparency(mario.cost)};
			DrawIndex += ThreadedBestFirstAgent.simultaneousSearchers;
			if (DrawIndex >= 400)
				DrawIndex = id;
		}
	}

}
