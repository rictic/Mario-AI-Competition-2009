package ai;

import com.mojang.mario.Agents.IAgent;
import com.mojang.mario.Environments.IEnvironment;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Apr 28, 2009
 * Time: 2:09:42 PM
 */
public class SimpleMLPAgent implements IAgent {

    final MLP mlp;
    final int numberOfOutputs = 6;

    public SimpleMLPAgent () {
        mlp = new MLP (10, 6, numberOfOutputs);
    }

    public SimpleMLPAgent (MLP mlp) {
        this.mlp = mlp;
    }

    public IAgent reset() {
        mlp.reset ();
        return this;
    }

    public boolean[] GetAction(IEnvironment observation) {
        double[] inputs = null;
        double[] outputs = mlp.propagate (inputs);
        boolean[] action = new boolean[numberOfOutputs];
        for (int i = 0; i < action.length; i++) {
            action[i] = outputs[i] > 0;    
        }
        return action;
    }

    public AGENT_TYPE getType() {
        return AGENT_TYPE.HUMAN;
    }

    public String getName() {
        return null;
    }

    public void setName(String name) {
        
    }
}
