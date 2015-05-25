package com.forgeessentials.chat.irc;

import org.pircbotx.User;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

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
    public String getCommandSenderName()
    {
        return "IRC:" + user.getNick();
    }

    @Override
    public IChatComponent func_145748_c_()
    {
        return new ChatComponentText(this.getCommandSenderName());
    }

    @Override
    public void addChatMessage(IChatComponent chatComponent)
    {
        if (user.getBot().isConnected())
            user.sendMessage(chatComponent.getUnformattedText());
    }

    @Override
    public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_)
    {
        return true;
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates()
    {
        return MinecraftServer.getServer().getPlayerCoordinates();
    }

    @Override
    public World getEntityWorld()
    {
        return MinecraftServer.getServer().getEntityWorld();
    }

}
