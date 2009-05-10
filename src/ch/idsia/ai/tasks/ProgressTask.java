package ch.idsia.ai.tasks;

import ch.idsia.ai.agents.IAgent;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.Evaluator;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:26:43 AM
 * Package: com.mojang.mario.Tasks
 */
public class ProgressTask implements Task {

    private EvaluationOptions options;

    public ProgressTask(EvaluationOptions evaluationOptions) {
        setOptions(evaluationOptions);
    }

    public double[] evaluate(IAgent controller) {
        double distanceTravelled = 0;
//        controller.reset();
        options.setAgent(controller);
        Evaluator evaluator = new Evaluator(options);
        List<EvaluationInfo> results = evaluator.evaluate();
        for (EvaluationInfo result : results) {
            distanceTravelled += result.computeDistancePassed();
        }
        distanceTravelled = distanceTravelled / results.size();
        return new double[]{distanceTravelled};
    }

    public void setOptions(EvaluationOptions options) {
        this.options = options;
    }

    public EvaluationOptions getOptions() {
        return options;
    }

}
