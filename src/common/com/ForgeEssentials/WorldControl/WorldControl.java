package com.ForgeEssentials.WorldControl;

import java.util.ArrayList;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.WorldControl.commands.CommandPos;
import com.ForgeEssentials.WorldControl.commands.CommandSet;
import com.ForgeEssentials.WorldControl.commands.CommandWand;
import com.ForgeEssentials.WorldControl.commands.WorldControlCommandBase;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

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

	}

	// load.
	public void load(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new WandController());
	}

	// load.
	public void serverStarting(FMLServerStartingEvent e)
	{
		registerCommand(e, new CommandWand(), false);
		registerCommand(e, new CommandPos(1), false);
		registerCommand(e, new CommandPos(2), false);
		registerCommand(e, new CommandSet(), true);
	}

	private void registerCommand(FMLServerStartingEvent e, WorldControlCommandBase command, boolean needsComplete)
	{
		if (needsComplete)
			needsCompleteCommands.add(command);
		e.registerServerCommand(command);

	}
}
