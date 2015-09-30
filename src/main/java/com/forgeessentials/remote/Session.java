package com.forgeessentials.remote;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.remote.RemoteHandler;
import com.forgeessentials.api.remote.RemoteHandler.PermissionException;
import com.forgeessentials.api.remote.RemoteHandler.RemoteException;
import com.forgeessentials.api.remote.RemoteManager;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteRequest.JsonRemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 *
 */
public class Session implements Runnable, RemoteSession
{

    private static final String SEPARATOR = "\n\n\n";

    private final Socket socket;

    private final Thread thread;

    private UserIdent ident;

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
                        // LoggingHandler.felog.warn("[remote] Connection closed: " + getRemoteHostname());
                        break;
                    }
                    processMessage(msg);
                }
                catch (IOException e)
                {
                    LoggingHandler.felog.debug("[remote] Socket error: " + e.getMessage());
                    break;
                }
            }
        }
        catch (IOException e)
        {
            LoggingHandler.felog.warn("[remote] Error opening input stream.");
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
            JsonRemoteRequest request = getGson().fromJson(message, JsonRemoteRequest.class);

            LoggingHandler.felog.debug(String.format("[remote] Request [%s]: %s", request.id, request.data == null ? "null" : request.data.toString()));

            if (request.auth != null)
            {
                ident = UserIdent.get(request.auth.username);
                if (!ModuleRemote.getInstance().getPasskey(ident).equals(request.auth.password))
                {
                    close("authentication failed", request);
                    return;
                }
            }

            // Check if user was banned
            if (ident != null && MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isBanned(ident.getGameProfile()))
            {
                close("banned", request);
                return;
            }

            // Check for remote permission
            if (!APIRegistry.perms.checkUserPermission(ident, ModuleRemote.PERM))
            {
                close(ident == null ? "need authentication" : "access denied", request);
                return;
            }

            // Get the correct remote handler
            RemoteHandler handler = ModuleRemote.getInstance().getHandler(request.id);
            if (handler == null)
            {
                sendMessage(RemoteResponse.error(request, "unknown message identifier"));
                return;
            }

            // Check permission for remote handler
            String p = handler.getPermission();
            if (p != null && !APIRegistry.perms.checkUserPermission(ident, p))
            {
                sendMessage(RemoteResponse.error(request, RemoteHandler.MSG_NO_PERMISSION));
                return;
            }

            // Handle request
            try
            {
                RemoteResponse<?> response = handler.handle(this, request);
                if (response != null)
                {
                    response.id = request.id;
                    response.rid = request.rid;
                }
                else
                    response = RemoteResponse.success(request);
                if (!(response instanceof RemoteResponse.Ignore))
                    sendMessage(response);
            }
            catch (PermissionException e)
            {
                sendMessage(RemoteResponse.error(request, RemoteHandler.MSG_NO_PERMISSION));
                return;
            }
            catch (RemoteException e)
            {
                sendMessage(RemoteResponse.error(request, e.getMessage()));
                return;
            }
            catch (Exception e)
            {
                sendMessage(RemoteResponse.error(request, RemoteHandler.MSG_EXCEPTION));
                LoggingHandler.felog.warn("[remote] Exception while handling message");
                e.printStackTrace();
                return;
            }
        }
        catch (IllegalArgumentException | JsonSyntaxException e)
        {
            LoggingHandler.felog.warn("[remote] Message error: " + e.getMessage());
            sendMessage(RemoteResponse.error(null, 0, e.getMessage()));
            close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.forgeessentials.api.remote.RemoteSession#sendMessage(java.lang.Object)
     */
    @Override
    public synchronized void sendMessage(RemoteResponse<?> message) throws IOException
    {
        OutputStreamWriter ow = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        ow.write(getGson().toJson(message) + SEPARATOR);
        ow.flush();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.forgeessentials.api.remote.RemoteSession#sendMessage(java.lang.Object)
     */
    @Override
    public boolean trySendMessage(RemoteResponse<?> message)
    {
        if (isClosed())
            return false;
        try
        {
            sendMessage(message);
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.forgeessentials.api.remote.RemoteSession#transformRemoteRequest(com.forgeessentials.api.remote.RemoteRequest,
     * java.lang.Class)
     */
    @Override
    public <T> RemoteRequest<T> transformRemoteRequest(JsonRemoteRequest request, Class<T> clazz)
    {
        return RemoteRequest.transform(request, getGson().fromJson(request.data, clazz));
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
    public synchronized UserIdent getUserIdent()
    {
        return ident;
    }

    /**
     * Terminates the session
     */
    @Override
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
    @Override
    public void close(String reason, int rid)
    {
        trySendMessage(RemoteResponse.error("close", rid, reason));
        close();
    }

    /**
     * Terminates the session
     * 
     * @throws IOException
     */
    public void close(String error, RemoteRequest<?> request)
    {
        LoggingHandler.felog.warn(String.format("[remote] Error: %s. Terminating session to %s", error, getRemoteAddress()));
        trySendMessage(RemoteResponse.error("close", request.rid, error));
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
    @Override
    public Gson getGson()
    {
        return ModuleRemote.getInstance().getGson();
    }

    @Override
    public RemoteManager getRemoteManager()
    {
        return ModuleRemote.getInstance();
    }

}
