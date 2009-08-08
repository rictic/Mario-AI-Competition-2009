package ch.idsia.ai.agents.ai;

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
		byte[][] levelScene = observation.getLevelSceneObservation();
//		float[] marioPos = observation.getMarioFloatPos();
//		float[] enemiesPos = observation.getEnemiesFloatPos();
		byte[][] enemiesScene = observation.getEnemiesObservation();
		
		String[][] scene = new String[Environment.HalfObsWidth*2][Environment.HalfObsHeight*2];
		boolean fireballDetected = false;
		
		for (int y = 0; y < levelScene.length; ++y)
			for (int x = 0; x < levelScene[0].length; ++x)
				scene[y][x] = asciiLevel(levelScene[y][x]);
		for (int y = 0; y < enemiesScene.length; ++y)
			for (int x = 0; x < enemiesScene[0].length; ++x){
				byte enemy = enemiesScene[y][x];
				if (enemy == BLANK)
					continue;
				if (enemy == MARIO)
					marioPosition = new int[]{y,x};
				if (enemy == FIREBALL)
					fireballDetected = true;
				scene[y][x] = asciiEnemy(enemy);
			}
		
		if (GlobalOptions.GameVeiwerOn)
			for (String[] sceneRow : scene){
				for(String square : sceneRow)
					System.out.print(square + " ");
				System.out.println();
			}

		if (levelScene[11][13] != 0 || levelScene[11][12] != 0 ||  DangerOfGap(levelScene))
		{
			if (observation.mayMarioJump() || ( !observation.isMarioOnGround() && action[Mario.KEY_JUMP]))
			{
				action[Mario.KEY_JUMP] = true;
			}
			++jumpCounter;
		}
		else if (dangerousEnemies(enemiesScene)) {
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

		action[Mario.KEY_SPEED] = !fireballDetected;
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
			case MARIO:
			case BLANK:
			case FIREFLOWER:
			case FIREBALL: return false;
			default: return true;
		}
	}
	
	private final static int EMPTY = 0;
	private final static int COIN = 34;
	private final static int SOLID = -10;
	private final static int PLATFORM = -11;
	private final static int PIPE = 20;
	private final static int COIN_QUESTIONMARK_BOX = 21;
	private final static int ITEM_QUESTIONMARK_BOX = 22;
	private final static int COIN_BRICK = 17;
	private final static int ITEM_BRICK = 18;
	private final static int BRICK = 16;

	private final static int MARIO = 1;
	private String asciiLevel(byte levelSquare) {
		switch(levelSquare) {
			case EMPTY: return " ";
			case COIN: return "O";
			case SOLID: return "X";
			case PLATFORM: return "-";
			case PIPE: return "P";
			case BRICK: return "B";
			case COIN_QUESTIONMARK_BOX:
			case ITEM_QUESTIONMARK_BOX:
			case COIN_BRICK:
			case ITEM_BRICK: return "?";
			case MARIO: return "M";
			default: return ""+levelSquare;
		}
	}
	
	private final static int BLANK = -1;
	private final static int GOOMBA = 2;
	private final static int FIREFLOWER = 15;
	private final static int FIREBALL = 25;
	private String asciiEnemy(byte enemySquare) {
		switch(enemySquare) {
			case MARIO: return "M";
			case GOOMBA: return "G";
			case FIREFLOWER: return "F";
			case FIREBALL: return "*";
			default: return ""+enemySquare;
		}
	}
}
