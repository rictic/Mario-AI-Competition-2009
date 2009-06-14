package ch.idsia.scenarios.test;

import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.ai.Evolvable;
import ch.idsia.ai.ea.ES;
import ch.idsia.ai.tasks.MultiDifficultyProgressTask;
import ch.idsia.ai.agents.ai.SimpleMLPAgent;
import ch.idsia.ai.agents.ai.MLPAgent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.Agent;
import ch.idsia.scenarios.Stats;
import wox.serial.Easy;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Jun 13, 2009
 * Time: 2:16:18 PM
 */
public class PaperEvolve {

    final static int generations = 100;
    final static int populationSize = 100;

    public static void main(String[] args) {
        final int seed = (int) (Math.random () * 10000000);
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setMaxAttempts(1);
        options.setPauseWorld(false);
        Evolvable initial = new MLPAgent();
        RegisterableAgent.registerAgent ((Agent) initial);
        options.setMaxFPS(true);
        options.setVisualization(false);
        MultiDifficultyProgressTask task = new MultiDifficultyProgressTask(options);
        task.setStartingSeed(seed);
        ES es = new ES (task, initial, populationSize);
        System.out.println("Evolving " + initial + " with task " + task);
        final String fileName = "evolved" + seed + ".xml";
        for (int gen = 0; gen < generations; gen++) {
            es.nextGeneration();
            double bestResult = es.getBestFitnesses()[0];
            System.out.println("Generation " + gen + " best " + bestResult);
            Evolvable bestEvolvable = es.getBests()[0];
            double[] fitnesses = task.evaluate((Agent) bestEvolvable);
            System.out.printf("%.4f  %.4f  %.4f  %.4f  %.4f\n",
                    fitnesses[0], fitnesses[1], fitnesses[2], fitnesses[3], fitnesses[4]);
            Easy.save (es.getBests()[0], fileName);
        }
        Stats.main(new String[]{fileName});
    }

}
