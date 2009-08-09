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
        MarioState ms;
	
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
		marioPosition = sensors.getMarioPosition();
                float[] mpos = observation.getMarioFloatPos();
                if(ms == null) {
                  // assume one frame of falling before we get an observation :(
                  ms = new MarioState(mpos[0], mpos[1], 0.0f, 3.0f);
                }
                System.out.println(String.format("mario x,y=(%5.1f,%5.1f)", mpos[0], mpos[1]));
		
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
		
		action[Mario.KEY_SPEED] = sensors.fireballsOnScreen == 0;
		if (oneIn(10))
			action[Mario.KEY_SPEED] = !action[Mario.KEY_SPEED];
		if (oneIn(100))
			action[Mario.KEY_JUMP] = !action[Mario.KEY_JUMP];
                int _act_token = (action[Mario.KEY_SPEED] ? 1 : 0) |
                  (action[Mario.KEY_RIGHT] ? 2 : 0) |
                  (action[Mario.KEY_JUMP] ? 4 : 0) |
                  (action[Mario.KEY_LEFT] ? 8 : 0);
                ms = ms.next(_act_token, sensors.levelScene);
                System.out.println(String.format("action: %d; predicted x,y=(%5.1f,%5.1f) xa,ya=(%5.1f,%5.1f)",
                      _act_token, ms.x, ms.y, ms.xa, ms.ya));
		return action;
	}
	
	private boolean dangerousEnemies(byte[][] enemiesScene) {
		int y = marioPosition[0];
		int x = marioPosition[1];
		if (sensors.isDangerous(enemiesScene[y][x])
		  ||sensors.isDangerous(enemiesScene[y][x+1])
		  ||sensors.isDangerous(enemiesScene[y][x+2]))
			return true;
		return false;
	}
	
	private boolean oneIn(int num) {
		return ((int)(Math.random () * num)) == 1;
	}
	

}
