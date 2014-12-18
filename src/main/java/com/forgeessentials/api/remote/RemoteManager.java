package com.forgeessentials.api.remote;

public interface RemoteManager {

    void registerHandler(RemoteHandler handler);

    public static class DefaultRemoteHandlerManager implements RemoteManager {

        @Override
        public void registerHandler(RemoteHandler handler)
        {
            /* do nothing */
        }

    }

}
