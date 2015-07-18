package com.forgeessentials.remote.handler.chat;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentTranslation;
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
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.remote.RemoteMessageID;
import com.forgeessentials.util.output.ChatOutputHandler;

@FERemoteHandler(id = RemoteMessageID.CHAT)
public class SendChatHandler extends GenericRemoteHandler<String>
{

    public static final String PERM = PERM_REMOTE + ".chat.send";

    public SendChatHandler()
    {
        super(PERM, String.class);
        APIRegistry.perms.registerPermission(PERM, PermissionLevel.TRUE, "Allows to send chat messages");
    }

    @Override
    protected RemoteResponse<?> handleData(RemoteSession session, RemoteRequest<String> request)
    {
        if (request.data == null)
            error("Missing message");

        EntityPlayerMP player = (EntityPlayerMP) session.getUserIdent().getFakePlayer();
        String cmd = String.format("/msg %s ", player.getCommandSenderName());
        IChatComponent header = ModuleChat.clickChatComponent(player.getCommandSenderName(), Action.SUGGEST_COMMAND, cmd);
        ChatComponentTranslation chatComponent = new ChatComponentTranslation("chat.type.text", header, request.data);
        ServerChatEvent event = new ServerChatEvent(player, request.data, chatComponent);
        if (!MinecraftForge.EVENT_BUS.post(event))
            ChatOutputHandler.broadcast(event.component);
        return null;
    }

}
