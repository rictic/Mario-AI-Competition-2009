package com.reddit.programming.mario;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;

public class RedditAgent extends RegisterableAgent implements Agent
{
	private ASCIIFrame asciiFrame;
	
	public RedditAgent(String s)
	{
		super(s);
		// TODO Auto-generated constructor stub

		asciiFrame = new ASCIIFrame();
	}

	@Override
	public void reset()
	{
	}

	@Override
	public boolean[] getAction(Environment observation)
	{
		return null;
	}
	public void UpdateMap(Sensors sensors)
	{
		if ((GlobalOptions.FPS != GlobalOptions.InfiniteFPS) && GlobalOptions.GameVeiwerOn)
			asciiFrame.Update(sensors.toString(), GlobalOptions.getMarioComponent());

		asciiFrame.tick();
	}
}
