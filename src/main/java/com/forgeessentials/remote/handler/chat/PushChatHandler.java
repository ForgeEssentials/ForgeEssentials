package com.forgeessentials.remote.handler.chat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.RemoteMessageID;
import com.forgeessentials.remote.network.ChatResponse;
import com.forgeessentials.remote.network.EnableRequest;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FERemoteHandler(id = RemoteMessageID.PUSH_CHAT)
public class PushChatHandler extends GenericRemoteHandler<EnableRequest>
{

    public static final String PERM = PERM_REMOTE + ".chat.push";

    public PushChatHandler()
    {
        super(PERM, EnableRequest.class);
        APIRegistry.perms.registerPermission(PERM, RegisteredPermValue.TRUE, "Allows requesting chat push messages");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public synchronized RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<EnableRequest> request)
    {
        if (hasPushSession(session) ^ !request.data.enable)
            error("chat push already " + (request.data.enable ? "enabled" : "disabled"));
        if (request.data.enable)
            addPushSession(session);
        else
            removePushSession(session);
        return success(request);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public synchronized void chatEvent(ServerChatEvent event)
    {
        push(new RemoteResponse<>(RemoteMessageID.CHAT, new ChatResponse(event.username, event.message)));
    }

}
