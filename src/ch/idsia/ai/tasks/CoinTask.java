package ch.idsia.ai.tasks;

import ch.idsia.ai.agents.IAgent;
import ch.idsia.tools.EvaluatorOptions;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:28:56 AM
 * Package: com.mojang.mario.Tasks
 */
public class CoinTask implements Task {

    private EvaluatorOptions options = new EvaluatorOptions ();

    public double[] evaluate(IAgent controller) {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setOptions(EvaluatorOptions options) {
        this.options = options;
    }

    public EvaluatorOptions getOptions() {
        return options;
    }
}
