package com.forgeessentials.remote.handler.permission;

import java.util.Collection;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.google.gson.JsonElement;

public class QueryRegisteredPermissionsHandler extends GenericRemoteHandler<JsonElement> {

    public static final String ID = "query_permission_registered";

    public QueryRegisteredPermissionsHandler()
    {
        super(ID, null, JsonElement.class);
    }

    @Override
    protected RemoteResponse<QueryRegisteredPermissionsHandler.Response> handleData(RemoteSession session, RemoteRequest<JsonElement> request)
    {
        Response response = new Response();
        response.permissions = APIRegistry.perms.getServerZone().getRootZone().enumRegisteredPermissions();
        return new RemoteResponse<QueryRegisteredPermissionsHandler.Response>(request, response);
    }

    public static class Response {

        public Collection<String> permissions;

    }

}
