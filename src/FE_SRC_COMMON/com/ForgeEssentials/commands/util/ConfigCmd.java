package com.ForgeEssentials.commands.util;

import java.io.File;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.commands.CommandAFK;
import com.ForgeEssentials.commands.CommandEnderchest;
import com.ForgeEssentials.commands.CommandMotd;
import com.ForgeEssentials.commands.CommandRules;
import com.ForgeEssentials.commands.CommandVirtualchest;
import com.ForgeEssentials.commands.ModuleCommands;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.ModuleConfigBase;

public class ConfigCmd extends ModuleConfigBase
{

	public static final File cmddir = new File(ForgeEssentials.FEDIR, "commands/");
	public Configuration config;
	
	public ConfigCmd(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		config = new Configuration(file, true);

		config.addCustomCategoryComment("general", "General Commands configuration.");
		ModuleCommands.removeDuplicateCommands = config.get("general", "removeDuplicateCommands", true,
				"Remove commands from the list if they already exist outside of FE.").getBoolean(true);
		CommandMotd.motd = config.get("general", "motd", "Welcome to a server running ForgeEssentials",
				"Specify the message that greets players when they log in to your server.").value;
		CommandRules.rulesFile = new File(cmddir, config.get("general", "RulesFile", "rules.txt",
				"Specify the file where the rules will read from and written to. This path is relative to this folder.").value);
		CommandEnderchest.useAlias = config.get("general", "useEnderChestAlias", true, "Use the alisa '/echest' for the command '/enderchest'.").getBoolean(
				true);
		CommandVirtualchest.useAlias = config.get("general", "useVirtualChestAlias", true, "Use the alias '/vchest' for the command '/virtualchest'.")
				.getBoolean(true);
		CommandVirtualchest.size = config.get("general", "VirtualChestRows", 6, "1 row = 9 slots. 3 = 1 chest, 6 = double chest (max size!).").getInt(6) * 9;
		CommandVirtualchest.name = config.get("general", "VirtualChestName", "Vault 13", "Don't use special stuff....").value;
		
		CommandAFK.warmup = config.get("general", "AFKwarmup", 5, "Amount of seconds you need to stand still in order to activate invulnerability.").getInt();
		config.addCustomCategoryComment("parts", "Enable/disable command groups here.");
		CommandRegistrar.general = config.get("parts", "enableGeneral", true,
				"Setting this to false will disable the following commands: /motd, /rules and /modlist.").getBoolean(true);
		CommandRegistrar.utility = config.get("parts", "enableUtility", true,
				"Setting this to false will disable the following commands: /butcher, /remove, /spawnmob, /tps, /afk, /kit, /enderchest, /virtualchest, /capabilities, /setspawn, /jump, /craft, /ping.").getBoolean(true);
		CommandRegistrar.op = config.get("parts", "enableOp", true,
				"Setting this to false will disable the following commands: /serverdo, /seeinventory.").getBoolean(true);
		CommandRegistrar.fun = config.get("parts", "enableFun", true,
				"Setting this to false will disable the following commands: /smite, /burn, /potion, /colourize.").getBoolean(true);
		CommandRegistrar.teleport = config.get("parts", "enableTP", true,
				"Setting this to false will disable the following commands: /back, /bed, /home, /spawn, /tp, /tphere, /tppos, /warp.").getBoolean(true);
		CommandRegistrar.cheat = config.get("parts", "enableFECheats", true,
				"Setting this to false will disable the following commands: /repair, /heal.").getBoolean(true);
		config.save();
	}

