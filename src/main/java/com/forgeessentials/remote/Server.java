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

import com.forgeessentials.api.remote.RemoteResponse;

/**
 *
 */
public class Server implements Runnable {

    private final ServerSocket serverSocket;

    private Thread serverThread;

    private Set<Session> sessions = new HashSet<>();

    /**
     * @param port
     * @param hostname
     */
    public Server(ServerSocket socket)
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
    public Server(int port, String hostname) throws IOException
    {
        this(new ServerSocket(port, 0, InetAddress.getByName(hostname)));
    }

    /**
     * @param port
     * @param hostname
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public Server(int port, String hostname, SSLContext sslCtx) throws IOException, GeneralSecurityException
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
            RemoteResponse<?> shutdownMessage = RemoteResponse.ok("shutdown", 0, "Server shutting down");
            for (Session session : sessions)
            {
                session.trySendMessage(shutdownMessage);
                session.close();
            }
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
                Session session = new Session(s);
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
