package ch.idsia.scenarios.test;

import wox.serial.Easy;
import ch.idsia.ai.Evolvable;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.ai.SimpleMLPAgent;
import ch.idsia.ai.ea.ES;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.scenarios.Stats;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Jun 14, 2009
 * Time: 2:15:51 PM
 */
public class EvolveSingle {

    final static int generations = 100;
    final static int populationSize = 100;
    

    public static void main(String[] args) {
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setMaxAttempts(1);
        options.setPauseWorld(false);
        Evolvable initial = new SimpleMLPAgent();
        RegisterableAgent.registerAgent ((Agent) initial);
        options.setMaxFPS(true);
        options.setLevelDifficulty(0);
        options.setVisualization(false);
        ProgressTask task = new ProgressTask(options);
        options.setLevelRandSeed((int) (Math.random () * Integer.MAX_VALUE));
        ES es = new ES (task, initial, populationSize);
        System.out.println("Evolving " + initial + " with task " + task);
        final String fileName = "evolved" + (int) (Math.random () * Integer.MAX_VALUE) + ".xml";
        for (int gen = 0; gen < generations; gen++) {
            es.nextGeneration();
            double bestResult = es.getBestFitnesses()[0];
            System.out.println("Generation " + gen + " best " + bestResult);
            Easy.save (es.getBests()[0], fileName);
        }
        Stats.main(new String[]{fileName, "1"});
    }
}
