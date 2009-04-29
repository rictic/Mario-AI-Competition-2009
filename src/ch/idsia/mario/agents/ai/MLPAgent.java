package ch.idsia.mario.agents.ai;

import ch.idsia.mario.environments.IEnvironment;
import ch.idsia.mario.agents.IAgent;
import ch.idsia.mario.agents.RegisterableAgent;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:35:23 AM
 * Package: com.mojang.mario.Agents
 */
public class MLPAgent extends RegisterableAgent implements IAgent
{
//    MLP mlp = new MLP (10, 5, 6);
    // inputs: the blocks around mario
    // outputs: the individual keys
    public MLPAgent()
    {
        super("MLPAgent");
        reset();
    }

    public void reset() {
    }

    public boolean[] GetAction(IEnvironment observation) {
        return new boolean[IEnvironment.NumberOfActions];
    }
}
