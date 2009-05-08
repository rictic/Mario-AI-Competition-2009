package ch.idsia.ai;

import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.Task;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.CmdLineOptions;
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
        }
        Task task = new ProgressTask();
        task.setOptions(new CmdLineOptions(args) );
        System.out.println ("Score: " + task.evaluate (controller));
    }


    public static IAgent load (String name) {
        IAgent controller;
        try {
            controller = (IAgent) Class.forName (name).newInstance ();
        }
        catch (ClassNotFoundException e) {
            System.out.println (name + " is not a class name; trying to load a wox definition with that name.");
            controller = (IAgent) Easy.load (name);
        }
        catch (Exception e) {
            e.printStackTrace ();
            controller = null;
            System.exit (0);
        }
        return controller;
    }
}
