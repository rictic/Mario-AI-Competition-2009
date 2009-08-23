/**
 * 
 */
package com.reddit.programming.mario;

import java.util.PriorityQueue;

import ch.idsia.mario.engine.GlobalOptions;

class StateSearcher implements Runnable {
		private final ThreadedBestFirstAgent heuristicSearchingAgent;
		private final PriorityQueue<MarioState> pq;
		private final MarioState initialState;
//		private final WorldState ws;
		final int id;
		private boolean shouldStop = false;
		public  volatile boolean isStopped = false;
		MarioState bestfound;
		private int DrawIndex = 0;
		private Object notificationObject;
		
		public StateSearcher(ThreadedBestFirstAgent threadedBestFirstAgent, MarioState initialState, WorldState ws, PriorityQueue<MarioState> pq, int id, Object notificationObject) {
			heuristicSearchingAgent = threadedBestFirstAgent;
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

					float h = heuristicSearchingAgent.cost(ms, initialState);
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
