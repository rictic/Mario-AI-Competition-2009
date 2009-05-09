package ch.idsia.scenarios;

import ch.idsia.ai.agents.IAgent;
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
        IAgent controller = new HumanKeyboardAgent();
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
        task.setOptions(options);

        System.out.println ("Score: " + task.evaluate (controller));
    }
}
