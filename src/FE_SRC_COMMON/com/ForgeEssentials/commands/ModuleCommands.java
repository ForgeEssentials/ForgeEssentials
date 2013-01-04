package com.ForgeEssentials.commands;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.commands.util.ConfigCmd;
import com.ForgeEssentials.commands.util.EventHandler;
import com.ForgeEssentials.commands.util.PlayerTrackerCommands;
import com.ForgeEssentials.commands.util.TickHandlerCommands;
import com.ForgeEssentials.commands.vanilla.CommandBan;
import com.ForgeEssentials.commands.vanilla.CommandBanIp;
import com.ForgeEssentials.commands.vanilla.CommandBanlist;
import com.ForgeEssentials.commands.vanilla.CommandDebug;
import com.ForgeEssentials.commands.vanilla.CommandDefaultGameMode;
import com.ForgeEssentials.commands.vanilla.CommandDeop;
import com.ForgeEssentials.commands.vanilla.CommandDifficulty;
import com.ForgeEssentials.commands.vanilla.CommandEnchant;
import com.ForgeEssentials.commands.vanilla.CommandGameRule;
import com.ForgeEssentials.commands.vanilla.CommandKick;
import com.ForgeEssentials.commands.vanilla.CommandMe;
import com.ForgeEssentials.commands.vanilla.CommandOp;
import com.ForgeEssentials.commands.vanilla.CommandPardon;
import com.ForgeEssentials.commands.vanilla.CommandPardonIp;
import com.ForgeEssentials.commands.vanilla.CommandPublish;
import com.ForgeEssentials.commands.vanilla.CommandSaveAll;
import com.ForgeEssentials.commands.vanilla.CommandSaveOff;
import com.ForgeEssentials.commands.vanilla.CommandSaveOn;
import com.ForgeEssentials.commands.vanilla.CommandSay;
import com.ForgeEssentials.commands.vanilla.CommandSeed;
import com.ForgeEssentials.commands.vanilla.CommandStop;
import com.ForgeEssentials.commands.vanilla.CommandTell;
import com.ForgeEssentials.commands.vanilla.CommandTime;
import com.ForgeEssentials.commands.vanilla.CommandToggleDownfall;
import com.ForgeEssentials.commands.vanilla.CommandWeather;
import com.ForgeEssentials.commands.vanilla.CommandWhitelist;
import com.ForgeEssentials.commands.vanilla.CommandXP;
import com.ForgeEssentials.core.IFEModule;
import com.ForgeEssentials.core.IModuleConfig;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.permission.RegGroup;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ModuleCommands implements IFEModule
{
	public static ConfigCmd conf;
	public static boolean removeDuplicateCommands;
	
	public ModuleCommands()
	{
		
	}

	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Commands module is enabled. Loading...");
		conf = new ConfigCmd();
	}

	public void load(FMLInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(this); // for the permissions.
		GameRegistry.registerPlayerTracker(new PlayerTrackerCommands());
	}

	@Override
	public void postLoad(FMLPostInitializationEvent e)
	{
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e)
	{
		//general
		e.registerServerCommand(new CommandMotd());
		e.registerServerCommand(new CommandRules());
		e.registerServerCommand(new CommandModlist());
		//utility
		e.registerServerCommand(new CommandButcher());
		e.registerServerCommand(new CommandRemove());
		e.registerServerCommand(new CommandSpawnMob());
		e.registerServerCommand(new CommandTPS());
		e.registerServerCommand(new CommandKit());
		e.registerServerCommand(new CommandEnderchest());
		e.registerServerCommand(new CommandVirtualchest());
		e.registerServerCommand(new CommandCapabilities());
		e.registerServerCommand(new CommandSetspawn());
		e.registerServerCommand(new CommandJump());
		e.registerServerCommand(new CommandCraft());
		//op
		e.registerServerCommand(new CommandServerDo());
		//fun
		e.registerServerCommand(new CommandSmite());
		e.registerServerCommand(new CommandBurn());
		e.registerServerCommand(new CommandPotion());
		e.registerServerCommand(new CommandColorize());
		//teleport
		e.registerServerCommand(new CommandBack());
		e.registerServerCommand(new CommandBed());
		e.registerServerCommand(new CommandHome());
		e.registerServerCommand(new CommandSpawn());
		e.registerServerCommand(new CommandTp());
		e.registerServerCommand(new CommandTphere());
		e.registerServerCommand(new CommandTppos());
		e.registerServerCommand(new CommandWarp());
		//cheat
		e.registerServerCommand(new CommandRepair());
		e.registerServerCommand(new CommandHeal());
		//Vanilla Override
		e.registerServerCommand(new CommandBan());
		e.registerServerCommand(new CommandBanIp());
		e.registerServerCommand(new CommandBanlist());
		e.registerServerCommand(new CommandDebug());
		e.registerServerCommand(new CommandDefaultGameMode());
		e.registerServerCommand(new CommandDeop());
		e.registerServerCommand(new CommandDifficulty());
		e.registerServerCommand(new CommandEnchant());
		e.registerServerCommand(new CommandGameRule());
//		e.registerServerCommand(new CommandHelp());
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
		e.registerServerCommand(new CommandStop());
		e.registerServerCommand(new CommandTell());
		e.registerServerCommand(new CommandTime());
		e.registerServerCommand(new CommandToggleDownfall());
		e.registerServerCommand(new CommandWeather());
		e.registerServerCommand(new CommandWhitelist());
		e.registerServerCommand(new CommandXP());
		e.registerServerCommand(new CommandKill());
		e.registerServerCommand(new CommandGive());
		e.registerServerCommand(new CommandClearInventory());
		e.registerServerCommand(new CommandGameMode());
	}

	@Override
	public void serverStarted(FMLServerStartedEvent e)
	{
		TickRegistry.registerScheduledTickHandler(new TickHandlerCommands(), Side.SERVER);
		if(removeDuplicateCommands) removeDuplicateCommands(FMLCommonHandler.instance().getMinecraftServerInstance());
	}


	private void removeDuplicateCommands(MinecraftServer server) 
	{
		if(server.getCommandManager() instanceof CommandHandler)
		{
			try
			{
				Set<String> commandNames = new HashSet<String>();
				Set<String> toRemoveNames = new HashSet<String>();
				CommandHandler cmdMng = (CommandHandler) server.getCommandManager();
				
				for(Object cmdObj : cmdMng.commandSet)
				{
					ICommand cmd = (ICommand) cmdObj;
					if(!commandNames.add(cmd.getCommandName()))
					{
						OutputHandler.debug("Duplicate command found! Name:" + cmd.getCommandName());
						toRemoveNames.add(cmd.getCommandName());
					}
				}
				Set toRemove = new HashSet();
				for(Object cmdObj : cmdMng.commandSet)
				{
					ICommand cmd = (ICommand) cmdObj;
					if(toRemoveNames.contains(cmd.getCommandName()))
					{
						Class<?> cmdClass = cmd.getClass();
						if(!cmdClass.getPackage().getName().contains("ForgeEssentials"))
						{
							OutputHandler.debug("Removing command '" + cmd.getCommandName() + "' from class: " + cmdClass.getName());
							toRemove.add(cmd.getCommandName());
						}
					}
				}
				cmdMng.commandSet.removeAll(toRemove);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.BasicCommands", true);
		event.registerPerm(this, RegGroup.MEMBERS, "ForgeEssentials.BasicCommands.compass", true);
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.list", true);
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.rules", true);
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.motd", true);
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.tps", true);
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.BasicCommands.modlist", true);
	}

	@Override
	public void serverStopping(FMLServerStoppingEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public IModuleConfig getConfig() 
	{
		return conf;
	}
}