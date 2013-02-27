package com.ForgeEssentials.core.commands;

import com.ForgeEssentials.core.commands.selections.CommandDeselect;
import com.ForgeEssentials.core.commands.selections.CommandPos;
import com.ForgeEssentials.core.commands.selections.CommandWand;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CoreCommands
{
	public void load(FMLServerStartingEvent e){


		e.registerServerCommand(new CommandFEVersion());
		e.registerServerCommand(new CommandFECredits());
		e.registerServerCommand(new CommandFEReload());
		e.registerServerCommand(new CommandFEDebug());
		e.registerServerCommand(new CommandPos(1));
		e.registerServerCommand(new CommandPos(2));
		e.registerServerCommand(new CommandWand());
		e.registerServerCommand(new CommandDeselect());
	}

}
