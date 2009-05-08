package ch.idsia.scenarios;

import ch.idsia.ai.Evolvable;
import ch.idsia.ai.SimpleMLPAgent;
import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.ea.ES;
import ch.idsia.ai.tasks.Task;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstName_at_idsia_dot_ch
 * Date: May 7, 2009
 * Time: 4:38:23 PM
 * Package: ch.idsia
 */

public class CustomRun
{
    final static int generations = 100;
    final static int populationSize = 100;

    public static void main(String[] args) {
        EvaluationOptions evaluationOptions = new CmdLineOptions(args);
        Evolvable initial = new SimpleMLPAgent();
        evaluationOptions.setAgent((IAgent) initial);     //TODO:SK Manage interfaces! Casting is ugly.
        Task task = new ProgressTask();
        task.setOptions(evaluationOptions); // or separate into actual EvaluationOptions and CmdLineOptions as in MainRun.main
        ES es = new ES (task, initial, populationSize);
        for (int gen = 0; gen < generations; gen++) {
           es.nextGeneration();
            System.out.println("Generation " + gen + " best " + es.getBestFitnesses()[0]);
        }
    }
}
