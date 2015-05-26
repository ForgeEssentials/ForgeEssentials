package com.forgeessentials.remote.handler.chat;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.remote.FERemoteHandler;
import com.forgeessentials.api.remote.GenericRemoteHandler;
import com.forgeessentials.api.remote.RemoteRequest;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.remote.handler.RemoteMessageID;
import com.forgeessentials.util.OutputHandler;

@FERemoteHandler(id = RemoteMessageID.SEND_CHAT)
public class ActionChatHandler extends GenericRemoteHandler<String>
{

    public static final String PERM = PERM_REMOTE + ".chat.send";

    public ActionChatHandler()
    {
        super(PERM, String.class);
        APIRegistry.perms.registerPermission(PERM, RegisteredPermValue.TRUE, "Allows to send chat messages");
    }

    @Override
    protected RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<String> request)
    {
        if (request.data == null)
            error("Missing message");

        EntityPlayerMP player = session.getUserIdent().getFakePlayer(MinecraftServer.getServer().worldServers[0]);
        String cmd = String.format("/msg %s ", player.getCommandSenderName());
        IChatComponent header = ModuleChat.clickChatComponent(player.getCommandSenderName(), Action.SUGGEST_COMMAND, cmd);
        ChatComponentTranslation chatComponent = new ChatComponentTranslation("chat.type.text", header, request.data);
        ServerChatEvent event = new ServerChatEvent(player, request.data, chatComponent);
        if (!MinecraftForge.EVENT_BUS.post(event))
            OutputHandler.broadcast(event.component);
        return success(request);
    }

}
