package ch.idsia.mario.agents;

import ch.idsia.mario.agents.ai.BasicAIAgent;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 11, 2009
 * Time: 10:47:47 AM
 * Package: com.mojang.mario.Agents
 */
public class RegisterableAgent extends BasicAIAgent
{
    static HashMap<String, IAgent> AgentsHashMap = new LinkedHashMap<String, IAgent>();

    public RegisterableAgent(String s)
    {
        setName(s);
        registerAgent((IAgent) this);
    }

    public static void registerAgent(IAgent agent)
    {
        AgentsHashMap.put(agent.getName(), agent);
    }

    public static Set<String> getAgentsNames()
    {
        return AgentsHashMap.keySet();
    }

    public static IAgent getAgentByName(String selectedItem)
    {
        return AgentsHashMap.get(selectedItem);
    }
}
