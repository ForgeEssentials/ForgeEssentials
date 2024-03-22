package com.forgeessentials.remote.handler.chat;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.RemoteMessageID;
import com.forgeessentials.remote.handler.chat.PushChatHandler.Request;
import com.forgeessentials.remote.network.ChatResponse;
import com.forgeessentials.util.output.ChatOutputHandler.ChatFormat;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.BaseComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

@FERemoteHandler(id = RemoteMessageID.PUSH_CHAT)
public class PushChatHandler extends GenericRemoteHandler<Request>
{

    public static final String PERM = PERM_REMOTE + ".chat.push";

    protected Map<RemoteSession, ChatFormat> formats = new WeakHashMap<>();

    protected static PushChatHandler instance;

    public PushChatHandler()
    {
        super(PERM, Request.class);
        APIRegistry.perms.registerPermission(PERM, DefaultPermissionLevel.ALL, "Allows requesting chat push messages");
        MinecraftForge.EVENT_BUS.register(this);
        instance = this;
    }

    @Override
    public synchronized RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<Request> request)
    {
        if (request.data.format != null)
            formats.put(session, ChatFormat.fromString(request.data.format));
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
        Component message = event.getComponent();
        String username = event.getUsername();
        pushMessage(message, username);
    }

    protected void pushMessage(Component message, String username)
    {
        RemoteResponse<?>[] messages = new RemoteResponse<?>[ChatFormat.values().length];
        if (!pushSessions.isEmpty())
        {
            Iterator<RemoteSession> it = pushSessions.iterator();
            while (it.hasNext())
            {
                RemoteSession session = it.next();
                if (session.isClosed())
                {
                    it.remove();
                    continue;
                }
                ChatFormat format = formats.get(session);
                if (format == null)
                    format = ChatFormat.PLAINTEXT;
                if (messages[format.ordinal()] == null)
                {
                    BaseComponent mes = new TextComponent("");
                    format.format(mes);
                    messages[format.ordinal()] = new RemoteResponse<>(RemoteMessageID.CHAT,
                            new ChatResponse(username, format.format(mes)));
                }
                session.trySendMessage(messages[format.ordinal()]);
            }
        }
    }

    public static void onMessage(Component message, String username)
    {
        instance.pushMessage(message, username);
    }

    public static class Request
    {

        public boolean enable;

        public String format;

        public Request(boolean enable, String format)
        {
            this.enable = enable;
            this.format = format;
        }

    }

}
