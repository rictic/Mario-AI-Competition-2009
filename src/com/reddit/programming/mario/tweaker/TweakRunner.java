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
import ch.idsia.scenarios.CompetitionScore;

public class TweakRunner {
	
	public static void main(String[] args) {
		float[] best = new float[9];
		best[0] = Tunables.FactorA;
		best[1] = Tunables.FactorB;
		best[2] = Tunables.FactorC;
		best[3] = Tunables.GIncrement;
		best[4] = Tunables.DeadCost;
		best[5] = Tunables.ChasmPenalty;
		best[6] = Tunables.FeetOnTheGroundBonus;
		best[7] = Tunables.MaxBreadth;
		best[8] = Tunables.HurtCost;
		
		float[] settings = new float[best.length];
		for (int i = 0; i<best.length; ++i)
			settings[i] = best[i];
			
		int parameter = 7;

		float bestScore = -1e10f;		
			
		Random rnd = new Random();
		int idx;
		if (parameter == -1)
			idx = rnd.nextInt(best.length);
		else
			idx = parameter;		
		boolean direction = false;
		for (int i =0 ; i< 1000; ++i)
		{
			Tunables.FactorA = settings[0];
			Tunables.FactorB = settings[1];
			Tunables.FactorC = settings[2];
			Tunables.GIncrement = settings[3];
			Tunables.DeadCost = settings[4];
			Tunables.ChasmPenalty = settings[5];
			Tunables.FeetOnTheGroundBonus = settings[6];
			Tunables.MaxBreadth = (int)settings[7];
			Tunables.HurtCost = settings[8];
			float score;
			if (Tunables.MaxBreadth >= 2)
				score = DoRun();
			else
				score = -1e10f;
			score -= Tunables.MaxBreadth;
			if (score > bestScore)
			{
				bestScore = score;
				for (int j = 0; j<best.length; ++j)
					best[j] = settings[j];
				float delta = 0.5f * rnd.nextFloat()*(direction?1:-1);
				settings[idx] = (settings[idx] * (1f+delta));
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
				if (parameter == -1)
					idx = rnd.nextInt(best.length);
				else
					idx = parameter;
				direction = rnd.nextBoolean();
				float delta = 0.5f * rnd.nextFloat()*(direction?1:-1);
				settings[idx] = (settings[idx] * (1f+delta)) + 0.01f*delta;
			}
		}
	}
	
	private static float DoRun()
	{
			return(float)CompetitionScore.score(new BestFirstAgent(), 0);
			/*
//		float min = 1e10f;
		float sum = 0;
		int c = 20;
		for (int i = 0; i< c; ++i)
		{
			//float r = Run(i, 20, 200);
//			min = Math.min(r, min);
			if (r < 0)
				return -1;
			sum += r;
		}
		return
//			 (min) +
			 	 (sum / c);*/
	}
	
	private static float Run(int seed, int difficulty, int length)
	{
		// we need a redoable state that stresses mario enough
		Tunables.PathFound = 0;
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
		
		return (float)task.evaluate(controller)[0] + Tunables.PathFound;
	}	
}
