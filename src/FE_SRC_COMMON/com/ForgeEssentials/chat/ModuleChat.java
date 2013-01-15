package com.ForgeEssentials.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.chat.commands.CommandMsg;
import com.ForgeEssentials.chat.commands.CommandMute;
import com.ForgeEssentials.chat.commands.CommandNickname;
import com.ForgeEssentials.chat.commands.CommandR;
import com.ForgeEssentials.chat.commands.CommandUnmute;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Config;
import com.ForgeEssentials.core.moduleLauncher.FEModule.Init;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PostInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.PreInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.ServerPostInit;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePostInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModulePreInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerInitEvent;
import com.ForgeEssentials.core.moduleLauncher.event.FEModuleServerPostInitEvent;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.permission.RegGroup;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;

@FEModule(name = "Chat", parentMod = ForgeEssentials.class, configClass=ConfigChat.class)
public class ModuleChat
{
	@Config
	public static ConfigChat conf;

	public ModuleChat()
	{
	}

	@PreInit
	public void preLoad(FEModulePreInitEvent e)
	{
		OutputHandler.SOP("Chat module is enabled. Loading...");
	}

	@Init
	public void load(FEModuleInitEvent e)
	{
		Chat chat = new Chat();
		MinecraftForge.EVENT_BUS.register(chat);
		MinecraftForge.EVENT_BUS.register(this); // for the permissions.
		NetworkRegistry.instance().registerChatListener(chat);

	}

	@PostInit
	public void postLoad(FEModulePostInitEvent e)
	{

		File banedFile = new File(ForgeEssentials.FEDIR, "bannedwords.txt");
		try
		{
			if (!banedFile.exists())
			{
				banedFile.createNewFile();
			}
			BufferedReader br = new BufferedReader(new FileReader(banedFile));
			String line;
			while ((line = br.readLine()) != null)
			{
				OutputHandler.debug(line.trim());
				Chat.bannedWords.add(line.trim());
			}
			br.close();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandMsg());
		e.registerServerCommand(new CommandR());
		e.registerServerCommand(new CommandNickname());
		e.registerServerCommand(new CommandMute());
		e.registerServerCommand(new CommandUnmute());
	}

	@ServerPostInit()
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		removeTell(FMLCommonHandler.instance().getMinecraftServerInstance());
	}

	@ForgeSubscribe
	public void registerPermissions(PermissionRegistrationEvent event)
	{
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.Chat.msg", true);
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.Chat.r", true);
	}
	
	private void removeTell(MinecraftServer server)
	{
		if (server.getCommandManager() instanceof CommandHandler)
		{
			try
			{
				Set cmdList = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler)server.getCommandManager(), "commandSet", "b");

				ICommand toRemove = null;
				Class<?> cmdClass = null;
				for (Object cmdObj : cmdList)
				{
					ICommand cmd = (ICommand) cmdObj;
					if (cmd.getCommandName().equalsIgnoreCase("tell"))
					{
						try
						{
							cmdClass = cmd.getClass();
							Package pkg = cmdClass.getPackage();
							if (pkg == null || !pkg.getName().contains("ForgeEssentials"))
							{
								toRemove = cmd;
								break;
							}
						}
						catch (Exception e)
						{
							OutputHandler.debug("Can't remove " + cmd.getCommandName());
							OutputHandler.debug(""+e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
				}
				if(toRemove != null)
				{
					OutputHandler.debug("Removing command '" + toRemove.getCommandName() + "' from class: " + cmdClass.getName());
					cmdList.remove(toRemove);
				}
				ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler)server.getCommandManager(), cmdList, "commandSet", "b");
				
				Map cmds = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler)server.getCommandManager(), "commandMap", "a");
				if(cmds.containsKey("tell"))
				{
					OutputHandler.debug("Removing command tell from vanilla set.");
					cmds.remove("tell");
					cmds.put("tell", new CommandMsg());
				}
				ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler)server.getCommandManager(), cmds, "commandMap", "a");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
