package com.reddit.programming.mario.tweaker;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;

//This line imports your interface agent.
import com.reddit.programming.mario.*;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.ai.*;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.utils.ArrayUtils;

public class TweakRunner {
	
	public static void main(String[] args) {
		for (int i =0 ; i< 1000; ++i)
			System.out.println(DoRun());
	}
	
	private static float DoRun()
	{
		// we need a redoable state that stresses mario enough
		int seed = 1000;
		int difficulty = 30;
		int length = 1000;
		
		GlobalOptions.setSeed(seed);
		GlobalOptions.setDifficulty(difficulty);

		Agent controller = new BestFirstAgent();
		GlobalOptions.currentController = controller.getName();
		GlobalOptions.writeFrames = false;
		EvaluationOptions options = new CmdLineOptions(new String[0]);
		options.setAgent(controller);
		Task task = new ProgressTask(options);
		options.setMaxFPS(false);
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
