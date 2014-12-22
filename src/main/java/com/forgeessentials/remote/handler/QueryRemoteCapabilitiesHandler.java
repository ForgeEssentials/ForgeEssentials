package com.forgeessentials.remote.handler;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.ModuleRemote;
import com.google.gson.JsonElement;

public class QueryRemoteCapabilitiesHandler extends GenericRemoteHandler<JsonElement> {

    public static final String ID = "query_remote_capabilities";

    public static final String PERM = RemoteHandler.PERM + ".query.remote.capabilities";

    public QueryRemoteCapabilitiesHandler()
    {
        super(ID, PERM, JsonElement.class);
        APIRegistry.perms.registerPermission(PERM, RegisteredPermValue.TRUE, "Allows querying capabilities (allowed handlers - should ALWAYS be granted)");
    }

    @Override
    protected RemoteResponse handleData(RemoteSession session, RemoteRequest<JsonElement> request)
    {
        Response response = new Response();
        for (Entry<String, RemoteHandler> handler : ModuleRemote.getInstance().getHandlers().entrySet())
        {
            String p = handler.getValue().getPermission();
            if (p == null || APIRegistry.perms.checkUserPermission(session.getUserIdent(), p))
                response.handlers.add(handler.getKey());
        }
        return new RemoteResponse<QueryRemoteCapabilitiesHandler.Response>(request, response);
    }

    public static class Response {

        public Set<String> handlers = new HashSet<>();

    }

}
