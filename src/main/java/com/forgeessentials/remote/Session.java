package com.forgeessentials.remote;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.forgeessentials.api.remote.RemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 *
 */
public class Session implements Runnable, RemoteSession {

    private static final String SEPARATOR = "\n\n\n";

    private final Socket socket;

    private final Thread thread;

    private UserIdent userIdent;

    /**
     * @param socket
     */
    public Session(Socket socket)
    {
        this.socket = socket;
        this.thread = new Thread(this);
        this.thread.start();
    }

    /*
     * Main session loop
     */
    @Override
    public void run()
    {
        try
        {
            final SocketStreamSplitter sss = new SocketStreamSplitter(socket.getInputStream(), SEPARATOR);
            while (true)
            {
                try
                {
                    final String msg = sss.readNext();
                    if (msg == null)
                    {
                        OutputHandler.felog.warning("[remote] Connection closed: " + getRemoteHostname());
                        break;
                    }
                    processMessage(msg);
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
     * @throws IOException
     */
    protected void processMessage(String message) throws IOException
    {
        try
        {
            Type type = new TypeToken<RemoteRequest<JsonElement>>() {/**/
            }.getType();
            RemoteRequest<JsonElement> request = getGson().fromJson(message, type);

            OutputHandler.felog.info(String.format("[remote] Request [%s]: %s", request.id, request.data.toString()));

            if (request.auth != null)
            {
                userIdent = new UserIdent(request.auth.username);
                if (!userIdent.hasUUID())
                    userIdent = null;
                else
                {
                    String password = "password";
                    if (!request.auth.password.equals(password))
                    {
                        close("authentication failed", request.rid);
                        return;
                    }
                }
            }

            if (userIdent == null && !ModuleRemote.getInstance().allowUnauthenticatedAccess())
            {
                close("need authentication", request.rid);
                return;
            }

            RemoteHandler handler = ModuleRemote.getInstance().getHandler(request.id);
            if (handler == null)
            {
                sendMessage(new RemoteResponse.Error(request.rid, "unknown message identifie"));
            }
            else
            {
                sendMessage(handler.handle(this, request));
            }
        }
        catch (IllegalArgumentException e)
        {
            OutputHandler.felog.warning("[remote] Message error: " + e.getMessage());
        }
        catch (JsonSyntaxException e)
        {
            OutputHandler.felog.warning("[remote] Message error: " + e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.forgeessentials.api.remote.RemoteSession#sendMessage(java.lang.Object)
     */
    @Override
    public void sendMessage(RemoteResponse obj) throws IOException
    {
        OutputStreamWriter ow = new OutputStreamWriter(socket.getOutputStream());
        ow.write(getGson().toJson(obj) + SEPARATOR);
        ow.flush();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.forgeessentials.api.remote.RemoteSession#getRemoteHostname()
     */
    @Override
    public String getRemoteHostname()
    {
        return ((InetSocketAddress) socket.getRemoteSocketAddress()).getHostName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.forgeessentials.api.remote.RemoteSession#getRemoteHostname()
     */
    @Override
    public String getRemoteAddress()
    {
        return ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().getHostAddress();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.forgeessentials.api.remote.RemoteSession#getRemoteHostname()
     */
    @Override
    public UserIdent getUserIdent()
    {
        return userIdent;
    }

    /**
     * Terminates the session
     */
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

    /**
     * Terminates the session
     * 
     * @throws IOException
     */
    public void close(String error, int rid) throws IOException
    {
        OutputHandler.felog.warning(String.format("[remote] Error: %s. Terminating session to %s", error, getRemoteAddress()));
        sendMessage(new RemoteResponse.Error(rid, error));
        close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.forgeessentials.api.remote.RemoteSession#isClosed()
     */
    @Override
    public boolean isClosed()
    {
        return socket.isClosed();
    }

    /**
     * Get the Gson instance from ModuleRemote
     */
    public Gson getGson()
    {
        return ModuleRemote.getInstance().getGson();
    }

}
