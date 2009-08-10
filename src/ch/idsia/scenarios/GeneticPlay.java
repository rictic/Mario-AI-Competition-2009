// This is a file that will play the game through once on normal speed.
// You can set which agent to use, and it was originally created to use
// the human KeyboardAgent.

package ch.idsia.scenarios;

//If you're using Eclipse, you should expand this import statement.
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import com.reddit.programming.mario.*;// This line imports your interface agent.

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.ai.*;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.utils.ArrayUtils;
import ch.idsia.ai.ea.GA;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 5, 2009
 * Time: 12:46:43 PM
 */
public class GeneticPlay {

    public static void main(String[] args) {
    	int seed = (int) (Math.random () * Integer.MAX_VALUE);
        GeneticAgent controller = new GeneticAgent(seed); // This line uses the agent you imported above.
        /*if (args.length > 0) {
            controller = RegisterableAgent.load (args[0]);
            RegisterableAgent.registerAgent (controller);
        }*/
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        //options.setAgent(controller);
        Task task = new ProgressTask(options);
        options.setMaxFPS(false);
        options.setVisualization(false);
        options.setMaxAttempts(1);
        options.setMatlabFileName("");
        options.setLevelRandSeed(seed);
        options.setLevelDifficulty(10);
        GlobalOptions.FPS = GlobalOptions.InfiniteFPS;
        task.setOptions(options);
        
        GA vivarium = new GA(task, new GeneticAgent(), 60);
        
        for (int i = 0; i < 30; i++)
        {
        	System.out.println("Best of Gen " + i + ": " + vivarium.getBestFitnesses()[0]);
        	vivarium.nextGeneration();
        	System.out.flush();
        }

        System.out.println("Score: " + ArrayUtils.toString(task.evaluate(controller)));
        System.out.println("Seed: " + options.getLevelRandSeed());
        System.out.println("Difficulty: " + options.getLevelDifficulty());
        System.out.println("Best Genome: " + vivarium.getBests()[0]);
    }
}