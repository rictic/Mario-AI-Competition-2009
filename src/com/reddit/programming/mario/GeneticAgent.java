package com.reddit.programming.mario;

import java.util.Random;
import ch.idsia.ai.ea.CrossoverEvolvable;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;
import com.reddit.programming.mario.Sensors;

public class GeneticAgent extends RegisterableAgent implements Agent, CrossoverEvolvable
{
	private double score;
	private boolean[] genes;
	private Sensors sensors = new Sensors();
	private int jumpCounter = 0;
	protected int[] marioPosition = null;
	private int maxJumpCount;
	
	public static final Random random = new Random();
	
	private static final int    numberOfCases = 6;
	private static final int    numberOfButtons = Environment.numberOfButtons * GeneticAgent.numberOfCases;
//	private static final double probOfMutation = 0.02;
	
	private static final int CASE_DEFAULT        = 0;
	private static final int CASE_WALL           = 1;
	private static final int CASE_ENEMIES        = 2;
	private static final int CASE_FIREBALLS      = 3;
	private static final int CASE_NO_FIREBALLS   = 4;
	private static final int CASE_JUMP_COUNT     = 5;
	
	public GeneticAgent()
	{
		super("GeneticAgent");
		action = new boolean[Environment.numberOfButtons];
		genes  = new boolean[GeneticAgent.numberOfButtons];
		maxJumpCount = GeneticAgent.random.nextInt();
		
		for (int i = 0; i < GeneticAgent.numberOfButtons; i++)
		{
			this.genes[i] = GeneticAgent.random.nextBoolean();
		}
	}
	
	public GeneticAgent(int randSeed)
	{
		super("GeneticAgent");
		
		// Allocate genes and action arrays
		action = new boolean[Environment.numberOfButtons];
		genes  = new boolean[GeneticAgent.numberOfButtons];
		
		// Randomly initialize genes
		GeneticAgent.random.setSeed(randSeed);
		
		for (int i = 0; i < GeneticAgent.numberOfButtons; i++)
		{
			genes[i] = GeneticAgent.random.nextBoolean();
		}
		
		// Reset this agent
		reset();
	}
	
	public CrossoverEvolvable getNewInstance()
	{
		return new GeneticAgent();
	}
	
	public CrossoverEvolvable copy()
	{
		GeneticAgent agent = new GeneticAgent();
		agent.score = this.score;
		agent.sensors = this.sensors;
		
		for (int i = 0; i < GeneticAgent.numberOfButtons; i++)
		{
			agent.genes[i] = this.genes[i];
		}
		
		return agent;
	}
	
	public void mutate()
	{
		int index = (int)(Math.random() * GeneticAgent.numberOfButtons);
		this.genes[index] = !this.genes[index];
		this.maxJumpCount = GeneticAgent.random.nextInt();
	}
	
	public CrossoverEvolvable[] crossover(CrossoverEvolvable other)
	{
		int randomIndex = (int)(Math.random() * genes.length);
		boolean temp;
		
		GeneticAgent[] child = new GeneticAgent[2];
		child[0] = (GeneticAgent)other.copy();
		child[1] = (GeneticAgent)this.copy();
		
		if (GeneticAgent.random.nextBoolean())
		{
			for (int i = randomIndex; i < genes.length; i++)
			{
				temp = child[0].genes[i];
				child[0].genes[i] = child[1].genes[i];
				child[1].genes[i] = temp;
			}
		}
		else
		{
			for (int i = 0; i < randomIndex; i++)
			{
				temp = child[0].genes[i];
				child[0].genes[i] = child[1].genes[i];
				child[1].genes[i] = temp;
			}
		}
		
		// Randomly crossover the bits in the maximum jump count
		randomIndex = GeneticAgent.random.nextInt(32);
		int mask = 0 | (1 << randomIndex);
		
		int jumpCountA = child[0].maxJumpCount & mask;
		int jumpCountB = child[1].maxJumpCount & mask;
		child[0].maxJumpCount = (child[0].maxJumpCount & ~mask) | jumpCountB;
		child[1].maxJumpCount = (child[1].maxJumpCount & ~mask) | jumpCountA;
		
		return child;
	}
	
	public void reset()
	{
		// Reset to the genomes default key configuration
		for (int i = 0; i < Environment.numberOfButtons; i++)
		{
			action[i] = genes[i];
		}
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
	
	private boolean dangerousEnemies(byte[][] enemiesScene) {
		int y = marioPosition[0];
		int x = marioPosition[1];
		if (sensors.isDangerous(enemiesScene[y][x])
		  ||sensors.isDangerous(enemiesScene[y][x+1])
		  ||sensors.isDangerous(enemiesScene[y][x+2]))
			return true;
		return false;
	}
	
	public void copyState(int caseValue)
	{
		int start = Environment.numberOfButtons * caseValue;
		int end = start + Environment.numberOfButtons;
		for (int i = start; i < end; i++)
		{
			action[(i % 5)] = genes[i];
		}
	}
	
	public boolean[] getAction(Environment observation)
	{
		sensors.updateReadings(observation);
		marioPosition = sensors.getMarioPosition();
		
		boolean wasJump = action[Mario.KEY_JUMP];
		
		if (sensors.levelScene[11][13] != 0 || sensors.levelScene[11][12] != 0 ||  DangerOfGap(sensors.levelScene))
		{
			if (observation.mayMarioJump() || ( !observation.isMarioOnGround() && action[Mario.KEY_JUMP]))
			{
				copyState(GeneticAgent.CASE_WALL);
			}
		}
		else if (dangerousEnemies(sensors.enemiesScene))
		{
			copyState(GeneticAgent.CASE_ENEMIES);
		}
		else
		{
			copyState(GeneticAgent.CASE_DEFAULT);
		}

		if (jumpCounter > this.maxJumpCount)
		{
			copyState(GeneticAgent.CASE_JUMP_COUNT);
		}
		if (sensors.fireballsOnScreen == 0)
		{
			copyState(GeneticAgent.CASE_NO_FIREBALLS);
		}
		else
		{
			copyState(GeneticAgent.CASE_FIREBALLS);
		}
		
		if (action[Mario.KEY_JUMP])
		{
			jumpCounter++;
		}
		else if(wasJump)
		{
			jumpCounter = 0;
		}
		
		return action;
	}

	public void setScore(double score)
	{
		this.score = score;
	}

	public double getScore()
	{
		return score;
	}
	
	public String toString()
	{
		String genome = "";
		for (int i = 0; i < genes.length; i++)
		{
			genome += genes[i] ? "1" : "0";
		}
		
		return genome + " (Max Jump: " + this.maxJumpCount + ")";
	}
}