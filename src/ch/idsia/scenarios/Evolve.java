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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 4, 2009
 * Time: 4:33:25 PM
 */
public class Evolve {

    final static int generations = 1500;
    final static int populationSize = 100;

    public static void main(String[] args) {
        Evolvable initial = new SimpleMLPAgent();
        EvaluationOptions options = new CmdLineOptions(args);
        options.setAgent((IAgent)initial);
        options.setMaxAttempts(1);
        options.setMaxFPS(true);
        options.setVisualization(false);
        options.setPauseWorld(true);
        Task task = new ProgressTask(options);
        ES es = new ES (task, initial, populationSize);
        List<IAgent> bestAgents = new ArrayList<IAgent>(1500);
        for (int gen = 0; gen < generations; gen++) {
           es.nextGeneration();
            LOGGER.println("Generation " + gen + " best " + es.getBestFitnesses()[0], LOGGER.VERBOSE_MODE.INFO);
           options.setVisualization(gen % 10 == 0);
            bestAgents.add( (IAgent)es.getBests()[0]) ;
            LOGGER.println("trying: " + task.evaluate((IAgent)es.getBests()[0])[0], LOGGER.VERBOSE_MODE.INFO);
           options.setVisualization(false);
        }

        LOGGER.println("Press any key to continue... ", LOGGER.VERBOSE_MODE.INFO);

        try {System.in.read();        } catch (IOException e) {            e.printStackTrace();        }

        options.setVisualization(true);
        for (IAgent bestAgent : bestAgents) {
            task.evaluate(bestAgent);
        }


        LOGGER.save("log.txt");
        System.exit(0);
    }
}
