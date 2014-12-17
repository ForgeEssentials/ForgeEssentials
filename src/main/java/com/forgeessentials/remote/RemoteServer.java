package com.forgeessentials.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLContext;

/**
 *
 */
public class RemoteServer implements Runnable {

    private final ServerSocket serverSocket;

    private Thread serverThread;

    private Set<RemoteSession> sessions = new HashSet<>();

    /**
     * @param port
     * @param hostname
     */
    public RemoteServer(ServerSocket socket)
    {
        serverSocket = socket;
        serverThread = new Thread(this);
        serverThread.start();
    }

    /**
     * @param port
     * @param hostname
     * @throws IOException
     */
    public RemoteServer(int port, String hostname) throws IOException
    {
        this(new ServerSocket(port, 0, InetAddress.getByName(hostname)));
    }

    /**
     * @param port
     * @param hostname
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public RemoteServer(int port, String hostname, SSLContext sslCtx) throws IOException, GeneralSecurityException
    {
        this(sslCtx.getServerSocketFactory().createServerSocket(port, 0, InetAddress.getByName(hostname)));
    }

    /**
     * Terminates the server
     */
    public void close()
    {
        try
        {
            serverSocket.close();
        }
        catch (IOException e)
        {
            /* ignore */
        }
    }

    /*
     * Server main loop
     */
    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                Socket s = serverSocket.accept();
                RemoteSession session = new RemoteSession(s);
                sessions.add(session);
            }
            catch (SocketException e)
            {
                /* socket probably closed */
            }
            catch (IOException e)
            {
                /* some other error */
            }
            if (serverSocket.isClosed() || !serverSocket.isBound())
                break;
        }
    }

}
