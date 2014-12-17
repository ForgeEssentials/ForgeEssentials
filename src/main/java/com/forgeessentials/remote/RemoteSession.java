package com.forgeessentials.remote;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.forgeessentials.util.OutputHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 *
 */
public class RemoteSession implements Runnable {

    private final Socket socket;

    private final Thread thread;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * @param socket
     */
    public RemoteSession(Socket socket)
    {
        this.socket = socket;
        thread = new Thread(this);
        thread.start();
    }
    
    /*
     * Main session loop
     */
    @Override
    public void run()
    {
        try
        {
            final SocketStreamSplitter sss = new SocketStreamSplitter(socket.getInputStream(), "\n\n\n");
            while (true)
            {
                try
                {
                    final String msg = sss.readNext();
                    if (msg == null)
                    {
                        OutputHandler.felog.warning("[remote] Connection closed: " + ((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName());
                        break;
                    }
                    processMessage(msg);
                }
                catch (JsonSyntaxException e)
                {
                    OutputHandler.felog.warning("[remote] Message error: " + e.getMessage());
                    break;
                }
                catch (IOException e)
                {
                    OutputHandler.felog.warning("[remote] Socket error: " + e.getMessage());
                    break;
                }
            }
        }
        catch (IOException e)
        {
            OutputHandler.felog.warning("[remote] Error opening input stream.");
        }
        close();
    }
    
    /**
     * All received messages start being processed here
     * 
     * @param message
     */
    protected void processMessage(String message)
    {
        JsonObject data = gson.fromJson(message, JsonObject.class);
        OutputHandler.felog.info("[remote] Message: " + data.toString());
    }

    public void close()
    {
        try
        {
            socket.close();
        }
        catch (IOException e)
        {
            /* ignore */
        }
    }

}
