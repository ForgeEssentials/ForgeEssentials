package com.ForgeEssentials.commands;

import com.ForgeEssentials.core.Module;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Home of the Commands plugin
 * @author luacs1998
 */

public class Commands extends Module{
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandMotd());
		e.registerServerCommand(new CommandButcher());
	}

}