package com.forgeessentials.util;

import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;

public class DoAsCommandSender implements ICommandSender
{

    protected ICommandSender sender;

    protected UserIdent ident;

    protected boolean hideChatMessages;

    public DoAsCommandSender()
    {
        this.ident = APIRegistry.IDENT_SERVER;
        this.sender = MinecraftServer.getServer();
    }

    public DoAsCommandSender(UserIdent ident)
    {
        this.ident = ident;
        this.sender = MinecraftServer.getServer();
    }

    public DoAsCommandSender(UserIdent ident, ICommandSender sender)
    {
        this.ident = ident;
        this.sender = sender;
    }

    public ICommandSender getOriginalSender()
    {
        return sender;
    }

    public UserIdent getUserIdent()
    {
        return ident;
    }

    @Override
    public String getName()
    {
        return sender.getName();
    }

    @Override
    public IChatComponent getDisplayName()
    {
        return sender.getDisplayName();
    }

    @Override
    public void addChatMessage(IChatComponent message)
    {
        if (!hideChatMessages)
            sender.addChatMessage(message);
    }

    @Override
    public boolean canCommandSenderUseCommand(int level, String command)
    {
        return true;
    }

    @Override
    public World getEntityWorld()
    {
        return sender.getEntityWorld();
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
	public BlockPos getPosition() {
		return sender.getPosition();
	}

	@Override
	public Vec3 getPositionVector() {
		return sender.getPositionVector();
	}

	@Override
	public Entity getCommandSenderEntity() {
		return sender.getCommandSenderEntity();
	}

	@Override
	public boolean sendCommandFeedback() {
		return sender.sendCommandFeedback();
	}

	@Override
	public void setCommandStat(Type type, int amount) {
		sender.setCommandStat(type, amount);
	}

}