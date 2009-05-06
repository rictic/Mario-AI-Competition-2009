package ch.idsia.ai.tasks;

import ch.idsia.ai.agents.IAgent;
import ch.idsia.tools.EvaluatorOptions;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:20:41 AM
 * Package: com.mojang.mario.Tasks
 */
public interface Task {

    public double[] evaluate (IAgent controller);

    public void setOptions (EvaluatorOptions options);

    public EvaluatorOptions getOptions ();

}
