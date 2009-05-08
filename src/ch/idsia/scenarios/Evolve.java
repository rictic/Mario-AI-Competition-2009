package ch.idsia.scenarios;

import ch.idsia.ai.Evolvable;
import ch.idsia.ai.SimpleMLPAgent;
import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.ea.ES;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.LOGGER;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 4, 2009
 * Time: 4:33:25 PM
 */
public class Evolve {

    final static int generations = 2;
    final static int populationSize = 100;

    public static void main(String[] args) {
        Evolvable initial = new SimpleMLPAgent();
        EvaluationOptions options = new CmdLineOptions(args);
        options.setAgent((IAgent)initial);
        options.setMaxAttempts(1);
        options.setMaxFPS(true);
        options.setVisualization(false);
        Task task = new ProgressTask(options);
        ES es = new ES (task, initial, populationSize);
        for (int gen = 0; gen < generations; gen++) {
           es.nextGeneration();
            LOGGER.println("Generation " + gen + " best " + es.getBestFitnesses()[0], LOGGER.VERBOSE_MODE.INFO);
           options.setVisualization(true);
            LOGGER.println("trying: " + task.evaluate((IAgent)es.getBests()[0])[0], LOGGER.VERBOSE_MODE.INFO);
           options.setVisualization(false);
        }
        LOGGER.save("log.txt");
    }
}
