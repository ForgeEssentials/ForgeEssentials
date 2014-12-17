package com.forgeessentials.remote;

import java.io.IOException;
import java.net.Socket;

/**
 *
 */
public class RemoteSession implements Runnable {

    private final Socket socket;

    private final Thread thread;

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
        while (true)
        {
            /* do some stuff */
            break;
        }
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
