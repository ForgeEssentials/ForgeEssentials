package com.forgeessentials.api.remote;

import com.forgeessentials.api.APIRegistry;

/**
 *
 */
public abstract class AbstractRemoteHandler implements RemoteHandler {

    private final String id;

    public AbstractRemoteHandler(String id)
    {
        this.id = id;
    }

    @Override
    public String getID()
    {
        return id;
    }

    public void register()
    {
        APIRegistry.remoteManager.registerHandler(this);
    }

}
