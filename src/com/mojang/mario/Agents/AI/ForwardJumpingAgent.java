package com.mojang.mario.Agents.AI;

import com.mojang.mario.Environments.IEnvironment;
import com.mojang.mario.Agents.IAgent;
import com.mojang.mario.Agents.RegisterableAgent;
import com.mojang.mario.sprites.Mario;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 12:27:07 AM
 * Package: com.mojang.mario.Agents
 */

public class ForwardJumpingAgent extends RegisterableAgent implements IAgent
{
    public ForwardJumpingAgent()
    {
        super("ForwardJumpingAgent");
        reset();
    }

    public IAgent reset()
    {
        Action = EmptyAction;
        Action[Mario.KEY_RIGHT] = true;
        Action[Mario.KEY_SPEED] = true;
        return this;
    }

    public boolean[] GetAction(IEnvironment observation)
    {
        Action[Mario.KEY_SPEED] = Action[Mario.KEY_JUMP] =  observation.mayMarioJump() || !observation.isMarioOnGround();
        return Action;
    }
}
