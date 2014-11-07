package com.forgeessentials.commands.util;

import java.util.ArrayList;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.CommandAFK;
import com.forgeessentials.commands.CommandBind;
import com.forgeessentials.commands.CommandBurn;
import com.forgeessentials.commands.CommandButcher;
import com.forgeessentials.commands.CommandCapabilities;
import com.forgeessentials.commands.CommandChunkLoaderList;
import com.forgeessentials.commands.CommandColorize;
import com.forgeessentials.commands.CommandCraft;
import com.forgeessentials.commands.CommandDrop;
import com.forgeessentials.commands.CommandEnchant;
import com.forgeessentials.commands.CommandEnderchest;
import com.forgeessentials.commands.CommandFindblock;
import com.forgeessentials.commands.CommandGameMode;
import com.forgeessentials.commands.CommandGetCommandBook;
import com.forgeessentials.commands.CommandHeal;
import com.forgeessentials.commands.CommandInventorySee;
import com.forgeessentials.commands.CommandJump;
import com.forgeessentials.commands.CommandKill;
import com.forgeessentials.commands.CommandKit;
import com.forgeessentials.commands.CommandLocate;
import com.forgeessentials.commands.CommandModlist;
import com.forgeessentials.commands.CommandMotd;
import com.forgeessentials.commands.CommandNoClip;
import com.forgeessentials.commands.CommandPing;
import com.forgeessentials.commands.CommandPotion;
import com.forgeessentials.commands.CommandPulse;
import com.forgeessentials.commands.CommandPush;
import com.forgeessentials.commands.CommandRemove;
import com.forgeessentials.commands.CommandRename;
import com.forgeessentials.commands.CommandRepair;
import com.forgeessentials.commands.CommandRules;
import com.forgeessentials.commands.CommandSmite;
import com.forgeessentials.commands.CommandSpawnMob;
import com.forgeessentials.commands.CommandTime;
import com.forgeessentials.commands.CommandVanish;
import com.forgeessentials.commands.CommandVirtualchest;
import com.forgeessentials.commands.CommandWeather;
import com.forgeessentials.commands.admin.CommandDoAs;
import com.forgeessentials.commands.admin.CommandMemusage;
import com.forgeessentials.commands.admin.CommandServerDo;
import com.forgeessentials.commands.admin.CommandServerSettings;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommandRegistrar {
	
    public static ArrayList<FEcmdModuleCommands> cmdList;

    static
    {
    	cmdList = new ArrayList<FEcmdModuleCommands>();
        cmdList.add(new CommandTime());
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
        cmdList.add(new CommandGameMode());
        cmdList.add(new CommandDoAs());
        cmdList.add(new CommandServerSettings());
        cmdList.add(new CommandGetCommandBook());
        cmdList.add(new CommandChunkLoaderList());
        cmdList.add(new CommandWeather());
        cmdList.add(new CommandBind());
        cmdList.add(new CommandRename());
        cmdList.add(new CommandVanish());
        cmdList.add(new CommandPush());
        cmdList.add(new CommandDrop());
        cmdList.add(new CommandPulse());
        cmdList.add(new CommandFindblock());
        cmdList.add(new CommandMemusage());
        cmdList.add(new CommandNoClip());
    }

    public static void commandConfigs(Configuration config)
    {
        config.load();
        
		// Add categories
		config.addCustomCategoryComment("commands", "All FE commands will have a config space here.");
		config.addCustomCategoryComment("CommandBlock", "Toggle server wide command block usage here.");
		config.addCustomCategoryComment("Player", "Toggle server wide player usage here.");
		config.addCustomCategoryComment("Console", "Toggle console usage here.");

		for (FEcmdModuleCommands fecmd : cmdList) {
			if (fecmd.usableByCmdBlock())
				fecmd.setEnabledForCmdBlock(config.get("CommandBlock", fecmd.getCommandName(), fecmd.isEnabledForCmdBlock()).getBoolean());
			if (fecmd.usableByPlayer())
				fecmd.setEnabledForPlayer(config.get("Player", fecmd.getCommandName(), fecmd.isEnabledForPlayer()).getBoolean());
			if (fecmd.canConsoleUseCommand())
				fecmd.setEnabledForConsole(config.get("Console", fecmd.getCommandName(), fecmd.isEnabledForConsole()).getBoolean());

			String category = "commands." + fecmd.getCommandName();
			config.addCustomCategoryComment(category, fecmd.getPermissionNode());

			fecmd.loadConfig(config, category);
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

	public static void registerCommands(FEModuleServerInitEvent e)
	{
        for (FEcmdModuleCommands cmd : cmdList)
        {
            cmd.registerExtraPermissions();
            FunctionHelper.registerServerCommand(cmd);
            APIRegistry.perms.registerPermissionDescription(cmd.getPermissionNode(), cmd.getCommandUsage(null));
        }
	}
	
}
