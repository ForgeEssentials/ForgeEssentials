package com.forgeessentials.commands.util;

import java.util.ArrayList;

import net.minecraftforge.common.Configuration;

import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.commands.CommandAFK;
import com.forgeessentials.commands.CommandBind;
import com.forgeessentials.commands.CommandBurn;
import com.forgeessentials.commands.CommandButcher;
import com.forgeessentials.commands.CommandCapabilities;
import com.forgeessentials.commands.CommandChunkLoaderList;
import com.forgeessentials.commands.CommandColorize;
import com.forgeessentials.commands.CommandCraft;
import com.forgeessentials.commands.CommandDoAs;
import com.forgeessentials.commands.CommandDrop;
import com.forgeessentials.commands.CommandEnchant;
import com.forgeessentials.commands.CommandEnderchest;
import com.forgeessentials.commands.CommandFindblock;
import com.forgeessentials.commands.CommandGameMode;
import com.forgeessentials.commands.CommandGetCommandBook;
import com.forgeessentials.commands.CommandGive;
import com.forgeessentials.commands.CommandHeal;
import com.forgeessentials.commands.CommandI;
import com.forgeessentials.commands.CommandInventorySee;
import com.forgeessentials.commands.CommandJump;
import com.forgeessentials.commands.CommandKill;
import com.forgeessentials.commands.CommandKit;
import com.forgeessentials.commands.CommandList;
import com.forgeessentials.commands.CommandLocate;
import com.forgeessentials.commands.CommandModlist;
import com.forgeessentials.commands.CommandMotd;
import com.forgeessentials.commands.CommandPing;
import com.forgeessentials.commands.CommandPotion;
import com.forgeessentials.commands.CommandPulse;
import com.forgeessentials.commands.CommandPush;
import com.forgeessentials.commands.CommandRemove;
import com.forgeessentials.commands.CommandRename;
import com.forgeessentials.commands.CommandRepair;
import com.forgeessentials.commands.CommandRules;
import com.forgeessentials.commands.CommandServerDo;
import com.forgeessentials.commands.CommandServerSettings;
import com.forgeessentials.commands.CommandSmite;
import com.forgeessentials.commands.CommandSpawnMob;
import com.forgeessentials.commands.CommandTime;
import com.forgeessentials.commands.CommandVanish;
import com.forgeessentials.commands.CommandVirtualchest;
import com.forgeessentials.commands.CommandWeather;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommandRegistrar
{
	public static ArrayList<FEcmdModuleCommands>	cmdList	= new ArrayList<FEcmdModuleCommands>();

	static
	{
		cmdList.add(new CommandMotd());
		cmdList.add(new CommandEnchant());
		cmdList.add(new CommandLocate());
		cmdList.add(new CommandRules());
		cmdList.add(new CommandModlist());
		cmdList.add(new CommandButcher());
		cmdList.add(new CommandRemove());
		cmdList.add(new CommandSpawnMob());
		cmdList.add(new CommandAFK());
		cmdList.add(new CommandKit());
		cmdList.add(new CommandEnderchest());
		cmdList.add(new CommandVirtualchest());
		cmdList.add(new CommandCapabilities());
		cmdList.add(new CommandJump());
		cmdList.add(new CommandCraft());
		cmdList.add(new CommandPing());
		cmdList.add(new CommandServerDo());
		cmdList.add(new CommandInventorySee());
		cmdList.add(new CommandSmite());
		cmdList.add(new CommandBurn());
		cmdList.add(new CommandPotion());
		cmdList.add(new CommandColorize());
		cmdList.add(new CommandRepair());
		cmdList.add(new CommandHeal());
		cmdList.add(new CommandKill());
		cmdList.add(new CommandGive());
		cmdList.add(new CommandI());
		cmdList.add(new CommandGameMode());
		cmdList.add(new CommandDoAs());
		cmdList.add(new CommandServerSettings());
		cmdList.add(new CommandGetCommandBook());
		cmdList.add(new CommandChunkLoaderList());
		cmdList.add(new CommandTime());
		cmdList.add(new CommandWeather());
		cmdList.add(new CommandBind());
		cmdList.add(new CommandRename());
		cmdList.add(new CommandList());
		cmdList.add(new CommandVanish());
		cmdList.add(new CommandPush());
		cmdList.add(new CommandDrop());
		cmdList.add(new CommandPulse());
		cmdList.add(new CommandFindblock());
	}

	public static void commandConfigs(Configuration config)
	{
		config.load();

		try
		{
			config.addCustomCategoryComment("commands", "All FE commands will have a config space here.");
			config.addCustomCategoryComment("CommandBlock", "Toggle server wide command block usage here.");
			config.addCustomCategoryComment("Player", "Toggle server wide player usage here.");
			config.addCustomCategoryComment("Console", "Toggle console usage here.");

			for (FEcmdModuleCommands fecmd : cmdList)
			{
				if (fecmd.usefullCmdBlock())
				{
					config.get("CommandBlock", fecmd.getCommandName(), fecmd.enableCmdBlock);
				}
				if (fecmd.usefullPlayer())
				{
					config.get("Player", fecmd.getCommandName(), fecmd.enablePlayer);
				}
				if (fecmd.canConsoleUseCommand())
				{
					config.get("Console", fecmd.getCommandName(), fecmd.enableConsole);
				}

				String category = "commands." + fecmd.getCommandName();
				config.addCustomCategoryComment(category, fecmd.getCommandPerm());
				for (String alias : config.get(category, "aliases", fecmd.getDefaultAliases()).getStringList())
				{
					fecmd.aliasList.add(alias);
				}
				fecmd.doConfig(config, category);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		config.save();
	}

	public static void load(FMLServerStartingEvent e)
	{
		for (FEcmdModuleCommands cmd : cmdList)
		{
			e.registerServerCommand(cmd);
		}
	}

	public static void registerPermissions(IPermRegisterEvent event)
	{
		for (FEcmdModuleCommands cmd : cmdList)
		{
			if (cmd.getCommandPerm() != null && cmd.getReggroup() != null)
			{
				event.registerPermissionLevel(cmd.getCommandPerm(), cmd.getReggroup());
				cmd.registerExtraPermissions(event);
			}
		}
	}
}
