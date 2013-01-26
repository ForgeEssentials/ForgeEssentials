package com.ForgeEssentials.core.compat;

import com.ForgeEssentials.core.moduleLauncher.ModuleContainer;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.TreeMultimap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class DuplicateCommandRemoval
{
	public static boolean removeDuplicateCommands;
	
	public static  void removeDuplicateCommands()
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		if (server.getCommandManager() instanceof CommandHandler)
		{
			try
			{
				Set<String> commandNames = new HashSet<String>();
				Set<String> toRemoveNames = new HashSet<String>();

				Set cmdList = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), "commandSet", "b");
				OutputHandler.debug("commandSet size: " + cmdList.size());

				for (Object cmdObj : cmdList)
				{
					ICommand cmd = (ICommand) cmdObj;
					if (!commandNames.add(cmd.getCommandName()))
					{
						OutputHandler.debug("Duplicate command found! Name:" + cmd.getCommandName());
						toRemoveNames.add(cmd.getCommandName());
					}
				}
				
				Set toRemove = new HashSet();
				for (Object cmdObj : cmdList)
				{
					ICommand cmd = (ICommand) cmdObj;
					if (toRemoveNames.contains(cmd.getCommandName()))
					{
						try
						{
							Class<?> cmdClass = cmd.getClass();
							Package pkg = cmdClass.getPackage();
							if (pkg == null || !pkg.getName().contains("ForgeEssentials"))
							{
								OutputHandler.debug("Removing command '" + cmd.getCommandName() + "' from class: " + cmdClass.getName());
								toRemove.add(cmd);
							}
						}
						catch (Exception e)
						{
							OutputHandler.debug("Can't remove " + cmd.getCommandName());
							OutputHandler.debug("" + e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
				}
				cmdList.removeAll(toRemove);
				OutputHandler.debug("commandSet size: " + cmdList.size());
				ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), cmdList, "commandSet", "b");

			}
			catch (Exception e)
			{
				OutputHandler.debug("Something broke: " + e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}
}
