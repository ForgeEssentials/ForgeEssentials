package com.forgeessentials.remote;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.network.ChatResponse;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class RemoteCommandSender extends DoAsCommandSender
{

    protected static Map<RemoteSession, RemoteCommandSender> sessions = new WeakHashMap<>();

    public static RemoteCommandSender get(RemoteSession session)
    {
        RemoteCommandSender result = sessions.get(session);
        if (result == null)
        {
            result = new RemoteCommandSender(session);
            sessions.put(session, result);
        }
        return result;
    }

    public static void unload(RemoteSession session)
    {
        sessions.remove(session);
        for (Iterator<RemoteSession> it = sessions.keySet().iterator(); it.hasNext();)
            if (it.next().isClosed())
                it.remove();
    }

    /* ------------------------------------------------------------ */

    protected RemoteSession session;

    private RemoteCommandSender(RemoteSession session)
    {
        super(session.getUserIdent());
        if (session.getUserIdent() != null)
            this.sender = session.getUserIdent().getFakePlayer().createCommandSourceStack();
        else
            this.sender = FakePlayerFactory.get(ServerUtil.getOverworld(), ModuleRemote.FAKEPLAYER)
                    .createCommandSourceStack();
        this.session = session;
    }

    public RemoteSession getSession()
    {
        return session;
    }

    @Override
    public UserIdent getUserIdent()
    {
        return session.getUserIdent();
    }

    @Override
    public Component getDisplayName()
    {
        return session.getUserIdent() != null ? new TextComponent(session.getUserIdent().getUsernameOrUuid())
                : new TextComponent("anonymous");
    }

    @Override
    public void sendMessage(Component chatComponent, UUID uuid)
    {
        // TODO: Instead of directly sending the messages to the client, cache them and
        // send them all after the running
        // command finished (only if enabled)
        CommandSourceStack receiver = ServerLifecycleHooks.getCurrentServer().createCommandSourceStack();
        if (session.getUserIdent() != null && session.getUserIdent().hasPlayer())
            receiver = session.getUserIdent().getPlayer().createCommandSourceStack();
        ChatOutputHandler.sendMessageI(receiver, chatComponent);

        if (!session.isClosed())
        {
            try
            {
                // TODO: Add second message WITH formatting
                ChatResponse msg = new ChatResponse(null,
                        ChatOutputHandler.stripFormatting(chatComponent.plainCopy().toString()));
                session.sendMessage(new RemoteResponse<>(RemoteMessageID.CHAT, msg));
            }
            catch (IOException e)
            {
                LoggingHandler.felog.error("Error sending remote message: " + e.getMessage());
                e.printStackTrace();
            }
        }

        ModuleRemote.getInstance().getServer().cleanSessions();
    }

    @Override
    public int getPermissionLevel()
    {
        if (sender.getEntity().hasPermissions(4))
            return 4;
        else if (sender.getEntity().hasPermissions(3))
            return 3;
        else if (sender.getEntity().hasPermissions(2))
            return 2;
        else if (sender.getEntity().hasPermissions(1))
            return 1;
        else
            return 0;
    }

}
