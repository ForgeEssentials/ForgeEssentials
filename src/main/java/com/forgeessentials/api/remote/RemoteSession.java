package com.forgeessentials.api.remote;

import java.io.IOException;

import com.forgeessentials.util.UserIdent;

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
    void sendMessage(RemoteResponse obj) throws IOException;

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
