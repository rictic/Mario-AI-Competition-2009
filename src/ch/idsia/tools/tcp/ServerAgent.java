package ch.idsia.tools.tcp;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.environments.Environment;
import ch.idsia.tools.EvaluationInfo;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 30, 2009
 * Time: 9:43:27 PM
 * Package: ch.idsia.tools.Network
 */

public class ServerAgent extends RegisterableAgent implements Agent
{
    Server server = null;
    private int port;
    private TCP_MODE tcpMode;

    public ServerAgent(int port, boolean enable)
    {
        super("ServerAgent");
        this.port = port;
        if (enable)
        {
            createServer(port);
        }
    }

    public ServerAgent(Server server)
    {
        super("ServerAgent");
        this.server = server;
    }

    public String getName()
    {
        return this.name + ((server == null) ? "" : server.getClientName());
    }


    // A tiny bit of singletone-like concept. Server is created ones for each egent. Basically we are not going
    // To create more than one ServerAgent at a run, but this flexibility allows to add this feature with certain ease.
    private void createServer(int port) {
        this.server = new Server(port, Environment.numberOfObservationElements, Environment.numberOfButtons);
//        this.name += server.getClientName();
    }

    public boolean isAvailable()
    {
        return (server != null) && server.isClientConnected();
    }

    public void reset()
    {
        action = new boolean[Environment.numberOfButtons];
        if (server == null)
            this.createServer(port);
    }

    private void sendCompleteObservation(Environment observation)
    {
//        byte[][] levelScene = observation.getLevelSceneObservation();
        // MERGED
        byte[][] completeObs = observation.getCompleteObservation();

        String tmpData = "O " +
                observation.mayMarioJump() + " " + observation.isMarioOnGround();
        for (int x = 0; x < completeObs.length; ++x)
        {
            for (int y = 0; y < completeObs.length; ++y)
            {
                tmpData += " " + (completeObs[x][y]);
            }
        }
//        tmpData = "O 0 10 101 0 1 0 10 10 1 0 10 0 1 010 1 01";

        server.sendSafe(tmpData);
        // TODO: StateEncoderDecoder.Encode.Decode.  zip, gzip do not send mario position. zero instead for better compression.
    }

    private void sendObservation(Environment observation)
    {
        if (this.tcpMode == TCP_MODE.SIMPLE_TCP)
        {
            this.sendCompleteObservation(observation);
        }
        else if (this.tcpMode == TCP_MODE.FAST_TCP)
        {
            this.sendBitmapObservation(observation);
        }
    }

    private void sendBitmapObservation(Environment observation)
    {
        String tmpData =  "E " +
                          observation.mayMarioJump() + " " +
                          observation.isMarioOnGround() +
                          observation.getBitmapLevelObservation() +
                          observation.getBitmapEnemiesObservation();
        server.sendSafe(tmpData);
    }

    public void integrateEvaluationInfo(EvaluationInfo evaluationInfo)
    {
        String fitnessStr = "FIT " +
                evaluationInfo.marioStatus + " " +
                evaluationInfo.computeDistancePassed() + " " +
                evaluationInfo.timeLeft + " " +
                evaluationInfo.marioMode + " " +
                evaluationInfo.numberOfGainedCoins + " ";
        server.sendSafe(fitnessStr);
    }

    private boolean[] receiveAction() throws IOException, NullPointerException
    {
        String data = server.recvSafe();
        if (data == null || data.startsWith("reset"))
            return null;
        boolean[] ret = new boolean[Environment.numberOfButtons];
//        String s = "[";
        for (int i = 0; i < Environment.numberOfButtons; ++i)
        {
            ret[i] = (data.charAt(i) == '1');
//            s += data.charAt(i);
        }
//        s += "]";

//        System.out.println("ServerAgent: action received :" + s);
        return ret;
    }

    public boolean[] getAction(Environment observation)
    {
        try
        {
//            System.out.println("ServerAgent: sending observation...");
            sendCompleteObservation(observation);
            action = receiveAction();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("I/O Communication Error");
            reset();
        }
        return action;
    }

    public AGENT_TYPE getType()
    {
        return Agent.AGENT_TYPE.TCP_SERVER;
    }
}
