package com.forgeessentials.snooper;

import com.forgeessentials.util.OutputHandler;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SocketListner implements Runnable {
    private int port;
    private String hostname;
    private Thread thread;
    private ServerSocket socket;
    private boolean running = false;
    private InetAddress inetAddress;
    public List<SocketHandler> socketList = new ArrayList<SocketHandler>();

    public SocketListner()
    {
        port = ModuleSnooper.port;
        hostname = ModuleSnooper.hostname;

        if (hostname.length() > 0)
        {
            try
            {
                inetAddress = InetAddress.getByName(hostname);
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
        }

        if (port == 0)
        {
            port = MinecraftServer.getServer().getPort();
        }

        thread = new Thread(this, "ForgeEssentials - Snooper - SocketListner");
        thread.start();
    }

    @Override
    public void run()
    {
        OutputHandler.felog.info("Starting snooper on " + inetAddress + ":" + port);
        if (!init())
        {
            OutputHandler.felog.severe("Unable to start the snooper!");
            return;
        }

        try
        {
            while (running)
            {
                try
                {
                    socketList.add(new SocketHandler(socket.accept(), this));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                socket.close();
                closeAll();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void closeAll()
    {
        for (SocketHandler handler : socketList)
        {
            try
            {
                handler.close();
            }
            catch (Exception e)
            {
            }
        }
        socketList.clear();
    }

    private boolean init()
    {
        try
        {
            socket = new ServerSocket(port, 0, inetAddress);
            running = true;
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void stop()
    {
        running = false;
    }
}
