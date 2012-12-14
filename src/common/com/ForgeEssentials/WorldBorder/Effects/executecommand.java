package com.ForgeEssentials.WorldBorder.Effects;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import net.minecraft.src.DedicatedServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

public class executecommand implements IEffect
{
	private String command = "/say %p Go back while you still can!";
	
	@Override
	public void registerConfig(Configuration config, String category)
	{
		command = config.get(category, "Command", command, "%p gets replaced with the players username").value;
	}

	@Override
	public void execute(EntityPlayerMP player) 
	{
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			DedicatedServer.getServer().executeCommand(command.replaceAll("%p", player.username));
		}
	}
}
