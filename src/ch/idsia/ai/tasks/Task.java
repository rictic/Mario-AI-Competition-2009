package ch.idsia.ai.tasks;

import ch.idsia.ai.agents.Agent;
import ch.idsia.tools.EvaluationOptions;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:20:41 AM
 * Package: com.mojang.mario.Tasks
 */
public interface Task {
    public double[] evaluate (Agent controller);

    public void setOptions (EvaluationOptions options);

    public EvaluationOptions getOptions ();

}