	@Override
	public void forceSave()
	{
		// TODO: may have problems..
		String path = CommandRules.rulesFile.getPath();
		path = path.replace(cmddir.getPath(), "");
		config.get("general", "RulesFile", "rules.txt", "Specify the file where the rules will read from and written to. This path is relative to this folder.").value = path;

		config.addCustomCategoryComment("general", "General Commands configuration.");
		config.get("general", "removeDuplicateCommands", true, "Remove commands from the list if they already exist outside of FE.").value = ""
				+ ModuleCommands.removeDuplicateCommands;
		config.get("general", "motd", "Welcome to a server running ForgeEssentials", "Specify the message that greets players when they log in to your server.").value = CommandMotd.motd;
		config.get("general", "useEnderChestAlias", true, "Use the alisa '/echest' for the command '/enderchest'.").value = "" + CommandEnderchest.useAlias;
		config.get("general", "useVirtualChestAlias", true, "Use the alias '/vchest' for the command '/virtualchest'.").value = ""
				+ CommandVirtualchest.useAlias;
		config.get("general", "VirtualChestRows", 6, "1 row = 9 slots. 3 = 1 chest, 6 = double chest (max size!).").value = "" + CommandVirtualchest.size / 9;
		config.get("general", "VirtualChestName", "Vault 13", "Don't use special stuff....").value = CommandVirtualchest.name;
		
		config.get("general", "AFKwarmup", 5, "Amount of seconds you need to stand still in order to activate invulnerability.").value = "" + CommandAFK.warmup;
		config.addCustomCategoryComment("parts", "Enable/disable command groups here.");
		config.get("parts", "enableGeneral", true,
				"Setting this to false will disable the following commands: /motd, /rules and /modlist.").value = "" + CommandRegistrar.general;
		config.get("parts", "enableUtility", true,
				"Setting this to false will disable the following commands: /butcher, /remove, /spawnmob, /tps, /afk, /kit, /enderchest, /virtualchest, /capabilities, /setspawn, /jump, /craft, /ping.").value = "" + CommandRegistrar.utility;
		config.get("parts", "enableOp", true,
				"Setting this to false will disable the following commands: /serverdo, /seeinventory.").value = "" + CommandRegistrar.op;
		config.get("parts", "enableFun", true,
				"Setting this to false will disable the following commands: /smite, /burn, /potion, /colourize.").value = "" + CommandRegistrar.fun;
		config.get("parts", "enableTP", true,
				"Setting this to false will disable the following commands: /back, /bed, /home, /spawn, /tp, /tphere, /tppos, /warp.").value = "" + CommandRegistrar.teleport;
		config.get("parts", "enableFECheats", true,
				"Setting this to false will disable the following commands: /repair, /heal.").value = "" + CommandRegistrar.cheat;
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();
		ModuleCommands.removeDuplicateCommands = config.get("general", "removeDuplicateCommands", true).getBoolean(true);
		CommandMotd.motd = config.get("general", "motd", "Welcome to a server running ForgeEssentials").value;
		CommandRules.rulesFile = new File(cmddir, config.get("general", "RulesFile", "rules.txt").value);
		CommandEnderchest.useAlias = config.get("general", "useEnderChestAlias", true).getBoolean(true);
		CommandVirtualchest.useAlias = config.get("general", "useVirtualChestAlias", true).getBoolean(true);
		CommandVirtualchest.size = config.get("general", "VirtualChestRows", 6).getInt(6) * 9;
		CommandVirtualchest.name = config.get("general", "VirtualChestName", "Vault 13").value;
		CommandAFK.warmup = config.get("general", "AFKwarmup", 5).getInt();
		CommandRegistrar.general = config.get("parts", "enableGeneral", true).getBoolean(true);
		CommandRegistrar.utility = config.get("parts", "enableUtility", true).getBoolean(true);
		CommandRegistrar.op = config.get("parts", "enableOp", true).getBoolean(true);
		CommandRegistrar.fun = config.get("parts", "enableFun", true).getBoolean(true);
		CommandRegistrar.teleport = config.get("parts", "enableTP", true).getBoolean(true);
		CommandRegistrar.cheat = config.get("parts", "enableFECheats", true).getBoolean(true);
	}
}
