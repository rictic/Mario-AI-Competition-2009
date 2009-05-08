package ch.idsia.ai.agents;

import ch.idsia.ai.agents.ai.BasicAIAgent;
import java.util.*;

import wox.serial.Easy;

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
        super();
        setName(s);
        registerAgent(this);
    }

    public static void registerAgent(IAgent agent)
    {
        AgentsHashMap.put(agent.getName(), agent);
    }

    public static void registerAgent(String agentWOXName) throws IllegalFormatException {
        if (agentWOXName.endsWith(".xml"))
            registerAgent(load(agentWOXName));
        else {
            try {
                throw new IllegalArgumentException("Critical Error: Cannot register the agent. Name specified " + agentWOXName +
                        " is not a valid WOX name. Should end up with '.xml'");
            }
            catch(IllegalArgumentException e) {
                System.err.println(e.getMessage());
                System.err.println("Exiting...");
                System.exit (1);
            }
        }
    }

    private static IAgent load (String name) {
        IAgent agent;
        try {
            agent = (IAgent) Class.forName (name).newInstance ();
        }
        catch (ClassNotFoundException e) {
            System.out.println (name + " is not a class name; trying to load a wox definition with that name.");
            agent = (IAgent) Easy.load (name);
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
        return AgentsHashMap.keySet();
    }

    public static IAgent getAgentByName(String agentName)
    {
        if (AgentsHashMap.get(agentName) == null)
        {

        }

        return AgentsHashMap.get(agentName);
    }
}
