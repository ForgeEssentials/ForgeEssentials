package com.forgeessentials.remote.handler.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

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
import com.forgeessentials.util.ServerUtil;
import com.google.gson.JsonPrimitive;

@FERemoteHandler(id = RemoteMessageID.QUERY_PLAYER)
public class QueryPlayerHandler extends GenericRemoteHandler<QueryPlayerRequest>
{

    public static final String FLAG_LOCATION = "location";
    public static final String FLAG_DETAIL = "detail";

    public static final String PERM = PERM_REMOTE + ".player.query";
    public static final String PERM_LOCATION = PERM + '.' + FLAG_LOCATION;
    public static final String PERM_DETAIL = PERM + '.' + FLAG_DETAIL;

    public QueryPlayerHandler()
    {
        super(PERM, QueryPlayerRequest.class);
        APIRegistry.perms.registerPermission(PERM, PermissionLevel.OP, "Allows querying player data");
        APIRegistry.perms.registerPermission(PERM_LOCATION, PermissionLevel.OP, "View location");
        APIRegistry.perms.registerPermission(PERM_DETAIL, PermissionLevel.OP, "View details (health, armor, etc.)");
    }

    @Override
    protected RemoteResponse<QueryPlayerResponse> handleData(RemoteSession session, RemoteRequest<QueryPlayerRequest> request)
    {
        if (request.data != null && request.data.flags != null)
            for (Iterator<String> it = request.data.flags.iterator(); it.hasNext();)
            {
                String flag = it.next();
                if (!APIRegistry.perms.checkUserPermission(session.getUserIdent(), PERM + '.' + flag))
                    it.remove();
            }

        QueryPlayerResponse response = new QueryPlayerResponse();
        if (request.data == null || request.data.name == null)
        {
            for (EntityPlayerMP player : ServerUtil.getPlayerList())
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
            case FLAG_LOCATION:
                pi.data.put(flag, session.getGson().toJsonTree(new DataFloatLocation(ident.getPlayerMP())));
                break;
            case FLAG_DETAIL:
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
