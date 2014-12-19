package com.forgeessentials.remote.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.api.remote.data.DataFloatLocation;
import com.forgeessentials.remote.handler.GetPlayerHandler.Request;
import com.forgeessentials.remote.handler.GetPlayerHandler.Response;
import com.forgeessentials.util.UserIdent;

public class GetPlayerHandler extends GenericRemoteHandler<Request> {

    public GetPlayerHandler()
    {
        super(Request.class, "get_player");
    }

    @Override
    protected RemoteResponse handleData(RemoteSession session, RemoteRequest<Request> request)
    {
        UserIdent ident = new UserIdent(request.data.username);
        if (!ident.hasPlayer())
            return new RemoteResponse.Error(request.rid, "player not found");

        Response response = new Response(ident.getUuid().toString(), ident.getUsername());
        for (String flag : request.data.flags)
        {
            switch (flag)
            {
            case "location":
                response.data.put(flag, new DataFloatLocation(ident.getPlayer()));
                break;
            case "detail":
                response.data.put("health", ident.getPlayer().getHealth());
                response.data.put("armor", ident.getPlayer().getTotalArmorValue());
                response.data.put("hunger", ident.getPlayer().getFoodStats().getFoodLevel());
                response.data.put("saturation", ident.getPlayer().getFoodStats().getSaturationLevel());
                break;
            }
        }

        return new RemoteResponse<Response>(response);
    }

    public static class Request {

        public String username;

        public Set<String> flags;

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
