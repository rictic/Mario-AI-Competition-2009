package com.mojang.mario.Agents.AI;

import com.mojang.mario.Environments.IEnvironment;
import com.mojang.mario.Agents.IAgent;
import com.mojang.mario.Agents.RegisterableAgent;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 28, 2009
 * Time: 10:37:18 PM
 * Package: com.mojang.mario.Agents
 */
public class RandomAgent extends RegisterableAgent implements IAgent
{
    public RandomAgent()
    {
        super("RandomAgent");
        reset();
    }

    private Random R = null;
    public IAgent reset()
    {
        // Dummy reset, of course, but meet formalities!
        R = new Random();
        return this;
    }

    public boolean[] GetAction(IEnvironment observation)
    {
        boolean[] ret = new boolean[IEnvironment.NumberOfActions];

        for (int i = 0; i < IEnvironment.NumberOfActions; ++i)
        {
            // Here the RandomAgent is encouraged to move more often to the Right and make long Jumps.
            boolean toggleParticularAction = R.nextBoolean();
            toggleParticularAction = (i == 0 && toggleParticularAction && R.nextBoolean()) ? R.nextBoolean() :  toggleParticularAction;
            toggleParticularAction = (i == 1 || i > 3 && !toggleParticularAction ) ? R.nextBoolean() :  toggleParticularAction;
            toggleParticularAction = (i > 3 && !toggleParticularAction ) ? R.nextBoolean() :  toggleParticularAction;
//            toggleParticularAction = (i == 4 && !toggleParticularAction ) ? R.nextBoolean() :  toggleParticularAction;
            ret[i] = toggleParticularAction;
        }
        if (ret[1])
            ret[0] = false;
        return ret;
    }
}
