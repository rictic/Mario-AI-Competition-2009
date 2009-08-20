package com.reddit.programming.mario;

import java.util.Stack;

import ch.idsia.ai.agents.Agent;

public final class BudgetingBestFirstAgent extends ThreadedBestFirstAgent implements Agent
{
	private Stack<Integer> decisions = new Stack<Integer>();
	private static final int budgetPerFrame = 30; //time, in milliseconds, that we can think per frame
	private long budget = 0;  //time, in milliseconds, that we can spend planning
	
	public BudgetingBestFirstAgent() {
		super("BudgetingBestFirstAgent");
	}


	@Override
	protected int searchForAction(MarioState initialState, WorldState ws) {
		budget += budgetPerFrame; //we get a frame's worth of time
		if (!decisions.isEmpty())
			return decisions.pop();	
		Object notificationObject = new Object();
		initializeSearchers(initialState, ws, notificationObject);
		
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
					bestfound = getBestFound();
				}
			}
		} catch (InterruptedException e) {throw new RuntimeException("Interrupted from sleep searching for the best action");}
		
		stopSearchers();
		
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



}
