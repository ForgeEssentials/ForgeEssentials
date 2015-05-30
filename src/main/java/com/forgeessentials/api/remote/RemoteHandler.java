package com.forgeessentials.api.remote;

import com.forgeessentials.api.remote.RemoteRequest.JsonRemoteRequest;

/**
 *
 */
public interface RemoteHandler
{

    public static final String PERM_REMOTE = "fe.remote";

    public static final String MSG_NO_PERMISSION = "no permission";

    public static final String MSG_EXCEPTION = "exception";

    String getPermission();

    RemoteResponse<?> handle(RemoteSession session, JsonRemoteRequest request);

    public static class RemoteException extends RuntimeException
    {
        private static final long serialVersionUID = -1742976516313756832L;

        public RemoteException(String message)
        {
            super(message);
        }

        public RemoteException(String message, Object... args)
        {
            super(String.format(message, args));
        }

        public RemoteException()
        {
            super(MSG_EXCEPTION);
        }

    }

    public static class PermissionException extends RemoteException
    {
        private static final long serialVersionUID = 4094169554447919502L;

        public PermissionException()
        {
            super(MSG_NO_PERMISSION);
        }

    }

}
