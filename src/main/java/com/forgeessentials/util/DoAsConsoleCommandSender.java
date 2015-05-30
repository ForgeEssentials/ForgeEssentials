package com.forgeessentials.util;

import net.minecraft.command.ICommandSender;
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
        sender.addChatMessage(message);
    }

    @Override
    public boolean canCommandSenderUseCommand(int level, String command)
    {
        return true;
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates()
    {
        return sender.getPlayerCoordinates();
    }

    @Override
    public World getEntityWorld()
    {
        return sender.getEntityWorld();
    }

}