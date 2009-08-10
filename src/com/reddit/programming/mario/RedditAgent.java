package com.reddit.programming.mario;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.GlobalOptions;

public class RedditAgent extends RegisterableAgent implements Agent
{
	private ASCIIFrame asciiFrame = null;
	private final boolean useASCIIFrame = (GlobalOptions.FPS != GlobalOptions.InfiniteFPS) && GlobalOptions.GameVeiwerOn;
	
	public RedditAgent(String name)
	{
		super(name);
//		if (useASCIIFrame)
//			asciiFrame = new ASCIIFrame();
	}

	public void UpdateMap(Sensors sensors)
	{
//		if (useASCIIFrame) {
//			asciiFrame.tick();
//			asciiFrame.Update(sensors.toString(), GlobalOptions.getMarioComponent());
//		}


	}
}
