package com.reddit.programming.mario;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.GlobalOptions;

public class RedditAgent extends RegisterableAgent implements Agent
{
	protected static final int ACT_SPEED = 1;
	protected static final int ACT_RIGHT = 2;
	protected static final int ACT_JUMP = 4;
	protected static final int ACT_LEFT = 8;

	private ASCIIFrame asciiFrame;
	
	public RedditAgent(String s)
	{
		super(s);
		asciiFrame = new ASCIIFrame();
	}

	public void UpdateMap(Sensors sensors)
	{
		if ((GlobalOptions.FPS != GlobalOptions.InfiniteFPS) && GlobalOptions.GameVeiwerOn)
			asciiFrame.Update(sensors.toString(), GlobalOptions.getMarioComponent());

		asciiFrame.tick();
	}
	
	public String actionToString(int action) {
		if (action == 0 ) return "NONE";
		String result = "";
		if ((action & ACT_LEFT ) > 0) result += "L";
		if ((action & ACT_RIGHT) > 0) result += "R";
		if ((action & ACT_JUMP ) > 0) result += "J";
		if ((action & ACT_SPEED) > 0) result += "S";
		return result;
	}
}
