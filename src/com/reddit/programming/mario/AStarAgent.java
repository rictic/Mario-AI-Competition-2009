package com.reddit.programming.mario;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;
import ch.idsia.mario.engine.GlobalOptions;

import java.util.List;
import java.util.LinkedList;

//Based on ForwardAgent

class Path {
	public LinkedList<MarioState> states;
	public double costSoFar = 0;
	public double estimatedTotalCost = 0;

	public void print() {
		System.out.print("Path: " + states.toString());
	}

	public void append(MarioState state) {
		states.add(state);
	}

	public MarioState pop() {
		return null;
		//return states.pop();
	}

	public Path() {
		states = new LinkedList<MarioState>();
	}

	public Path(LinkedList<MarioState> s) {
		states = s;
	}
}

public class AStarAgent extends RegisterableAgent implements Agent
{
	MarioState ms;
	protected Sensors sensors = new Sensors();

	public AStarAgent()
	{
		super("AStarAgent");
	}

	@Override
	public void reset()
	{

	}

	@Override
	public boolean[] getAction(Environment observation)
	{
		sensors.updateReadings(observation);

		float[] mpos = observation.getMarioFloatPos();

		if(ms == null) {
			// assume one frame of falling before we get an observation :(
			ms = new MarioState(mpos[0], mpos[1], 0.0f, 3.0f);
		} else {
			//System.out.println(String.format("mario x,y=(%5.1f,%5.1f)", mpos[0], mpos[1]));
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

		MarioState[] result = new MarioState[] {
				ms.next(12, sensors.levelScene, mX, mY),
				ms.next(8, sensors.levelScene, mX, mY),
				ms.next(4, sensors.levelScene, mX, mY),
				ms.next(2, sensors.levelScene, mX, mY) //extend, decide on state, etc.
				// for example: if jumping, we can simply go on...
		};
		return result;
	}

	private double costLeftHeuristics(MarioState curr, double xRightOfScreen) {
		return xRightOfScreen - curr.x;
	}

	private int costToNextState(MarioState state1, MarioState state2) {
		return 1; // a bit more sophistication ;0
	}

	private Path findPath(MarioState s, Path[] ps) {
		for(Path p : ps) {
			// s == state.getFirst(), then return...
		}
		return null;
	}

	// A* search algorithm
	private Path search(Path[] startPaths, int j, double xRightOfScreen)
	// j: index of currently handles item startPaths[j]
	{
		if (startPaths.length == 0) {
			System.out.println("No feasible solution, abort.");
			System.exit(1);
		}

		// TODO: check if startPaths[j] is feasibe and either take it, or push it into a list

		Path path = startPaths[j];
		MarioState state = path.states.getFirst();

		for(MarioState state2 : successors(state)) {
			double cost = path.costSoFar + costToNextState(state, state2);
			double cost2 = costLeftHeuristics(state2, xRightOfScreen);
			Path path2 = new Path(new LinkedList<MarioState>(path.states));
			path2.costSoFar = cost;
			path2.estimatedTotalCost = cost + cost2;
			path2.append(state2);
		}



		return null;
	}
}
