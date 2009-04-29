package ch.idsia.ai.agents;

import ch.idsia.mario.environments.IEnvironment;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;


/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 27, 2009
 * Time: 1:21:20 AM
 * Package: com.mojang.mario.Agents
 */
public class TCPAgent extends RegisterableAgent implements IAgent
{
    private int port;
//    private Thread thread;

    public static void main(String[] args)
    {
        //TODO: DO this as option
        TCPAgent a = new TCPAgent(4242);
        a.reset();
    }

    private BufferedReader reader;
    private PrintStream printer;
    private Socket socket;

    public TCPAgent(int port)
    {
        super("TCPAgent");
        this.port = port;
        reset();
    }

    public void reset()
    {
        try
        {
            ServerSocket ss = new ServerSocket (port);
            System.out.println ("Waiting for a client to connect on port " + port);
            socket = ss.accept ();
            System.out.println ("We have a connection from " + socket.getInetAddress ());
            reader = new BufferedReader (new InputStreamReader(socket.getInputStream ()));
            System.out.println(reader.readLine());            
            printer = new PrintStream (socket.getOutputStream ());
            printer.println ("Hi! Hope you are PyBrain\n");
//            thread = new Thread();
//            Thread thread = new Thread (this);
//            thread.start ();
        }
        catch (Exception e)
        {
            e.printStackTrace ();
        }
    }

    private void sendLevelSceneObservation(IEnvironment observation) throws IOException
    {
        byte[][] levelScene = observation.getLevelSceneObservation();

        String tmpData = "" +
                observation.mayMarioJump() + " " + observation.isMarioOnGround();
        int dataLength = tmpData.length();
        byte i = 0;
        for (int y = 0; y < levelScene[0].length; ++y)
        {
            for (int x = 0; x < levelScene.length; ++x)
            {
//                tmpData += " " + (levelScene[x][y]);
//                dataLength += ("" + levelScene[x][y]).length() + 1;
                dataLength += (" " + i).length();
                tmpData += " " + i;
                ++i;
                if (i > 120)
                    i = 0;
            }
        }


        if (dataLength != tmpData.length() || tmpData.split(" ").length != 486)
            try {
                throw new Exception();
            } catch (Exception e) {
                System.err.println("Critical Server Error");
                System.out.println("length" + dataLength + ", " + tmpData.length() + tmpData.split(" ").length );
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        // TODO: add ack, nack
        // TODO: StateEncoderDecoder.Encode.Decode.  zip, do not send mario position. zero instead for better zipping.
        printer.print(tmpData);
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (printer.checkError())
        {
            System.err.println("ERROR DETECTED!");
            System.out.println("length: " + tmpData.split(" ").length);
        }


        System.out.println("observation sent!" + dataLength);
    }

    private boolean[] receiveAction() throws IOException
    {
        System.out.println("receiving action");
        String a = reader.readLine();
        System.out.println("action Line: " + a);
        boolean[] ret = new boolean[IEnvironment.NumberOfActions];
        String s = "[";
        for (int i = 0; i < IEnvironment.NumberOfActions; ++i)
        {
            ret[i] = (a.charAt(i) == '1');
            s += a.charAt(i);
        }
        s += "]";

        System.out.println("action received :" + s);
        return ret;
    }

    public boolean[] GetAction(IEnvironment observation)
    {
        try
        {
            System.out.println("sending observation...");
            sendLevelSceneObservation(observation);

            Action = receiveAction();

            Thread.sleep(1);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Communication Error");
            reset();
        } catch (InterruptedException e) {
            e.printStackTrace(); 
        }
        return Action;
    }

    public AGENT_TYPE getType()
    {
        return IAgent.AGENT_TYPE.TCP;
    }
}
