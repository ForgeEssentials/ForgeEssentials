package com.forgeessentials.worldborder.Effects;

import com.forgeessentials.worldborder.WorldBorder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.Configuration;

public class executecommand implements IEffect {
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
            MinecraftServer.getServer().executeCommand(command.replaceAll("%p", player.username));
        }
    }
}
