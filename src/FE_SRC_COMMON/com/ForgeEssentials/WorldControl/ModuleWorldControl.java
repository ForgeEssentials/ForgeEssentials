package com.ForgeEssentials.WorldControl;

//Depreciated
import java.util.ArrayList;

import com.ForgeEssentials.WorldControl.TickTasks.TickTaskTopManipulator.Mode;
import com.ForgeEssentials.WorldControl.commands.CommandRedo;
import com.ForgeEssentials.WorldControl.commands.CommandReplace;
import com.ForgeEssentials.WorldControl.commands.CommandSet;
import com.ForgeEssentials.WorldControl.commands.CommandTopManipulate;
import com.ForgeEssentials.WorldControl.commands.CommandUndo;
import com.ForgeEssentials.WorldControl.commands.WorldControlCommandBase;
import com.ForgeEssentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Init;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PreInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerInit;
import com.ForgeEssentials.util.events.modules.FEModuleInitEvent;
import com.ForgeEssentials.util.events.modules.FEModulePreInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerInitEvent;

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

	}

	// serverStart.
	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandSet());
		e.registerServerCommand(new CommandRedo());
		e.registerServerCommand(new CommandUndo());
		e.registerServerCommand(new CommandReplace());
		e.registerServerCommand(new CommandTopManipulate("thaw", Mode.THAW));
		e.registerServerCommand(new CommandTopManipulate("freeze", Mode.FREEZE));
		e.registerServerCommand(new CommandTopManipulate("snow", Mode.SNOW));
		e.registerServerCommand(new CommandTopManipulate("till", Mode.TILL));
		e.registerServerCommand(new CommandTopManipulate("untill", Mode.UNTILL));
		// WEIntegration.serverStarting(e);
	}

	@PermRegister
	public static void registerPerms(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.WorldControl.commands.set", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.WorldControl.commands.undo", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.WorldControl.commands.redo", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.WorldControl.commands.replace", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.WorldControl.commands.thaw", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.WorldControl.commands.freeze", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.WorldControl.commands.snow", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.WorldControl.commands.till", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.WorldControl.commands.untill", RegGroup.OWNERS);
	}

}
