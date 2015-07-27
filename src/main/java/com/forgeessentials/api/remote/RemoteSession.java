package com.forgeessentials.api.remote;

import java.io.IOException;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.remote.RemoteRequest.JsonRemoteRequest;
import com.google.gson.Gson;

/**
 *
 */
public interface RemoteSession
{

    /**
     * Sends a message to the client. Throws a {@link SessionClosedException}, if the session was already closed.
     * 
     * @param message
     * @throws IOException
     */
    void sendMessage(RemoteResponse<?> message) throws IOException;

    /**
     * Sends a message to the client. Throws a {@link SessionClosedException}, if the session was already closed.
     * 
     * @param message
     */
    boolean trySendMessage(RemoteResponse<?> message);

    /**
     * Transforms a generic request into one with the correctly deserialized data
     * 
     * @param request
     * @param clazz
     */
    <T> RemoteRequest<T> transformRemoteRequest(JsonRemoteRequest request, Class<T> clazz);

    /**
     * Returns the hostname of the remote client
     */
    String getRemoteHostname();

    /**
     * Returns the IP address of the remote client
     */
    String getRemoteAddress();

    /**
     * Gets the UserIdent of the authenticated user
     */
    UserIdent getUserIdent();

    /**
     * Closes the session
     */
    void close();

    /**
     * Closes the session
     */
    void close(String reason, int rid);

    /**
     * Checks, if the session was closed
     */
    boolean isClosed();

    /**
     * Get the Gson instance of the remote manager
     */
    Gson getGson();

    /**
     * Gets the remote manager of this session
     */
    RemoteManager getRemoteManager();

    /**
     * Thrown, when a message should be sent to the remote-client, but the session was already terminated
     */
    public static class SessionClosedException extends Exception
    {
        private static final long serialVersionUID = -7782278063344870691L;

        private final RemoteSession session;

        public SessionClosedException(RemoteSession session)
        {
            this.session = session;
        }

        public RemoteSession getSession()
        {
            return session;
        }

    }

}
