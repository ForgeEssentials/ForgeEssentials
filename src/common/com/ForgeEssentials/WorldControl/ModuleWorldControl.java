package com.ForgeEssentials.WorldControl;

//Depreciated
import java.util.ArrayList;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.WorldControl.commands.*;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskHandler;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskTopManipulator;
import com.ForgeEssentials.WorldControl.tickTasks.TickTaskTopManipulator.Mode;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.config.Configuration;
import com.ForgeEssentials.core.config.FEConfig;
import com.ForgeEssentials.core.config.Property;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.TickRegistry;

// central class for all the WorldControl stuff
public class ModuleWorldControl implements IFEModule
{
	// implicit constructor WorldControl()
	public static int defaultWandID;
	public static ArrayList<WorldControlCommandBase> needsCompleteCommands = new ArrayList<WorldControlCommandBase>();
	
	// Some static fields for WorldControl config.
	public static int WCblocksPerTick;

	// preload.
	public void preLoad(FMLPreInitializationEvent event)
	{
		OutputHandler.SOP("WorldControl module is enabled. Loading...");
	}

	// load.
	public void load(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new WandController());
		TickRegistry.registerTickHandler(new TickTaskHandler(), Side.SERVER);
		
		OutputHandler.SOP("WorldControl loading configuration...");
		
		Configuration conf = ForgeEssentials.config.config;
		
		conf.addCustomCategoryComment("WorldControl", "Properties used by WorldControl");
		
		Property prop = conf.get("WorldControl", "BlocksPerTick", 20);
		prop.comment = "Specifies the maximum blocks/tick that can be changed via the WorldControl functions. Powerful computers may set higher, servers may want to keep it lower.";
		ModuleWorldControl.WCblocksPerTick = prop.getInt();
		OutputHandler.SOP("Setting blocks/tick to: " + WCblocksPerTick);
		
		ForgeEssentials.config.config.save();
	}
	
	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	// serverStart.
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandWand());
		e.registerServerCommand(new CommandPos(1));
		e.registerServerCommand(new CommandPos(2));
		e.registerServerCommand(new CommandSet());
		e.registerServerCommand(new CommandRedo());
		e.registerServerCommand(new CommandUndo());
		e.registerServerCommand(new CommandReplace());
		e.registerServerCommand(new CommandTopManipulate("thaw", Mode.THAW));
		e.registerServerCommand(new CommandTopManipulate("freeze", Mode.FREEZE));
		e.registerServerCommand(new CommandTopManipulate("snow", Mode.SNOW));
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
		// TODO Auto-generated method stub
		
	}
}
