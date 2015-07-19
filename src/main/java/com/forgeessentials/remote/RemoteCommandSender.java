package com.forgeessentials.remote;

import java.io.IOException;

import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

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

    public EntityPlayer getPlayer()
    {
        return session.getUserIdent().getFakePlayer();
    }

    @Override
    public String getName()
    {
        return session.getUserIdent().getUsernameOrUuid();
    }

    @Override
    public IChatComponent getDisplayName()
    {
        return getPlayer().getDisplayName();
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
    public BlockPos getPosition()
    {
        return getPlayer().getPosition();
    }

    @Override
    public Vec3 getPositionVector()
    {
        return getPlayer().getPositionVector();
    }

    @Override
    public World getEntityWorld()
    {
        return getPlayer().getEntityWorld();
    }

    @Override
    public Entity getCommandSenderEntity()
    {
        return getPlayer().getCommandSenderEntity();
    }

    @Override
    public boolean sendCommandFeedback()
    {
        return getPlayer().sendCommandFeedback();
    }

    @Override
    public void func_174794_a(Type p_174794_1_, int p_174794_2_)
    {
        getPlayer().func_174794_a(p_174794_1_, p_174794_2_);
    }

}
