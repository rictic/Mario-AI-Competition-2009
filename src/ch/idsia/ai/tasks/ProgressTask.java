package ch.idsia.ai.tasks;

import ch.idsia.ai.agents.IAgent;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.EvaluatorOptions;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.ToolsConfigurator;
import ch.idsia.mario.engine.level.LevelGenerator;
import ch.idsia.mario.engine.MarioComponent;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:26:43 AM
 * Package: com.mojang.mario.Tasks
 */
public class ProgressTask implements Task {

    private EvaluatorOptions options = new EvaluatorOptions ();

    public double[] evaluate(IAgent controller) {
        
        
        options.setAgent(controller);
        Evaluator evaluator = new Evaluator (options);
        List<EvaluationInfo> results = evaluator.evaluate ();
        double distanceTravelled = 0;
        for (EvaluationInfo result : results) {
            distanceTravelled += result.computeDistancePassed();
        }
        distanceTravelled = distanceTravelled / results.size();
        return new double[]{distanceTravelled};
    }

    public void setOptions(EvaluatorOptions options) {
        this.options = options;
    }

    public EvaluatorOptions getOptions() {
        return options;
    }
}
