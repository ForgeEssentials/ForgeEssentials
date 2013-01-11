package com.ForgeEssentials.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import com.ForgeEssentials.core.moduleLauncher.FEModule.PreInit;
import com.ForgeEssentials.core.moduleLauncher.FEModule.*;
import com.ForgeEssentials.core.moduleLauncher.IModuleConfig;
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
	public void preLoad(FMLPreInitializationEvent e)
	{
		OutputHandler.SOP("Chat module is enabled. Loading...");
	}

	@Init
	public void load(FMLInitializationEvent e)
	{
		Chat chat = new Chat();
		MinecraftForge.EVENT_BUS.register(chat);
		MinecraftForge.EVENT_BUS.register(this); // for the permissions.
		NetworkRegistry.instance().registerChatListener(chat);

	}

	@PostInit
	public void postLoad(FMLPostInitializationEvent e)
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
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandMsg());
		e.registerServerCommand(new CommandR());
		e.registerServerCommand(new CommandNickname());
		e.registerServerCommand(new CommandMute());
		e.registerServerCommand(new CommandUnmute());
	}

	@ServerPostInit
	public void serverStarted(FMLServerStartedEvent e)
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
				Set cmds = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler)server.getCommandManager(), "commandSet", "b");
				
				for (Object cmdObj : cmds)
				{
					ICommand cmd = (ICommand) cmdObj;
					if (cmd.getCommandName().equalsIgnoreCase("tell"))
					{
						try
						{
							Class<?> cmdClass = cmd.getClass();
							Package pkg = cmdClass.getPackage();
							if (pkg == null || !pkg.getName().contains("ForgeEssentials"))
							{
								OutputHandler.debug("Removing command '" + cmd.getCommandName() + "' from class: " + cmdClass.getName());
								cmds.remove(cmd);
							}
						}
						catch (Exception e)
						{
							OutputHandler.debug("dafug? Got exception:" + e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
				}
				ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler)server.getCommandManager(), cmds, "commandSet", "b");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
