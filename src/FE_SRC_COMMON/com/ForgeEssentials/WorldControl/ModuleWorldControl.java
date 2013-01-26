package com.ForgeEssentials.WorldControl;

//Depreciated
import java.io.File;
import java.util.ArrayList;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskTopManipulator.Mode;
import com.ForgeEssentials.WorldControl.commands.CommandDeselect;
import com.ForgeEssentials.WorldControl.commands.CommandPos;
import com.ForgeEssentials.WorldControl.commands.CommandRedo;
import com.ForgeEssentials.WorldControl.commands.CommandReplace;
import com.ForgeEssentials.WorldControl.commands.CommandSet;
import com.ForgeEssentials.WorldControl.commands.CommandTopManipulate;
import com.ForgeEssentials.WorldControl.commands.CommandUndo;
import com.ForgeEssentials.WorldControl.commands.CommandWand;
import com.ForgeEssentials.WorldControl.commands.WorldControlCommandBase;
import com.ForgeEssentials.WorldControl.weintegration.WEIntegration;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Init;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PreInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerInit;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePreInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerInitEvent;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TickTaskHandler;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

// central class for all the WorldControl stuff
@FEModule(name = "WorldControl", parentMod = ForgeEssentials.class, isCore = true)
public class ModuleWorldControl
{
	// implicit constructor WorldControl()
	public static int defaultWandID;
	public static ArrayList<WorldControlCommandBase> needsCompleteCommands = new ArrayList<WorldControlCommandBase>();

	// Some static fields for WorldControl config.
	public static int WCblocksPerTick;

	public static final File wcconf = new File(ForgeEssentials.FEDIR, "WorldControl.cfg");

	// preload.
	@PreInit
	public void preLoad(FEModulePreInitEvent event)
	{
		OutputHandler.SOP("WorldControl module is enabled. Loading...");
		doConfig();
	}

	public static void doConfig()
	{
		Configuration conf = new Configuration(wcconf, true);

		conf.load();
		conf.addCustomCategoryComment("WorldControl", "Properties used by WorldControl");

		Property prop = conf.get("WorldControl", "BlocksPerTick", 20);
		prop.comment = "Specifies the maximum blocks/tick that can be changed via the WorldControl functions. Powerful computers may set it higher, servers may want to keep it lower.";
		WCblocksPerTick = prop.getInt();
		OutputHandler.SOP("Setting blocks/tick to: " + WCblocksPerTick);

		conf.save();
	}

	// load.
	@Init
	public void load(FEModuleInitEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new WandController());
		TickRegistry.registerTickHandler(new TickTaskHandler(), Side.SERVER);

	}

	// serverStart.
	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandWand());
		e.registerServerCommand(new CommandDeselect());
		e.registerServerCommand(new CommandPos(1));
		e.registerServerCommand(new CommandPos(2));
		e.registerServerCommand(new CommandSet());
		e.registerServerCommand(new CommandRedo());
		e.registerServerCommand(new CommandUndo());
		e.registerServerCommand(new CommandReplace());
		e.registerServerCommand(new CommandTopManipulate("thaw", Mode.THAW));
		e.registerServerCommand(new CommandTopManipulate("freeze", Mode.FREEZE));
		e.registerServerCommand(new CommandTopManipulate("snow", Mode.SNOW));
		e.registerServerCommand(new CommandTopManipulate("till", Mode.TILL));
		e.registerServerCommand(new CommandTopManipulate("untill", Mode.UNTILL));
		WEIntegration.serverStarting(e);
	}
}
