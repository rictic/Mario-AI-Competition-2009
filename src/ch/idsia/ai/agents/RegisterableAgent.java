package ch.idsia.ai.agents;

import ch.idsia.ai.agents.ai.BasicAIAgent;
import wox.serial.Easy;

import java.util.IllegalFormatException;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 11, 2009
 * Time: 10:47:47 AM
 package ch.idsia.ai.agents;
 */
public class RegisterableAgent extends BasicAIAgent
{
    private static Agent currentAgent = null;

    public RegisterableAgent(String s)
    {
        super();
        setName(s);
        registerAgent(this);
    }

    public static void registerAgent(Agent agent)
    {
        AgentsPool.put(agent.getName(), agent);
    }

    public static void setAgent(Agent agent) {
        currentAgent = agent;
    }

    public static void registerAgent(String agentWOXName) throws IllegalFormatException
    {
        registerAgent(load(agentWOXName));
    }

    public static Agent load (String name) {
        Agent agent;
        try {
            agent = (Agent) Class.forName (name).newInstance ();
        }
        catch (ClassNotFoundException e) {
            System.out.println (name + " is not a class name; trying to load a wox definition with that name.");
            agent = (Agent) Easy.load (name);
        }
        catch (Exception e) {
            e.printStackTrace ();
            agent = null;
            System.exit (1);
        }
        return agent;
    }

    public static Set<String> getAgentsNames()
    {
        return AgentsPool.agentsHashMap.keySet();
    }

    public static Agent getAgentByName(String agentName)
    {
        // There is only one case possible;
        Agent ret = AgentsPool.agentsHashMap.get(agentName);
        if (ret == null)
            ret = AgentsPool.agentsHashMap.get(agentName.split(":")[0]); 
        return ret;
    }

    public static Agent getAgent()
    {
        return currentAgent;
    }

}
