package com.ForgeEssentials.WorldControl;

//Depreciated
import java.util.ArrayList;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskTopManipulator.Mode;
import com.ForgeEssentials.WorldControl.commands.CommandChunk;
import com.ForgeEssentials.WorldControl.commands.CommandContract;
import com.ForgeEssentials.WorldControl.commands.CommandDeselect;
import com.ForgeEssentials.WorldControl.commands.CommandExpand;
import com.ForgeEssentials.WorldControl.commands.CommandPos;
import com.ForgeEssentials.WorldControl.commands.CommandRedo;
import com.ForgeEssentials.WorldControl.commands.CommandReplace;
import com.ForgeEssentials.WorldControl.commands.CommandSet;
import com.ForgeEssentials.WorldControl.commands.CommandShift;
import com.ForgeEssentials.WorldControl.commands.CommandTopManipulate;
import com.ForgeEssentials.WorldControl.commands.CommandUndo;
import com.ForgeEssentials.WorldControl.commands.CommandWand;
import com.ForgeEssentials.WorldControl.commands.WorldControlCommandBase;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.PreInit;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermRegister;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.core.ForgeEssentials;
//import com.ForgeEssentials.WorldControl.weintegration.WEIntegration;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

// central class for all the WorldControl stuff
@FEModule(name = "WorldControl", parentMod = ForgeEssentials.class, configClass = ConfigWorldControl.class)
public class ModuleWorldControl
{
	// implicit constructor WorldControl()
	public static int									defaultWandID;
	public static ArrayList<WorldControlCommandBase>	needsCompleteCommands	= new ArrayList<WorldControlCommandBase>();

	// preload.
	@PreInit
	public void preLoad(FEModulePreInitEvent event)
	{
	}

	// load.
	@Init
	public void load(FEModuleInitEvent event)
	{
		MinecraftForge.EVENT_BUS.register(WandController.instance);
		TickRegistry.registerTickHandler(WandController.instance, Side.SERVER);
	}
	
	@PermRegister(ident = "ModuleWorldControl")
	public static void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.WorldControl._ALL_", RegGroup.OWNERS);
		for(String str : permsToRegister) {
			event.registerPermissionLevel("ForgeEssentials.WorldControl._ALL_", RegGroup.OWNERS);
		}
		event.registerPermissionLevel("ForgeEssentials.WorldControl.longreach", RegGroup.OWNERS);
	}
	
	private static ArrayList<String> permsToRegister = new ArrayList<String>();
	
	public void registerCommand(FEModuleServerInitEvent e, WorldControlCommandBase command) {
		e.registerServerCommand(command);
		if(!permsToRegister.contains(command.getCommandPerm()))permsToRegister.add(command.getCommandPerm());
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
		e.registerServerCommand(new CommandTopManipulate("green", Mode.GREEN));
		e.registerServerCommand(new CommandChunk());
		e.registerServerCommand(new CommandExpand());
		e.registerServerCommand(new CommandContract());
		e.registerServerCommand(new CommandShift());
		// WEIntegration.serverStarting(e);
	}
}
