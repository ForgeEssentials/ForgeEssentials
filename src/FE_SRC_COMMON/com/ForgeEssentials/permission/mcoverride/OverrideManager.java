package com.ForgeEssentials.permission.mcoverride;

import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

// Hopefully we can transform the stuff in and we won't need this class.

public class OverrideManager
{

	public static void regOverrides(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandBan());
		e.registerServerCommand(new CommandBanIp());
		e.registerServerCommand(new CommandBanlist());
		e.registerServerCommand(new CommandDebug());
		e.registerServerCommand(new CommandDefaultGameMode());
		e.registerServerCommand(new CommandDeop());
		e.registerServerCommand(new CommandDifficulty());
		e.registerServerCommand(new CommandGameRule());
		e.registerServerCommand(new CommandHelp());
		e.registerServerCommand(new CommandKick());
		e.registerServerCommand(new CommandMe());
		e.registerServerCommand(new CommandOp());
		e.registerServerCommand(new CommandPardon());
		e.registerServerCommand(new CommandPardonIp());
		e.registerServerCommand(new CommandPublish());
		e.registerServerCommand(new CommandSaveAll());
		e.registerServerCommand(new CommandSaveOff());
		e.registerServerCommand(new CommandSaveOn());
		e.registerServerCommand(new CommandSay());
		e.registerServerCommand(new CommandSeed());
		e.registerServerCommand(new CommandSetSpawn());
		e.registerServerCommand(new CommandStop());
		e.registerServerCommand(new CommandToggleDownfall());
		e.registerServerCommand(new CommandWhitelist());
		e.registerServerCommand(new CommandXP());
		e.registerServerCommand(new CommandTestFor());
	}
	public static void registerOverridePerms(IPermRegisterEvent e){
		e.registerPermissionLevel("Minecraft.commands.ban", RegGroup.ZONE_ADMINS);
		e.registerPermissionLevel("Minecraft.commands.ban-ip", RegGroup.OWNERS);
		e.registerPermissionLevel("Minecraft.commands.debug", RegGroup.ZONE_ADMINS);
		e.registerPermissionLevel("Minecraft.commands.defaultgamemode", RegGroup.OWNERS);
		e.registerPermissionLevel("Minecraft.commands.deop", RegGroup.OWNERS);
		e.registerPermissionLevel("Minecraft.commands.difficulty", RegGroup.OWNERS);
		e.registerPermissionLevel("Minecraft.commands.gamerule", RegGroup.OWNERS);
		e.registerPermissionLevel("Minecraft.commands.kick", RegGroup.ZONE_ADMINS);
		e.registerPermissionLevel("Minecraft.commands.me", RegGroup.GUESTS);
		e.registerPermissionLevel("Minecraft.commands.op", RegGroup.OWNERS);
		e.registerPermissionLevel("Minecraft.commands.pardon", RegGroup.ZONE_ADMINS);
		e.registerPermissionLevel("Minecraft.commands.pardon-ip", RegGroup.ZONE_ADMINS);
		e.registerPermissionLevel("Minecraft.commands.publish", RegGroup.OWNERS);
		e.registerPermissionLevel("Minecraft.commands.save-all", RegGroup.ZONE_ADMINS);
		e.registerPermissionLevel("Minecraft.commands.save-on", RegGroup.ZONE_ADMINS);
		e.registerPermissionLevel("Minecraft.commands.save-off", RegGroup.ZONE_ADMINS);
		e.registerPermissionLevel("Minecraft.commands.say", RegGroup.OWNERS);
		e.registerPermissionLevel("Minecraft.commands.seed", RegGroup.MEMBERS);
		e.registerPermissionLevel("Minecraft.commands.stop", RegGroup.GUESTS);
		e.registerPermissionLevel("Minecraft.commands.whitelist", RegGroup.OWNERS);
		e.registerPermissionLevel("Minecraft.commands.xp", RegGroup.ZONE_ADMINS);
		e.registerPermissionLevel("Minecraft.commands.toggledownfall", RegGroup.ZONE_ADMINS);
		e.registerPermissionLevel("Minecraft.commands.testfor", RegGroup.MEMBERS);
		e.registerPermissionLevel("Minecraft.commands.help", RegGroup.GUESTS);
		
	}

}
