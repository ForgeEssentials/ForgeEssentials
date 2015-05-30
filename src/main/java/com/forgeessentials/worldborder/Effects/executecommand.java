package com.forgeessentials.worldborder.Effects;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.worldborder.WorldBorder;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class executecommand implements IEffect
{
    private String command = "/say %p! Go back while you still can!";

    @Override
    public void registerConfig(Configuration config, String category)
    {
        command = config.get(category, "Command", command, "%p gets replaced with the players username").getString();
    }

    @Override
    public void execute(WorldBorder wb, EntityPlayerMP player)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), command.replaceAll("%p", player.getDisplayName()));
        }
    }
}
