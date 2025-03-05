package com.forgeessentials.remote.handler.chat;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.RemoteMessageID;
import com.forgeessentials.util.output.ChatOutputHandler;

@FERemoteHandler(id = RemoteMessageID.CHAT)
public class SendChatHandler extends GenericRemoteHandler<String>
{

    public static final String PERM = PERM_REMOTE + ".chat.send";

    public SendChatHandler()
    {
        super(PERM, String.class);
        APIRegistry.perms.registerPermissionDescription(PERM, "Allows to send chat messages");
        APIRegistry.perms.getServerZone().setGroupPermission(Zone.GROUP_DEFAULT, PERM, false);
        APIRegistry.perms.getServerZone().setGroupPermission(Zone.GROUP_PLAYERS, PERM, true);
    }

    @Override
    protected RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<String> request)
    {
        if (request.data == null)
            error("Missing message");
        UserIdent ident = session.getUserIdent();
        if (ident != null)
        {
            ServerPlayer player = ident.getPlayerMP();
            TranslatableComponent message = new TranslatableComponent("chat.type.text",
                    new Object[] { player.getDisplayName().getString(), ForgeHooks.newChatWithLinks(request.data) });
            ServerChatEvent event = new ServerChatEvent(player, request.data, message);
            if (MinecraftForge.EVENT_BUS.post(event))
                return null;
            if (event.getMessage() != null)
                ChatOutputHandler.broadcast(event.getMessage());
        }
        else
        {
            TranslatableComponent message = new TranslatableComponent("chat.type.text",
                    new Object[] { "anonymous", ForgeHooks.newChatWithLinks(request.data) });
            ChatOutputHandler.broadcast(message);
            QueryChatHandler.onMessage(message);
            PushChatHandler.onMessage(message, "anonymous");
        }
        return null;
    }

}
