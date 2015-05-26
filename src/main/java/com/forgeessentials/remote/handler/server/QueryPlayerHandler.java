package com.forgeessentials.remote.handler.server;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.api.remote.data.DataFloatLocation;
import com.forgeessentials.remote.RemoteMessageID;
import com.forgeessentials.remote.network.PlayerInfoResponse;
import com.forgeessentials.remote.network.QueryPlayerRequest;
import com.forgeessentials.remote.network.QueryPlayerResponse;
import com.forgeessentials.util.FunctionHelper;
import com.google.gson.JsonPrimitive;

@FERemoteHandler(id = RemoteMessageID.QUERY_PLAYER)
public class QueryPlayerHandler extends GenericRemoteHandler<QueryPlayerRequest>
{

    public static final String PERM = PERM_REMOTE + ".query.player";
    public static final String PERM_LOCATION = PERM + ".location";
    public static final String PERM_DETAIL = PERM + ".detail";

    public QueryPlayerHandler()
    {
        super(PERM, QueryPlayerRequest.class);
        APIRegistry.perms.registerPermission(PERM, RegisteredPermValue.OP, "Allows querying player data");
    }

    @Override
    protected RemoteResponse<QueryPlayerResponse> handleData(RemoteSession session, RemoteRequest<QueryPlayerRequest> request)
    {
        if (request.data != null && request.data.flags != null)
            for (String flag : request.data.flags)
            {
                switch (flag)
                {
                case "location":
                    checkPermission(session, PERM_LOCATION);
                    break;
                case "detail":
                    checkPermission(session, PERM_DETAIL);
                    break;
                }
            }

        QueryPlayerResponse response = new QueryPlayerResponse();
        if (request.data == null || request.data.name == null)
        {
            for (EntityPlayerMP player : FunctionHelper.getPlayerList())
                response.players.add(getPlayerInfoResponse(session, UserIdent.get(player), request.data == null ? null : request.data.flags));
        }
        else
        {
            UserIdent ident = UserIdent.get(request.data.name);
            if (!ident.hasPlayer())
                error("player not found");
            response.players.add(getPlayerInfoResponse(session, ident, request.data.flags));
        }
        return new RemoteResponse<QueryPlayerResponse>(request, response);
    }

    public PlayerInfoResponse getPlayerInfoResponse(RemoteSession session, UserIdent ident, Set<String> flags)
    {
        PlayerInfoResponse pi = new PlayerInfoResponse(ident.getUuid().toString(), ident.getUsername());
        if (flags == null)
            return pi;
        pi.data = new HashMap<>();
        for (String flag : flags)
        {
            switch (flag)
            {
            case "location":
                pi.data.put(flag, session.getGson().toJsonTree(new DataFloatLocation(ident.getPlayerMP())));
                break;
            case "detail":
                pi.data.put("health", new JsonPrimitive(ident.getPlayerMP().getHealth()));
                pi.data.put("armor", new JsonPrimitive(ident.getPlayerMP().getTotalArmorValue()));
                pi.data.put("hunger", new JsonPrimitive(ident.getPlayerMP().getFoodStats().getFoodLevel()));
                pi.data.put("saturation", new JsonPrimitive(ident.getPlayerMP().getFoodStats().getSaturationLevel()));
                break;
            }
        }
        return pi;
    }

}
