package com.forgeessentials.remote.handler.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.api.remote.data.DataFloatLocation;
import com.forgeessentials.remote.RemoteMessageID;
import com.forgeessentials.remote.network.QueryPlayerRequest;
import com.forgeessentials.util.ServerUtil;
import com.google.gson.JsonElement;
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
        APIRegistry.perms.registerPermission(PERM, DefaultPermissionLevel.OP, "Allows querying player data");
        APIRegistry.perms.registerPermission(PERM_LOCATION, DefaultPermissionLevel.OP, "View location");
        APIRegistry.perms.registerPermission(PERM_DETAIL, DefaultPermissionLevel.OP, "View details (health, armor, etc.)");
    }

    @Override
    protected RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<QueryPlayerRequest> request)
    {
        if (request.data != null && request.data.flags != null)
            for (Iterator<String> it = request.data.flags.iterator(); it.hasNext();)
            {
                String flag = it.next();
                if (!APIRegistry.perms.checkUserPermission(session.getUserIdent(), PERM + '.' + flag))
                    it.remove();
            }

        Map<UUID, Map<String, JsonElement>> players = new HashMap<>();
        if (request.data == null || request.data.name == null)
        {
            for (EntityPlayerMP player : ServerUtil.getPlayerList())
            {
                UserIdent ident = UserIdent.get(player);
                players.put(ident.getUuid(), getPlayerInfoResponse(session, ident, request.data == null ? null : request.data.flags));
            }
        }
        else
        {
            UserIdent ident = UserIdent.get(request.data.name);
            if (!ident.hasPlayer())
                error("player not found");
            players.put(ident.getUuid(), getPlayerInfoResponse(session, ident, request.data.flags));
        }
        return new RemoteResponse<Object>(request, players);
    }

    public Map<String, JsonElement> getPlayerInfoResponse(RemoteSession session, UserIdent ident, Set<String> flags)
    {
        Map<String, JsonElement> pi = new HashMap<>();
        pi.put("name", new JsonPrimitive(ident.getUsername()));
        if (flags == null)
            return pi;
        for (String flag : flags)
        {
            switch (flag)
            {
            case FLAG_LOCATION:
                pi.put(flag, session.getGson().toJsonTree(new DataFloatLocation(ident.getPlayerMP())));
                break;
            case FLAG_DETAIL:
                pi.put("health", new JsonPrimitive(ident.getPlayerMP().getHealth()));
                pi.put("armor", new JsonPrimitive(ident.getPlayerMP().getTotalArmorValue()));
                pi.put("hunger", new JsonPrimitive(ident.getPlayerMP().getFoodStats().getFoodLevel()));
                pi.put("saturation", new JsonPrimitive(ident.getPlayerMP().getFoodStats().getSaturationLevel()));
                break;
            }
        }
        return pi;
    }

}
