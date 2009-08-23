package com.reddit.programming.mario.tweaker;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;

//This line imports your interface agent.
import com.reddit.programming.mario.*;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.ai.*;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressPlusTimeLeftTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.utils.ArrayUtils;
import java.util.Random;

public class TweakRunner {
	
	public static void main(String[] args) {
		float[] best = new float[9];
		best[0] = Tunables.FactorA;
		best[1] = Tunables.FactorB;
		best[2] = Tunables.FactorC;
		best[3] = Tunables.DeathPenalty;
		best[4] = Tunables.DeathPenaltyWeakening;
		best[5] = Tunables.GIncrement;
		best[6] = Tunables.DeadCost;
		best[7] = Tunables.ChasmPenalty;
		best[8] = Tunables.FeetOnTheGroundBonus;
		
		float[] settings = new float[9];
		for (int i = 0; i<best.length; ++i)
			settings[i] = best[i];

		float bestScore = -1e10f;		
			
		Random rnd = new Random();
		int idx = 0;
		boolean direction = false;
		for (int i =0 ; i< 1000; ++i)
		{
			Tunables.FactorA = settings[0];
			Tunables.FactorB = settings[1];
			Tunables.FactorC = settings[2];
			Tunables.DeathPenalty = settings[3];
			Tunables.DeathPenaltyWeakening = settings[4];
			Tunables.GIncrement = settings[5];
			Tunables.DeadCost = settings[6];
			Tunables.ChasmPenalty = settings[7];
			Tunables.FeetOnTheGroundBonus = settings[8];
			float score = DoRun();
			if (score > bestScore)
			{
				bestScore = score;
				for (int j = 0; j<best.length; ++j)
					best[j] = settings[j];
				float delta = rnd.nextFloat()*(direction?1:-1);
				settings[idx] = (settings[idx] * (1f+delta)) + delta;
				System.out.println(score+":"+ArrayUtils.toString(best));
			}
//			 if (score == bestScore)
//			{
//				System.out.println("=");
//				float delta = rnd.nextFloat()*(direction?1:-1);
//				settings[idx] = (settings[idx] * (1f+delta)) + delta;
//			}
			else
			{
				System.out.println(".");
				for (int j = 0; j<best.length; ++j)
					settings[j] = best[j];
				idx = rnd.nextInt(best.length);
				direction = rnd.nextBoolean();
				float delta = rnd.nextFloat()*(direction?1:-1);
				settings[idx] = (settings[idx] * (1f+delta)) + delta;
			}
		}
	}
	
	private static float DoRun()
	{
//		float min = 1e10f;
		float sum = 0;
		int c = 20;
		for (int i = 0; i< c; ++i)
		{
			float r = Run(i, 20, 100);
//			min = Math.min(r, min);
			sum += r;
		}
		return
//			 (min) +
			 	 (sum / c);
	}
	
	private static float Run(int seed, int difficulty, int length)
	{
		// we need a redoable state that stresses mario enough
		
		GlobalOptions.setSeed(seed);
		GlobalOptions.setDifficulty(difficulty);

		Agent controller = new BestFirstAgent();
		GlobalOptions.currentController = controller.getName();
		GlobalOptions.writeFrames = false;
		EvaluationOptions options = new CmdLineOptions(new String[0]);
		options.setAgent(controller);
		Task task = new ProgressPlusTimeLeftTask(options);
		options.setMaxFPS(true);
		options.setVisualization(false);
		options.setMaxAttempts(1);
		options.setMatlabFileName("");
		options.setLevelLength(length);
		options.setLevelRandSeed(seed);
		options.setLevelDifficulty(difficulty);
		task.setOptions(options);
		
		return (float)task.evaluate(controller)[0];
	}	
}
