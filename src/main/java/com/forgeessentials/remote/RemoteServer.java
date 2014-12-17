package com.forgeessentials.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;

/**
 *
 */
public class RemoteServer {

    private final ServerSocket serverSocket;

    /**
     * @param port
     * @param hostname
     * @throws IOException
     */
    public RemoteServer(int port, String hostname) throws IOException
    {
        serverSocket = new ServerSocket(port, 0, InetAddress.getByName(hostname));
    }

    /**
     * @param port
     * @param hostname
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public RemoteServer(int port, String hostname, SSLContext sslCtx) throws IOException, GeneralSecurityException
    {
        serverSocket = sslCtx.getServerSocketFactory().createServerSocket(port, 0, InetAddress.getByName(hostname));
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

}
