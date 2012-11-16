package com.ForgeEssentials.WorldControl;

//Depreciated
import java.util.ArrayList;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.WorldControl.commands.*;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskHandler;
import com.ForgeEssentials.core.OutputHandler;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.TickRegistry;

// central class for all the WorldControl stuff
public class WorldControl
{
	// implicit constructor WorldControl()
	public static int defaultWandID;
	public static boolean useExtraSlash;
	public static ArrayList<WorldControlCommandBase> needsCompleteCommands = new ArrayList<WorldControlCommandBase>();

	// load.
	public void preLoad(FMLPreInitializationEvent event)
	{
		OutputHandler.SOP("WorldControl module is enabled. Loading...");
	}

	// load.
	public void load(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new WandController());
		TickRegistry.registerTickHandler(new TickTaskHandler(), Side.SERVER);
	}

	// load.
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandWand());
		e.registerServerCommand(new CommandPos(1));
		e.registerServerCommand(new CommandPos(2));
		e.registerServerCommand(new CommandSet());
		e.registerServerCommand(new CommandRedo());
		e.registerServerCommand(new CommandUndo());
	}
}
