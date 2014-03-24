package com.forgeessentials.permission.mcoverride;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

// Hopefully we can transform the stuff in and we won't need this class.
// All commands as of Minecraft 1.5. I'm simply too lazy to do anymore.
// Won't be needed as and when Forge gets a Permission API.

public class OverrideManager
{

	public static void regOverrides(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandBanIp());
		e.registerServerCommand(new CommandBanlist());
		e.registerServerCommand(new CommandDebug());
		e.registerServerCommand(new CommandDefaultGameMode());
		e.registerServerCommand(new CommandDeop());
		e.registerServerCommand(new CommandDifficulty());
		e.registerServerCommand(new CommandGameRule());
		e.registerServerCommand(new CommandKick());
		e.registerServerCommand(new CommandMe());
		e.registerServerCommand(new CommandOp());
		e.registerServerCommand(new CommandPardonIp());
		e.registerServerCommand(new CommandPublish());
		e.registerServerCommand(new CommandSaveAll());
		e.registerServerCommand(new CommandSaveOff());
		e.registerServerCommand(new CommandSaveOn());
		e.registerServerCommand(new CommandSay());
		e.registerServerCommand(new CommandSeed());
		e.registerServerCommand(new CommandStop());
		e.registerServerCommand(new CommandToggleDownfall());
		e.registerServerCommand(new CommandXP());
		e.registerServerCommand(new CommandTestFor());
	}

}
