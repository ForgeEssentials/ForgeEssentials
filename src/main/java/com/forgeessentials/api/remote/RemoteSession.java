package com.forgeessentials.api.remote;

import java.io.IOException;

import com.forgeessentials.util.UserIdent;
import com.google.gson.JsonElement;

/**
 *
 */
public interface RemoteSession {

    /**
     * Sends a message to the client. Throws a {@link SessionClosedException}, if the session was already closed.
     * 
     * @param obj
     * @throws SessionClosedException
     * @throws IOException
     */
    void sendMessage(RemoteResponse response) throws IOException;

    /**
     * Sends a message to the client. Throws a {@link SessionClosedException}, if the session was already closed.
     * 
     * @param obj
     * @throws SessionClosedException
     * @throws IOException
     */
    boolean trySendMessage(RemoteResponse response);

    /**
     * Transforms a generic request into one with the correctly deserialized data
     * 
     * @param request
     * @param clazz
     */
    <T> RemoteRequest<T> transformRemoteRequest(RemoteRequest<JsonElement> request, Class<T> clazz);

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
     * Thrown, when a message should be sent to the remote-client, but the session was already terminated
     */
    public static class SessionClosedException extends Exception {

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
