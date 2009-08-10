package com.reddit.programming.mario;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.GlobalOptions;

public class RedditAgent extends RegisterableAgent implements Agent
{
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
}
