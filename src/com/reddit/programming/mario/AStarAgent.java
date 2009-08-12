package com.reddit.programming.mario;

import java.util.concurrent.PriorityBlockingQueue;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.environments.Environment;

//This code is very early,
//currently it doesn't work at all


class Path {
	public PriorityBlockingQueue<MarioState> states;
	public double costSoFar = 0;
	public double estimatedTotalCost = 0;

	public void print() {
		System.out.print("Path: " + states.toString());
	}

	public void append(MarioState state) {
		states.add(state);
	}

	public MarioState pop() {
		return states.remove();
	}

	public Path() {
		states = new PriorityBlockingQueue<MarioState>();
	}
}

public class AStarAgent extends RegisterableAgent implements Agent {
	MarioState ms;
	protected Sensors sensors = new Sensors();

	public AStarAgent() {
		super("AStarAgent");
	}

	@Override
	public void reset() {

	}

	@Override
	public boolean[] getAction(Environment observation) {
		sensors.updateReadings(observation);

		float[] mpos = observation.getMarioFloatPos();

		if(ms == null) {
			// assume one frame of falling before we get an observation :(
			ms = new MarioState(mpos[0], mpos[1], 0.0f, 3.0f);
		}

		Path p = new Path();

		p.append(ms);

		for(MarioState s : successors(ms)) {
			System.out.println(s.toString());
		}
		//p.print();

		search(new Path[]{p}, 0, mpos[0] + 11*16.0);

		return new boolean[]{true, false, false, false, false};
	}

	private MarioState[] successors(MarioState ms) {
		// quantize mario's position to get the map origin
		int mX = (int)ms.x/16 - 11;
		int mY = (int)ms.y/16 - 11;

//		MarioState[] result = new MarioState[] {
//				ms.next(12, sensors.levelScene, mX, mY),
//				ms.next(8, sensors.levelScene, mX, mY),
//				ms.next(4, sensors.levelScene, mX, mY),
//				ms.next(2, sensors.levelScene, mX, mY) //extend, decide on state, etc.
//				// for example: if jumping, we can simply go on...
//		};
//		return result;
		return null;
	}

	private double costLeftHeuristics(MarioState curr, double xRightOfScreen) {
		return xRightOfScreen - curr.x;
	}

	private int costToNextState(MarioState state1, MarioState state2) {
		int cost = 1; //it costs us one timeunit's worth of action to get here
		//Add in huge penalties for taking damage or falling off a cliff here
		
		//A penalty for being above an open pit followed by a reward for reaching the
		//other side could be helpful
		
		//Add in the distance to the right-hand side of the screen
		
		return cost; 
	}

	private Path findPath(MarioState s, Path[] ps) {
		for(Path p : ps) {
			// s == state.getFirst(), then return...
		}
		return null;
	}

	// A* search algorithm
	// j: index of currently handles item startPaths[j]
	private Path search(Path[] startPaths, int j, double xRightOfScreen) {
		if (startPaths.length == 0)
			throw new IllegalStateException("No feasible solution.");

		// TODO: check if startPaths[j] is feasibe and either take it, or push it into a list

		Path path = startPaths[j];
		MarioState state = path.pop();

		for(MarioState state2 : successors(state)) {
			double cost = path.costSoFar + costToNextState(state, state2);
			double cost2 = costLeftHeuristics(state2, xRightOfScreen);
//			Path path2 = new Path(new LinkedList<MarioState>(path.states));
//			path2.costSoFar = cost;
//			path2.estimatedTotalCost = cost + cost2;
//			path2.append(state2);
		}



		return null;
	}
}
