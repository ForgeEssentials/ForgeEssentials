package com.forgeessentials.remote.handler.chat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.RemoteMessageID;
import com.forgeessentials.remote.handler.chat.QueryChatHandler.Request;
import com.forgeessentials.util.output.ChatOutputHandler.ChatFormat;

@FERemoteHandler(id = RemoteMessageID.QUERY_CHAT)
public class QueryChatHandler extends GenericRemoteHandler<Request>
{

    private static final int BUFFER_SIZE = 200;

    public static final String PERM = PERM_REMOTE + ".chat.query";

    private static Map<Long, IChatComponent> chatLog = new TreeMap<>();

    public QueryChatHandler()
    {
        super(PERM, Request.class);
        APIRegistry.perms.registerPermission(PERM, PermissionLevel.TRUE, "Allows querying chat messages");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public synchronized RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<Request> request)
    {
        ChatFormat format = request.data == null ? ChatFormat.PLAINTEXT : ChatFormat.fromString(request.data.format);
        Map<Long, Object> messages = new HashMap<>();
        for (Entry<Long, IChatComponent> message : chatLog.entrySet())
        {
            if (request.data != null && message.getKey() < request.data.timestamp)
                continue;
            messages.put(message.getKey(), format.format(message.getValue()));
        }
        return new RemoteResponse<Map<?, ?>>(request, messages);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public synchronized void chatEvent(ServerChatEvent event)
    {
        onMessage(event.getComponent());
    }

    public static void onMessage(IChatComponent message)
    {
        Long key = System.currentTimeMillis();
        while (chatLog.containsKey(key))
            key++;
        chatLog.put(key, message);
        while (chatLog.size() > BUFFER_SIZE)
        {
            Iterator<?> it = chatLog.entrySet().iterator();
            it.next();
            it.remove();
        }
    }

    public static class Request
    {

        public long timestamp;

        public String format;

    }

}
