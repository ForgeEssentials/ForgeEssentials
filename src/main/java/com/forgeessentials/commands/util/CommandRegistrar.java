package com.forgeessentials.commands.util;

import com.forgeessentials.commands.*;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.server.CommandHandlerForge;

import java.util.ArrayList;

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
				fecmd.setEnabledForCmdBlock(config.get("Player", fecmd.getCommandName(), fecmd.isEnabledForPlayer()).getBoolean());
			if (fecmd.canConsoleUseCommand())
				fecmd.setEnabledForCmdBlock(config.get("Console", fecmd.getCommandName(), fecmd.isEnabledForConsole()).getBoolean());

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
            e.registerServerCommand(cmd);
        }
	}
}
