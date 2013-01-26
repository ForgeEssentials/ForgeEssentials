package com.ForgeEssentials.commands.util;

import java.util.ArrayList;
import java.util.Set;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.commands.*;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class CommandRegistrar 
{
	public static ArrayList<ForgeEssentialsCommandBase> cmdList = new ArrayList();
	
	static
	{
		cmdList.add(new CommandMotd());
		cmdList.add(new CommandRules());
		cmdList.add(new CommandModlist());
		cmdList.add(new CommandButcher());
		cmdList.add(new CommandRemove());
		cmdList.add(new CommandSpawnMob());
		cmdList.add(new CommandTPS());
		cmdList.add(new CommandAFK());
		cmdList.add(new CommandKit());
		cmdList.add(new CommandEnderchest());
		cmdList.add(new CommandVirtualchest());
		cmdList.add(new CommandCapabilities());
//		cmdList.add(new CommandSetspawn());
		cmdList.add(new CommandJump());
		cmdList.add(new CommandCraft());
		cmdList.add(new CommandPing());
		cmdList.add(new CommandServerDo());
		cmdList.add(new CommandSeeInventory());
		cmdList.add(new CommandSmite());
		cmdList.add(new CommandBurn());
		cmdList.add(new CommandPotion());
		cmdList.add(new CommandColorize());
		cmdList.add(new CommandBack());
		cmdList.add(new CommandBed());
		cmdList.add(new CommandHome());
		cmdList.add(new CommandSpawn());
		cmdList.add(new CommandTp());
		cmdList.add(new CommandTphere());
		cmdList.add(new CommandTppos());
		cmdList.add(new CommandWarp());
		cmdList.add(new CommandRepair());
		cmdList.add(new CommandHeal());
		cmdList.add(new CommandKill());
		cmdList.add(new CommandGive());
		cmdList.add(new CommandI());
		cmdList.add(new CommandClearInventory());
		cmdList.add(new CommandGameMode());
		cmdList.add(new CommandSlap());
		cmdList.add(new CommandVanish());
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
			
			for (ForgeEssentialsCommandBase fecmd : cmdList)
			{		
				if(fecmd.usefullCmdBlock())			config.get("CommandBlock", fecmd.getCommandName(), fecmd.enableCmdBlock);
				if(fecmd.usefullPlayer()) 			config.get("Player", fecmd.getCommandName(), fecmd.enablePlayer);
				if(fecmd.canConsoleUseCommand()) 	config.get("Console", fecmd.getCommandName(), fecmd.enableConsole);
				
				String category = "commands." + fecmd.getCommandName();
				config.addCustomCategoryComment(category, fecmd.getCommandPerm());
				for(String alias : config.get(category, "aliases", fecmd.getDefaultAliases()).valueList)
				{
					fecmd.aliasList.add(alias);
				}
				fecmd.doConfig(config, category);
			}
		}
		catch(Exception e)
		{e.printStackTrace();}
		
		config.save();
	}
	
	public static void load(FMLServerStartingEvent e) 
	{
		for(ForgeEssentialsCommandBase cmd : cmdList)
		{
			e.registerServerCommand(cmd);
		}
	}
}
