package com.forgeessentials.chat.irc;

import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.pircbotx.User;

import com.forgeessentials.util.output.ChatOutputHandler;

public class IrcCommandSender implements ICommandSender
{

    private User user;

    public IrcCommandSender(User user)
    {
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

    @Override
    public String getName()
    {
        return "IRC:" + user.getNick();
    }

    @Override
    public IChatComponent getDisplayName()
    {
        return new ChatComponentText(this.getName());
    }

    @Override
    public void addChatMessage(IChatComponent chatComponent)
    {
        if (user.getBot().isConnected())
            user.send().message(ChatOutputHandler.stripFormatting(chatComponent.getUnformattedText()));
    }

    @Override
    public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_)
    {
        return true;
    }

	@Override
	public BlockPos getPosition() {
		return getServer().getPosition();
	}

    @Override
    public Vec3 getPositionVector()
    {
        return getServer().getPositionVector();
    }

    @Override
    public World getEntityWorld()
    {
        return getServer().getEntityWorld();
    }

    @Override
    public Entity getCommandSenderEntity()
    {
        return getServer().getCommandSenderEntity();
    }

    @Override
    public boolean sendCommandFeedback()
    {
        return getServer().sendCommandFeedback();
    }

    @Override
    public void setCommandStat(Type p_174794_1_, int p_174794_2_)
    {
        getServer().setCommandStat(p_174794_1_, p_174794_2_);
    }

    public MinecraftServer getServer()
    {
        return MinecraftServer.getServer();
    }
}
