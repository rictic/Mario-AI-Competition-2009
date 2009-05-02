package ch.idsia.tools.Network;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.BindException;
import java.util.Random;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 30, 2009
 * Time: 9:15:54 PM
 * Package: ch.idsia.tools.Network
 */

public class Server 
{
    private String clientName = "<>";
//    public static final int defaultPort = 4242;

    public boolean isClientConnected() {return !socket.isClosed();}


    enum STATUS {SUCCEED, ERROR_SENDING, ERROR_RECEIVING}
    private int port;
    private int requiredSentDataSize = 1;
    private int requiredReceiveDataSize = 1;
    private List<Integer> trustedLengths = null; // TODO:SK trustedLengths

    private BufferedReader in = null;
    PrintWriter out = null;
    ServerSocket serverSocket = null;
    private Socket socket = null;

    public Server(int port, int requiredSentDataSize, int requiredReceiveDataSize)
    {
        this.port = port;
        this.requiredSentDataSize = requiredSentDataSize;
        this.requiredReceiveDataSize = requiredReceiveDataSize;
        reset();
    }

    private int resetSafe() 
    {
        try
        {
            System.out.println("Server: Binding Server to listern port " + port);
            serverSocket = new ServerSocket(this.port);
            System.out.println ("Server: Waiting for a client to connect on port " + this.port);
            socket = serverSocket.accept ();
            System.out.println ("Server: We have a connection from " + socket.getInetAddress ());
            out = new PrintWriter(socket.getOutputStream ());
            this.send("Server: Hi! Welcome.");
            in = new BufferedReader (new InputStreamReader(socket.getInputStream ()));
            final String greetingMessage = in.readLine();
            this.setClientName(greetingMessage);
            System.out.println(greetingMessage);
        }
        catch (BindException be)
        {
//            be.printStackTrace();
            System.err.println("Server: Port " + this.port + " is in use.");
            this.port = 4000 + ((new Random()).nextInt() % 1000);
            return 1;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("Server: I/O ERROR");
            return 2;
        }

        return 0;
    }

    private void restartServer()
    {
        shutDownServer();
        System.out.println("Server will be restarted at port " + this.port );
        reset();
    }

    public void reset() {
        int status;
        do {
            status = this.resetSafe();
        }
        while (status == 1 || status == 2);
    }

    private void send(String message)
    {
        System.out.println("Sedning message: " + message);
        out.print(message);
        System.out.println("Server: " + message.length() + " bytes of data had been sent");
        if (out.checkError())
        {
            System.err.println("Server.send() : Error detected while sending");
            restartServer();
        }
    }

    public STATUS sendSafe(String message)
    {
        int len = message.split(" ").length;
        if (len != this.requiredSentDataSize)
        {
            try
            {
                throw new Exception("Actual data size " + len + "of the sending message" + message +
                        "does not match required value " + requiredSentDataSize);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                restartServer();
                return STATUS.ERROR_SENDING;
            }
        }
        else
        {
            this.send(message);
        }
        return STATUS.SUCCEED;
    }

    private String recv()
    {
        String ret = null;
        try {
            System.out.println("Server.recv() >> Looking forward receiving data");
            ret = in.readLine();
            System.out.println("Server.recv() >> " + ret.length() + " bytes of data received: " + ret);
            return ret;
        }
        catch (NullPointerException e)
        {
            System.err.println("Server.recv() >> Null message received. Client cancelled connection");
//            restartServer();
            return "";
        }        
        catch (IOException e) {
            System.err.println("Server.recv() >> I/O exception. Cause: " + e.getCause());
//            restartServer();
            return "";
        }
    }

    public String recvSafe()
    {
        String message = recv();
        int len = message.length();
        if (len != this.requiredReceiveDataSize)
        {
            try
            {
                throw new Exception("Server.recvSafe: Actual data size " + len +
                        " of the received message <" + message +
                        "> does not match required value " + requiredReceiveDataSize);
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
                restartServer();
            }
        }
        else
        {
            return message;
        }

        message = "";
        for (int i = 0; i < this.requiredReceiveDataSize; ++i)  message += "1";
        return message;
    }


    private void shutDownServer()
    {
        try
        {
            System.out.println("Server: Try to Shutdown Server...");
            in.close();
            out.close();
            serverSocket.close();
            socket.close();
            System.out.println("Server: Server has been shutted down properly...");
        } catch (NullPointerException e) {
            System.err.println("Error Shutting Down: server is not created.");
        }
        catch (IOException e)
        {
            System.err.println("I/O Exception while shutting down");
        }
    }


    public String getClientName()
    {
        return clientName;
    }

    private void setClientName(String greetingMessage)
    {
        String[] m = greetingMessage.split(" am ");
        if (m.length == 2)
            this.clientName = "<" + m[1] + ">";
    }


}
