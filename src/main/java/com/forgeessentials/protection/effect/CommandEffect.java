package com.forgeessentials.protection.effect;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandEffect extends ZoneEffect {

    protected String command;
    
    public CommandEffect(EntityPlayerMP player, int interval, String command)
    {
        super(player, interval, false);
        command = command.replaceAll("%p", player.getCommandSenderName());
        command = command.replaceAll("%u", player.getPersistentID().toString());
        this.command = command;
    }

    @Override
    public void execute()
    {
        MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), command);
    }

}
