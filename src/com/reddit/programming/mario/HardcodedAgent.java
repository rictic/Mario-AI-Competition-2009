package com.reddit.programming.mario;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;
import ch.idsia.mario.engine.GlobalOptions;

//Based on ForwardAgent

public class HardcodedAgent extends RegisterableAgent implements Agent
{
	private boolean[] action;
	private int jumpCounter = 0;
	protected int[] marioPosition = null;
	protected Sensors sensors = new Sensors();
	
	public HardcodedAgent()
	{
		super("HardcodedAgent");
		action = new boolean[Environment.numberOfButtons];
		reset();
	}

	@Override
	public void reset()
	{
		action[Mario.KEY_RIGHT] = true;
		action[Mario.KEY_SPEED] = true;
	}

	protected boolean DangerOfGap(byte[][] levelScene)
	{
		for (int x = 9; x < 13; ++x)
		{
			boolean f = true;
			for(int y = 12; y < 22; ++y)
			{
				if  (levelScene[y][x] != 0)
					f = false;
			}
			if (f && levelScene[12][11] != 0)
				return true;
		}
		return false;
	}

	@Override
	public boolean[] getAction(Environment observation)
	{
		sensors.updateReadings(observation);
		
		
		if ((GlobalOptions.FPS != GlobalOptions.InfiniteFPS) && GlobalOptions.GameVeiwerOn)
			System.out.println(sensors);
		
		if (sensors.levelScene[11][13] != 0 || sensors.levelScene[11][12] != 0 ||  DangerOfGap(sensors.levelScene))
		{
			if (observation.mayMarioJump() || ( !observation.isMarioOnGround() && action[Mario.KEY_JUMP]))
			{
				action[Mario.KEY_JUMP] = true;
			}
			++jumpCounter;
		}
		else if (dangerousEnemies(sensors.enemiesScene)) {
			action[Mario.KEY_JUMP] = true;
			++jumpCounter;
		}
		else {
			action[Mario.KEY_JUMP] = false;
			jumpCounter = 0;
		}

		if (jumpCounter > 32)
		{
			jumpCounter = 0;
			action[Mario.KEY_JUMP] = false;
		}

		action[Mario.KEY_SPEED] = sensors.fireballsOnscreen == 0;
		if (oneIn(10))
			action[Mario.KEY_SPEED] = !action[Mario.KEY_SPEED];
		if (oneIn(100))
			action[Mario.KEY_JUMP] = !action[Mario.KEY_JUMP];
		return action;
	}
	
	private boolean dangerousEnemies(byte[][] enemiesScene) {
		int y = marioPosition[0];
		int x = marioPosition[1];
		if (isDangerous(enemiesScene[y][x])
		  ||isDangerous(enemiesScene[y][x+1])
		  ||isDangerous(enemiesScene[y][x+2]))
			return true;
		return false;
	}
	
	private boolean isDangerous(byte enemy) {
		switch(enemy) {
			case Sensors.MARIO:
			case Sensors.BLANK:
			case Sensors.FIREFLOWER:
			case Sensors.FIREBALL: return false;
			default: return true;
		}
	}
	
	private boolean oneIn(int num) {
		return ((int)(Math.random () * num)) == 1;
	}
	

}
