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

    public IAgent reset() {
        return null;
    }

    public boolean[] GetAction(IEnvironment observation) {
        return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
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
