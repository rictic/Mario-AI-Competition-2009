package ch.idsia.ai.tasks;

import ch.idsia.ai.agents.IAgent;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.EvaluatorOptions;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.mario.engine.level.LevelGenerator;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:26:43 AM
 * Package: com.mojang.mario.Tasks
 */
public class ProgressTask implements Task {

    EvaluatorOptions options = new EvaluatorOptions ();

    public double[] evaluate(IAgent controller) {
        options.setAgent(controller);
        options.setLevelLength(320);
        options.setLevelDifficulty(0);
        options.setLevelRandSeed(1);
        options.setVisualization(true);
        options.setLevelType(LevelGenerator.TYPE_OVERGROUND);
        options.setPauseWorld(false);
        options.setPowerRestoration(false);
        options.setStopSimulationIfWin(false);
        Evaluator evaluator = new Evaluator (options);
        List<EvaluationInfo> results = evaluator.evaluate ();
        double distanceTravelled = 0;
        for (EvaluationInfo result : results) {
            distanceTravelled += result.computeDistancePassed();
        }
        distanceTravelled = distanceTravelled / results.size();
        return new double[]{distanceTravelled};
    }

    public void setDifficuly(int difficulty) {
        options.setLevelDifficulty(difficulty);
    }

    public void setSeed(int seed) {
        options.setLevelRandSeed(seed);
    }
}
