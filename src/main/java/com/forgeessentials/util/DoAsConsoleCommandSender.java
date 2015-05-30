package com.forgeessentials.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class DoAsConsoleCommandSender implements ICommandSender
{

    public final ICommandSender sender;

    public DoAsConsoleCommandSender(ICommandSender sender)
    {
        this.sender = sender;
    }

    @Override
    public String getCommandSenderName()
    {
        return sender.getCommandSenderName();
    }

    @Override
    public IChatComponent func_145748_c_()
    {
        return sender.func_145748_c_();
    }

    @Override
    public void addChatMessage(IChatComponent message)
    {
        OutputHandler.sendMessage(sender, message);
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