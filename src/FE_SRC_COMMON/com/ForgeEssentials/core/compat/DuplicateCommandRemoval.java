package com.ForgeEssentials.core.compat;

import com.ForgeEssentials.core.moduleLauncher.ModuleContainer;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class DuplicateCommandRemoval
{
	public static boolean removeDuplicateCommands;
	
	public static  void remove()
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		if (server.getCommandManager() instanceof CommandHandler)
		{
			try
			{
				HashMap<String, ICommand> initials = new HashMap<String, ICommand>();
				HashMultimap<String, ICommand> duplicates = HashMultimap.create();

				Set<ICommand> cmdList = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), "commandSet", "b");
				OutputHandler.debug("commandSet size: " + cmdList.size());

				ICommand keep;
				boolean worked;
				for (ICommand cmd : cmdList)
				{
					keep = initials.put(cmd.getCommandName(), cmd);
					worked = false;
					if (keep != null)
					{
						OutputHandler.debug("Duplicate command found! Name:" + keep.getCommandName());
						duplicates.put(cmd.getCommandName(), cmd);
						duplicates.put(cmd.getCommandName(), keep);
					}
				}
				
				Set<ICommand> toRemove = new HashSet();
				keep = null;
				Class<? extends ICommand> cmdClass;
				int kept = -1, other = -1;
				for (String name : duplicates.keySet())
				{
					keep = null;
					kept = -1;
					other = -1;
					cmdClass = null;
					
					for (ICommand cmd : duplicates.get(name))
					{
						other = getCommandPriority(cmd);
						
						if (keep == null)
						{
							kept = other;
							
							if (kept == -1)
							{
								keep = null;
								duplicates.remove(name, cmd);
							}
							else
								keep = cmd;
							
							continue;
						}
						
						if (kept > other)
						{
							toRemove.add(cmd);
							cmdClass = cmd.getClass();
							OutputHandler.debug("Removing command '" + cmd.getCommandName() + "' from class: " + cmdClass.getName());
						}
						else
						{
							toRemove.add(keep);
							cmdClass = keep.getClass();
							OutputHandler.debug("Removing command '" + keep.getCommandName() + "' from class: " + cmdClass.getName());
							
							keep = cmd;
							kept = other;
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
	
	 // 0 = vanilla. 1 = fe. 2 = other mods
	private static int getCommandPriority(ICommand cmd)
	{
		try
		{
			Class<?> cmdClass = cmd.getClass();
			Package pkg = cmdClass.getPackage();
			if (pkg == null || pkg.getName().contains("net.minecraft"))
				return 0;
			else if (pkg == null || pkg.getName().contains("ForgeEssentials"))
				return 1;
			else
				return 2;
		}
		catch (Exception e)
		{
			OutputHandler.debug("Can't remove " + cmd.getCommandName());
			OutputHandler.debug("" + e.getLocalizedMessage());
			e.printStackTrace();
			return -1;
		}
	}
}
