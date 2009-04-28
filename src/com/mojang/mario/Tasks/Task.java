package com.mojang.mario.Tasks;

import com.mojang.mario.Agents.IAgent;

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
