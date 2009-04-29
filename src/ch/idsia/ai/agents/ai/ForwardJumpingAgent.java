package ch.idsia.ai.agents.ai;

import ch.idsia.mario.environments.IEnvironment;
import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.java.mario.engine.sprites.Mario;

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

    public void reset()
    {
        Action = EmptyAction;
        Action[Mario.KEY_RIGHT] = true;
        Action[Mario.KEY_SPEED] = true;
    }

    public boolean[] GetAction(IEnvironment observation)
    {
        Action[Mario.KEY_SPEED] = Action[Mario.KEY_JUMP] =  observation.mayMarioJump() || !observation.isMarioOnGround();
        return Action;
    }
}
