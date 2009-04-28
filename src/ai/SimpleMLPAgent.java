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

    public SimpleMLPAgent () {
        mlp = new MLP (10, 6, 6);
    }

    public SimpleMLPAgent (MLP mlp) {
        this.mlp = mlp;
    }

    public IAgent reset() {
        return null;
    }

    public boolean[] GetAction(IEnvironment observation) {
        return new boolean[0];
    }

    public AGENT_TYPE getType() {
        return null;
    }

    public String getName() {
        return null;
    }

    public void setName(String name) {
        
    }
}
