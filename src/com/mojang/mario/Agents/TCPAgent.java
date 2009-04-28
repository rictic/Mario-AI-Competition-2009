package com.mojang.mario.Agents;

import com.mojang.mario.Environments.IEnvironment;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 28, 2009
 * Time: 2:50:57 PM
 * Package: com.mojang.mario.Agents
 */
public class TCPAgent implements IAgent {

    public IAgent reset() {
        return this;
    }

    public boolean[] GetAction(IEnvironment observation) {
        return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public AGENT_TYPE getType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setName(String name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
