package com.forgeessentials.remote;

import java.io.IOException;

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

    private RemoteSession session;

    public RemoteCommandSender(RemoteSession session)
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
    public String getCommandSenderName()
    {
        return session.getUserIdent() != null ? session.getUserIdent().getUsernameOrUuid() : "anonymous";
    }

    @Override
    public void addChatMessage(IChatComponent chatComponent)
    {
        if (session.getUserIdent() != null && session.getUserIdent().hasPlayer())
        {
            ChatOutputHandler.sendMessage(session.getUserIdent().getPlayer(), chatComponent);
        }
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
    }

    @Override
    public boolean canCommandSenderUseCommand(int level, String cmd)
    {
        return sender.canCommandSenderUseCommand(level, cmd);
    }

}
