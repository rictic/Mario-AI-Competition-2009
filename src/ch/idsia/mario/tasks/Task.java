package ch.idsia.mario.tasks;

import ch.idsia.mario.agents.IAgent;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:20:41 AM
 * Package: com.mojang.mario.Tasks
 */
public interface Task {

    public double[] evaluate (IAgent controller);

    public void setDifficuly (int difficulty);

    public void setSeed (int seed);

}
