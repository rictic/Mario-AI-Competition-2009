package ch.idsia.scenarios;

import ch.idsia.ai.Evolvable;
import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.agents.ai.SimpleMLPAgent;
import ch.idsia.ai.ea.ES;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.LOGGER;
import wox.serial.Easy;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 4, 2009
 * Time: 4:33:25 PM
 */
public class Evolve {

    final static int generations = 100;
    final static int populationSize = 100;

    public static void main(String[] args) {
        Evolvable initial = new SimpleMLPAgent();
        EvaluationOptions options = new CmdLineOptions(args);
        options.setAgent((IAgent) initial);
        options.setMaxAttempts(1);
        options.setMaxFPS(true);
        options.setVisualization(false);
        options.setExitProgramWhenFinished(false);
        Task task = new ProgressTask(options);
        ES es = new ES(task, initial, populationSize);
        for (int gen = 0; gen < generations; gen++) {
            es.nextGeneration();
            Easy.save(es.getBests()[0], "evolved.xml");
            System.out.println("Generation " + gen + " best " + es.getBestFitnesses()[0]);
            options.setVisualization(true);
            System.out.println("trying: " + task.evaluate((IAgent) es.getBests()[0])[0]);
            options.setVisualization(false);
        }

    }
}
