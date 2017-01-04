package com.forgeessentials.remote.handler;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.ModuleRemote;
import com.forgeessentials.remote.RemoteMessageID;
import com.google.gson.JsonElement;

@FERemoteHandler(id = RemoteMessageID.QUERY_REMOTE_CAPABILITIES)
public class QueryRemoteCapabilitiesHandler extends GenericRemoteHandler<JsonElement>
{

    // public static final String PERM = PERM_REMOTE + ".query.remote.capabilities";

    public QueryRemoteCapabilitiesHandler()
    {
        super(null, JsonElement.class);
        // APIRegistry.perms.registerPermission(PERM, DefaultPermissionLevel.ALL,
        // "Allows querying capabilities (allowed handlers - should ALWAYS be granted)");
    }

    @Override
    protected RemoteResponse<QueryRemoteCapabilitiesHandler.Response> handleData(RemoteSession session, RemoteRequest<JsonElement> request)
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

    public static class Response
    {

        public Set<String> handlers = new HashSet<>();

    }

}
