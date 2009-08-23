package com.reddit.programming.mario;

import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.idsia.ai.agents.Agent;

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
			searchers[i] = new StateSearcher(this, initialState, ws, pqs[i], i, notificationObject);
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
}
