package com.forgeessentials.remote.handler.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.RemoteMessageID;
import com.forgeessentials.remote.handler.chat.QueryChatHandler.Request;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FERemoteHandler(id = RemoteMessageID.QUERY_CHAT)
public class QueryChatHandler extends GenericRemoteHandler<Request>
{

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
        ChatFormat format = ChatFormat.PLAINTEXT;
        if (request.data != null && request.data.format != null && !request.data.format.isEmpty())
        {
            try
            {
                format = ChatFormat.valueOf(request.data.format.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                /* do nothing */
            }
        }

        Map<Long, Object> messages = new HashMap<>();
        for (Entry<Long, IChatComponent> message : chatLog.entrySet())
        {
            if (request.data != null && message.getKey() < request.data.timestamp)
                continue;
            Object formattedMessage;
            switch (format)
            {
            case HTML:
                formattedMessage = ChatOutputHandler.formatHtml(message.getValue());
                break;
            case MINECRAFT:
                formattedMessage = ChatOutputHandler.getFormattedMessage(message.getValue());
                break;
            case DETAIL:
                formattedMessage = message;
                break;
            default:
            case PLAINTEXT:
                formattedMessage = ChatOutputHandler.stripFormatting(ChatOutputHandler.getUnformattedMessage(message.getValue()));
                break;
            }
            messages.put(message.getKey(), formattedMessage);
        }
        return new RemoteResponse<Map<?, ?>>(request, messages);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public synchronized void chatEvent(ServerChatEvent event)
    {
        Long key = System.currentTimeMillis();
        while (chatLog.containsKey(key))
            key++;
        chatLog.put(key, event.component);
    }

    public static enum ChatFormat
    {

        PLAINTEXT, HTML, MINECRAFT, DETAIL;

    }

    public static class Request
    {

        public long timestamp;

        public String format;

    }

}
