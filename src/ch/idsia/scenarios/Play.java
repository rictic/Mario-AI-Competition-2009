package ch.idsia.scenarios;

import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.Task;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import wox.serial.Easy;

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
            controller = load (args[0]);
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

    public static IAgent load (String name) {
        IAgent agent;
        try {
            agent = (IAgent) Class.forName (name).newInstance ();
        }
        catch (ClassNotFoundException e) {
            System.out.println (name + " is not a class name; trying to load a wox definition with that name.");
            agent = (IAgent) Easy.load (name);
        }
        catch (Exception e) {
            e.printStackTrace ();
            agent = null;
            System.exit (1);
        }
        return agent;
    }


}
