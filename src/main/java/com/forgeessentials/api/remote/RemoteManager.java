package com.forgeessentials.api.remote;

public interface RemoteManager {

    void registerHandler(RemoteHandler handler);

    RemoteHandler getHandler(String id);

    public static class DefaultRemoteHandlerManager implements RemoteManager {

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.forgeessentials.api.remote.RemoteManager#registerHandler(com.forgeessentials.api.remote.RemoteHandler)
         */
        @Override
        public void registerHandler(RemoteHandler handler)
        {
            /* do nothing */
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.forgeessentials.api.remote.RemoteManager#getHandler(java.lang.String)
         */
        @Override
        public RemoteHandler getHandler(String id)
        {
            return null;
        }

    }

}
