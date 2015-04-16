package com.forgeessentials.remote.handler.permission;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.google.gson.JsonElement;

public class QueryPermissionsHandler extends GenericRemoteHandler<JsonElement> {

    public static final String ID = "query_permission";

    public static final String PERM = PERM_REMOTE + ".permission";

    public QueryPermissionsHandler()
    {
        super(ID, null, JsonElement.class);
    }

    @Override
    protected RemoteResponse<ServerZone> handleData(RemoteSession session, RemoteRequest<JsonElement> request)
    {
        return new RemoteResponse<ServerZone>(request, APIRegistry.perms.getServerZone());
    }

}
