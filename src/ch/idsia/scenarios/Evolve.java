package ch.idsia.scenarios;

import ch.idsia.ai.Evolvable;
import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.ai.SimpleMLPAgent;
import ch.idsia.ai.ea.ES;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.LOGGER;
import wox.serial.Easy;

import java.io.IOException;
import java.text.DecimalFormat;
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
        DecimalFormat df = new DecimalFormat("0000");

        for (int gen = 0; gen < 5; gen++) {
           es.nextGeneration();
            LOGGER.println("Generation " + gen + " best " + es.getBestFitnesses()[0], LOGGER.VERBOSE_MODE.INFO);
//           options.setVisualization(gen+1 % 10 == 0);
            IAgent a = (IAgent) es.getBests()[0];
            a.setName(((IAgent)initial).getName() + df.format(gen));
            RegisterableAgent.registerAgent(a);
            bestAgents.add(a ) ;
            LOGGER.println("trying: " + task.evaluate(a)[0], LOGGER.VERBOSE_MODE.INFO);
//           options.setVisualization(false);
        }

        // TODO: log dir / log dump dir option
        LOGGER.println("Saving bests... ", LOGGER.VERBOSE_MODE.INFO);

        options.setVisualization(true); int i = 0;
        for (IAgent bestAgent : bestAgents) {
            Easy.save(bestAgent, "bestAgent" +  df.format(i++) + ".xml");
        }

        LOGGER.println("Saved! Press return key to continue...", LOGGER.VERBOSE_MODE.INFO);
        try {System.in.read();        } catch (IOException e) {            e.printStackTrace();        }

        for (IAgent bestAgent : bestAgents) {
            task.evaluate(bestAgent);
        }


        LOGGER.save("log.txt");
        System.exit(0);
    }
}
