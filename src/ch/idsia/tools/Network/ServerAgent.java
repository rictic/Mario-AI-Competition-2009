package ch.idsia.tools.Network;

import ch.idsia.mario.environments.IEnvironment;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.IAgent;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 30, 2009
 * Time: 9:43:27 PM
 * Package: ch.idsia.tools.Network
 */

public class ServerAgent extends RegisterableAgent implements IAgent
{
    Server server = null;
    private int port;

    public ServerAgent(int port, boolean enable)
    {
        super("ServerAgent");
        this.port = port;
        if (enable)
        {
            createServer(port);
        }
    }

    // A tiny bit of singletone-like concept. Server is created ones for each egent. Basically we are not going
    // To create more than one ServerAgent at a run, but this flexibility allows to add this feature with certain ease.
    private void createServer(int port) {
        this.server = new Server(port, IEnvironment.NumberOfObservationElements, IEnvironment.NumberOfActions);
        this.Name += server.getClientName();
    }

    public boolean isAvailable()
    {
        return (server != null) && server.isClientConnected();
    }

    public void reset()
    {
        Action = EmptyAction;
        if (server == null)
            this.createServer(port);
    }

    private void sendLevelSceneObservation(IEnvironment observation) throws IOException
    {
        byte[][] levelScene = observation.getLevelSceneObservation();

        String tmpData = "" +
                observation.mayMarioJump() + " " + observation.isMarioOnGround();
        for (int y = 0; y < levelScene[0].length; ++y)
        {
            for (int x = 0; x < levelScene.length; ++x)
            {
                tmpData += " " + (levelScene[x][y]);
            }
        }
        server.sendSafe(tmpData);
        // TODO: StateEncoderDecoder.Encode.Decode.  zip, do not send mario position. zero instead for better zipping.
    }

    private boolean[] receiveAction() throws IOException, NullPointerException
    {
        String data = server.recvSafe();
        boolean[] ret = new boolean[IEnvironment.NumberOfActions];
        String s = "[";
        for (int i = 0; i < IEnvironment.NumberOfActions; ++i)
        {
            ret[i] = (data.charAt(i) == '1');
            s += data.charAt(i);
        }
        s += "]";

        System.out.println("ServerAgent: action received :" + s);
        return ret;
    }

    public boolean[] GetAction(IEnvironment observation)
    {
        try
        {
            System.out.println("ServerAgent: sending observation...");
            sendLevelSceneObservation(observation);
            Action = receiveAction();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("I/O Communication Error");
            reset();
        }
        return Action;
    }

    public AGENT_TYPE getType()
    {
        return IAgent.AGENT_TYPE.TCP_SERVER;
    }
}
