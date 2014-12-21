package com.forgeessentials.remote.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.api.remote.data.DataFloatLocation;
import com.forgeessentials.util.UserIdent;

public class QueryPlayerHandler extends GenericRemoteHandler<QueryPlayerHandler.Request> {

    public static final String ID = "query_player";

    public static final String PERM = RemoteHandler.PERM + ".query.player";
    public static final String PERM_LOCATION = PERM + ".location";
    public static final String PERM_DETAIL = PERM + ".detail";

    public QueryPlayerHandler()
    {
        super(ID, PERM, QueryPlayerHandler.Request.class);
        APIRegistry.perms.registerPermission(PERM, RegisteredPermValue.OP, "Allows querying player data");
    }

    @Override
    protected RemoteResponse handleData(RemoteSession session, RemoteRequest<QueryPlayerHandler.Request> request)
    {
        UserIdent ident = new UserIdent(request.data.username);
        if (!ident.hasPlayer())
            error("player not found");

        Response response = new Response(ident.getUuid().toString(), ident.getUsername());
        for (String flag : request.data.flags)
        {
            switch (flag)
            {
            case "location":
                checkPermission(session, PERM_LOCATION);
                response.data.put(flag, new DataFloatLocation(ident.getPlayer()));
                break;
            case "detail":
                checkPermission(session, PERM_DETAIL);
                response.data.put("health", ident.getPlayer().getHealth());
                response.data.put("armor", ident.getPlayer().getTotalArmorValue());
                response.data.put("hunger", ident.getPlayer().getFoodStats().getFoodLevel());
                response.data.put("saturation", ident.getPlayer().getFoodStats().getSaturationLevel());
                break;
            }
        }

        return new RemoteResponse<QueryPlayerHandler.Response>(request, response);
    }

    public static class Request {

        public String username;

        public Set<String> flags;

        public Request(String username, String... flags)
        {
            this.username = username;
            int i = flags.length;
            for (int j = 0; j < i; ++j)
            {
                this.flags.add(flags[i]);
            }
        }
    }

    public static class Response {

        public String uuid;

        public String username;

        public Map<String, Object> data = new HashMap<>();

        public Response(String uuid, String username)
        {
            this.uuid = uuid;
            this.username = username;
        }
    }

}
