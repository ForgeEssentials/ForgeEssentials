package com.ForgeEssentials.WorldControl;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.WorldControl.commands.CommandPos;
import com.ForgeEssentials.WorldControl.commands.CommandWand;
import com.ForgeEssentials.core.Module;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

// central class for all the WorldControl stuff
public class WorldControl extends Module
{
	// implicit constructor WorldControl()
	
	// load.
	public void load(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new WandController());
	}
	
	// load.
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandWand());
		e.registerServerCommand(new CommandPos(1));
		e.registerServerCommand(new CommandPos(2));
	}
}
