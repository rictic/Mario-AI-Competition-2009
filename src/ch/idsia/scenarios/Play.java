package ch.idsia.scenarios;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 5, 2009
 * Time: 12:46:43 PM
 */
public class Play {

    public static void main(String[] args) {
		int seed = (int) (Math.random () * Integer.MAX_VALUE);
		int difficulty = 10;
		int length = 320;
		
		if (args.length > 1) {
			seed = Integer.parseInt(args[1]);
		}
		if (args.length > 2) {
			difficulty = Integer.parseInt(args[2]);
		}
		if (args.length > 3) {
			length = Integer.parseInt(args[3]);
		}

        Agent controller = new HumanKeyboardAgent();
        if (args.length > 0) {
            controller = RegisterableAgent.load (args[0]);
            RegisterableAgent.registerAgent (controller);
        }
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setAgent(controller);
        Task task = new ProgressTask(options);
        options.setMaxFPS(false);
        options.setVisualization(true);
        options.setMaxAttempts(1);
        options.setMatlabFileName("");
        options.setLevelRandSeed(seed);
        options.setLevelDifficulty(difficulty);
		options.setLevelLength(length);
        task.setOptions(options);

        System.out.println ("Score: " + task.evaluate (controller)[0]);
    }
}
