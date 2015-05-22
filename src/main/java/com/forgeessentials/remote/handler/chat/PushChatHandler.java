package com.forgeessentials.remote.handler.chat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteRequest.PushRequestData;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FERemoteHandler(id = PushChatHandler.ID)
public class PushChatHandler extends GenericRemoteHandler<PushRequestData> {

    public static final String ID = "push_chat";

    public static final String PERM = PERM_REMOTE + ".push.chat";

    public PushChatHandler()
    {
        super(PERM, PushRequestData.class);
        APIRegistry.perms.registerPermission(PERM, RegisteredPermValue.TRUE, "Allows requesting chat push-messages");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public synchronized RemoteResponse<String> handleData(RemoteSession session, RemoteRequest<PushRequestData> request)
    {
        if (hasPushSession(session) ^ !request.data.enable)
            error("chat push already " + (request.data.enable ? "enabled" : "disabled"));
        if (request.data.enable)
            addPushSession(session);
        else
            removePushSession(session);
        return RemoteResponse.ok(request);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public synchronized void chatEvent(ServerChatEvent event)
    {
        push(new RemoteResponse<>(ID, new Response(event.username, event.message)));
    }

    public static class Response {

        public String username;

        public String message;

        public Response(String username, String message)
        {
            this.username = username;
            this.message = message;
        }
    }

}
