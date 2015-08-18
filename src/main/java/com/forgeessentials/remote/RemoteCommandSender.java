package com.forgeessentials.remote;

import java.io.IOException;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.remote.RemoteResponse;
import com.forgeessentials.api.remote.RemoteSession;
import com.forgeessentials.remote.network.ChatResponse;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

public class RemoteCommandSender implements ICommandSender
{

    private RemoteSession session;

    public RemoteCommandSender(RemoteSession session)
    {
        this.session = session;
    }

    public RemoteSession getSession()
    {
        return session;
    }

    public UserIdent getUserIdent()
    {
        return session.getUserIdent();
    }

    public EntityPlayer getPlayer()
    {
        return session.getUserIdent().getFakePlayer();
    }

    @Override
    public String getCommandSenderName()
    {
        return session.getUserIdent().getUsernameOrUuid();
    }

    @Override
    public IChatComponent func_145748_c_()
    {
        return getPlayer().func_145748_c_();
    }

    @Override
    public void addChatMessage(IChatComponent chatComponent)
    {
        if (session.getUserIdent().hasPlayer())
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
        return getPlayer().canCommandSenderUseCommand(level, cmd);
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates()
    {
        return getPlayer().getPlayerCoordinates();
    }

    @Override
    public World getEntityWorld()
    {
        return getPlayer().getEntityWorld();
    }

}
