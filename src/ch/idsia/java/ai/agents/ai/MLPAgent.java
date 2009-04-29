package ch.idsia.java.ai.agents.ai;

import ch.idsia.java.mario.engine.environments.IEnvironment;
import ch.idsia.java.ai.agents.IAgent;
import ch.idsia.java.ai.agents.RegisterableAgent;

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
