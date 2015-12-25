package com.forgeessentials.remote;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.FakePlayerFactory;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.network.ChatResponse;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

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
            this.sender = session.getUserIdent().getFakePlayer();
        else
            this.sender = FakePlayerFactory.get(ServerUtil.getOverworld(), ModuleRemote.FAKEPLAYER);
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
    public String getName()
    {
        return session.getUserIdent() != null ? session.getUserIdent().getUsernameOrUuid() : "anonymous";
    }

    @Override
    public void addChatMessage(IChatComponent chatComponent)
    {
        // TODO: Instead of directly sending the messages to the client, cache them and send them all after the running
        // command finished (only if enabled)
        ICommandSender receiver = MinecraftServer.getServer();
        if (session.getUserIdent() != null && session.getUserIdent().hasPlayer())
            receiver = session.getUserIdent().getPlayer();
        ChatOutputHandler.sendMessage(receiver, chatComponent);

        if (!session.isClosed())
        {
            try
            {
                // TODO: Add second message WITH formatting
                ChatResponse msg = new ChatResponse(null, ChatOutputHandler.stripFormatting(chatComponent.getUnformattedText()));
                session.sendMessage(new RemoteResponse<ChatResponse>(RemoteMessageID.CHAT, msg));
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
    public boolean canCommandSenderUseCommand(int level, String cmd)
    {
        return sender.canCommandSenderUseCommand(level, cmd);
    }

}
