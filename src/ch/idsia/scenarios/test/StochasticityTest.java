package ch.idsia.scenarios.test;

import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.tasks.Task;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.CmdLineOptions;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 9, 2009
 * Time: 4:23:04 PM
 */
public class StochasticityTest {

    final static int repetitions = 10;

    public static void main(String[] args) {
        IAgent controller = RegisterableAgent.load (args[0]);
        //RegisterableAgent.registerAgent (controller);
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setAgent(controller);
        Task task = new ProgressTask(options);
        options.setMaxFPS(false);
        options.setVisualization(true);
        options.setMaxAttempts(1);
        options.setMatlabFileName("");
        task.setOptions(options);
        for (int i = 0; i < repetitions; i++) {
            System.out.println ("Score: " + task.evaluate (controller));
        }
    }

}
