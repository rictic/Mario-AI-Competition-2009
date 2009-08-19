// This is a file that will play the game through once on normal speed.
// You can set which agent to use, and it was originally created to use
// the human KeyboardAgent.

package ch.idsia.scenarios;

//If you're using Eclipse, you should expand this import statement.
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

public class PlayForever {

	public static void main(String[] args) {
		int seed = (int) (Math.random () * Integer.MAX_VALUE);
		int difficulty = 20;
		int length = 100000;
		
		if (args.length > 1) {
			seed = Integer.parseInt(args[1]);
		}
		if (args.length > 2) {
			difficulty = Integer.parseInt(args[2]);
		}
		if (args.length > 3) {
			length = Integer.parseInt(args[3]);
		}

		GlobalOptions.drawText = false;
		while (true) {
			Agent controller = new BudgetingBestFirstAgent(); // This line uses the agent you imported above.
			if (args.length > 0) {
				controller = RegisterableAgent.load (args[0]);
				RegisterableAgent.registerAgent (controller);
			}
			GlobalOptions.setSeed(seed);
			GlobalOptions.setDifficulty(difficulty);
			
			
			GlobalOptions.currentController = controller.getName();
			GlobalOptions.writeFrames = false; //set to true to write frames to disk
			GlobalOptions.TimerOn = false;
			EvaluationOptions options = new CmdLineOptions(new String[0]);
			options.setAgent(controller);
			Task task = new ProgressTask(options);
			options.setMaxFPS(false);
			options.setVisualization(true);
			options.setMaxAttempts(1);
			options.setMatlabFileName("");
			options.setLevelLength(length);
			options.setLevelRandSeed(seed);
			options.setLevelDifficulty(difficulty);
			options.setTimeLimit(0);
			
			task.setOptions(options);

			System.out.println("Score: " + ArrayUtils.toString(task.evaluate(controller)));
			System.out.println("Seed: " + options.getLevelRandSeed());
			System.out.println("Difficulty: " + options.getLevelDifficulty());
			seed++;
		}
	}
}

