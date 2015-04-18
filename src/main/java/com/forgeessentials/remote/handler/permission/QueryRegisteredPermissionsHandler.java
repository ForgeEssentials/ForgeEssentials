package com.forgeessentials.remote.handler.permission;

import java.util.Collection;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.google.gson.JsonElement;

@FERemoteHandler(id = "query_permission_registered")
public class QueryRegisteredPermissionsHandler extends GenericRemoteHandler<JsonElement> {

    public QueryRegisteredPermissionsHandler()
    {
        super(null, JsonElement.class);
    }

    @Override
    protected RemoteResponse<Collection<String>> handleData(RemoteSession session, RemoteRequest<JsonElement> request)
    {
        return new RemoteResponse<Collection<String>>(request, APIRegistry.perms.getServerZone().getRootZone().enumRegisteredPermissions());
    }

}
