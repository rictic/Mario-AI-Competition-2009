package ch.idsia.ai.agents;

import ch.idsia.mario.environments.IEnvironment;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 28, 2009
 * Time: 8:46:42 PM
 * Package: com.mojang.mario.Agents
 */
public interface IAgent
{
    public enum AGENT_TYPE {AI, HUMAN, TCP}

    // clears all dynamic data, such as hidden layers in recurrent networks
    // just implement an empty method for a reactive controller
    public void reset();

    public boolean[] GetAction(IEnvironment observation);

    public AGENT_TYPE getType();

    public String getName();

    public void setName(String name);

//    public void register();
}
