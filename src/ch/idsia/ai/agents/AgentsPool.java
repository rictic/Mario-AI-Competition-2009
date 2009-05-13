package ch.idsia.ai.agents;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: May 9, 2009
 * Time: 8:28:06 PM
 * Package: ch.idsia.ai.agents
 */
public class AgentsPool
{
    public static void put(String name, Agent agent) {
        agentsHashMap.put(agent.getName(), agent);
    }
    
    static HashMap<String, Agent> agentsHashMap = new LinkedHashMap<String, Agent>();
}
