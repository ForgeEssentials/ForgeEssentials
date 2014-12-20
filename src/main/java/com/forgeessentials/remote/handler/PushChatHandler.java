package com.forgeessentials.remote.handler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;

import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PushChatHandler extends GenericRemoteHandler<PushChatHandler.Request> {

    public static final String ID = "push_chat";

    protected Set<RemoteSession> pushSessions = new HashSet<>();

    public PushChatHandler()
    {
        super(ID, PushChatHandler.Request.class);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public synchronized RemoteResponse handleData(RemoteSession session, RemoteRequest<PushChatHandler.Request> request)
    {
        if (pushSessions.contains(session) ^ !request.data.enable)
            return RemoteResponse.error(request, "chat push already " + (request.data.enable ? "enabled" : "disabled"));
        if (request.data.enable)
            pushSessions.add(session);
        else
            pushSessions.remove(session);
        return RemoteResponse.ok(request);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public synchronized void chatEvent(ServerChatEvent event)
    {
        RemoteResponse<Response> response = new RemoteResponse<>(getID(), new Response(event.username, event.message));
        Iterator<RemoteSession> it = pushSessions.iterator();
        while (it.hasNext())
        {
            RemoteSession session = it.next();
            if (session.isClosed())
            {
                it.remove();
                continue;
            }
            session.trySendMessage(response);
        }
    }

    public static class Request {

        public boolean enable;

        public Request(boolean enable)
        {
            this.enable = enable;
        }
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
