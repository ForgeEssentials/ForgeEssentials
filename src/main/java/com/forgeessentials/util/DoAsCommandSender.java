package com.forgeessentials.util;

import net.minecraft.command.ICommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;

public class DoAsCommandSender implements ICommandSource
{

    protected ICommandSource sender;

    protected UserIdent ident;

    protected boolean hideChatMessages;

    public DoAsCommandSender()
    {
        this.ident = APIRegistry.IDENT_SERVER;
        this.sender = getServer();
    }

    public DoAsCommandSender(UserIdent ident)
    {
        this.ident = ident;
        this.sender = getServer();
    }

    public DoAsCommandSender(UserIdent ident, ICommandSource sender)
    {
        this.ident = ident;
        this.sender = sender;
    }

    public ICommandSource getOriginalSender()
    {
        return sender;
    }

    public UserIdent getUserIdent()
    {
        return ident;
    }
    /*
    @Override
    public String getName()
    {
        return sender.;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return sender.getDisplayName();
    }
*/
	@Override
	public void sendMessage(ITextComponent message, UUID p_145747_2_) {
		if (!hideChatMessages)
            sender.sendMessage(message, p_145747_2_);
		
	}
/*
    @Override
    public boolean canUseCommand(int level, String command)
    {
        return true;
    }

    @Override
    public World getEntityWorld()
    {
        return sender.getEntityWorld();
    }

    @Override
    public BlockPos getPosition()
    {
        return sender.getPosition();
    }

    @Override
    public Vector3d getPositionVector()
    {
        return sender.getPositionVector();
    }

    @Override
    public Entity getCommandSenderEntity()
    {
        return sender.getCommandSenderEntity();
    }

    @Override
    public boolean sendCommandFeedback()
    {
        return sender.sendCommandFeedback();
    }

    @Override
    public void setCommandStat(Type p_174794_1_, int p_174794_2_)
    {
        sender.setCommandStat(p_174794_1_, p_174794_2_);
    }
*/
    public MinecraftServer getServer()
    {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public UserIdent getIdent()
    {
        return ident;
    }

    public void setIdent(UserIdent ident)
    {
        this.ident = ident;
    }

    public void setHideChatMessages(boolean hideChatMessages)
    {
        this.hideChatMessages = hideChatMessages;
    }

    public boolean isHideChatMessages()
    {
        return hideChatMessages;
    }

	@Override
	public boolean acceptsSuccess() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean acceptsFailure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldInformAdmins() {
		// TODO Auto-generated method stub
		return false;
	}

}