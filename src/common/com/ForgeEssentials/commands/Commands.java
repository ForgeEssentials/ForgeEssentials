package com.ForgeEssentials.commands;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Home of the Commands plugin
 * @author luacs1998
 */

public class Commands {
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandMotd());
		e.registerServerCommand(new CommandButcher());
	}

}
