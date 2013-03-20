package com.ForgeEssentials.chat;

import java.io.File;
import java.util.Map;
import java.util.Set;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.ForgeEssentialsRegistrar.PermRegister;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePostInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerPostInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerStopEvent;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.chat.commands.CommandAutoMessage;
import com.ForgeEssentials.chat.commands.CommandMail;
import com.ForgeEssentials.chat.commands.CommandMsg;
import com.ForgeEssentials.chat.commands.CommandMute;
import com.ForgeEssentials.chat.commands.CommandNickname;
import com.ForgeEssentials.chat.commands.CommandPm;
import com.ForgeEssentials.chat.commands.CommandR;
import com.ForgeEssentials.chat.commands.CommandUnmute;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;

@FEModule(name = "Chat", parentMod = ForgeEssentials.class, configClass = ConfigChat.class)
public class ModuleChat
{
	@FEModule.Config
	public static ConfigChat	conf;

	@FEModule.ModuleDir
	public static File			moduleDir;

	private MailSystem			mailsystem;

	public ModuleChat()
	{
	}

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		ChatFormatter chat = new ChatFormatter();
		MinecraftForge.EVENT_BUS.register(chat);
	}

	@FEModule.PostInit
	public void postLoad(FEModulePostInitEvent e)
	{
		mailsystem = new MailSystem();
	}

	@FEModule.ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		e.registerServerCommand(new CommandMsg());
		e.registerServerCommand(new CommandR());
		e.registerServerCommand(new CommandNickname());
		e.registerServerCommand(new CommandPm());
		e.registerServerCommand(new CommandMute());
		e.registerServerCommand(new CommandUnmute());
		e.registerServerCommand(new CommandMail());
		e.registerServerCommand(new CommandAutoMessage());
	}

	@FEModule.ServerPostInit()
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		removeTell(FMLCommonHandler.instance().getMinecraftServerInstance());
		new AutoMessage(FMLCommonHandler.instance().getMinecraftServerInstance());
		MailSystem.LoadAll();
		GameRegistry.registerPlayerTracker(mailsystem);
	}

	@FEModule.ServerStop()
	public void serverStopping(FEModuleServerStopEvent e)
	{
		MailSystem.SaveAll();
	}

	@PermRegister
	public static void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.Chat.commands.r", RegGroup.GUESTS);
		event.registerPermissionLevel("ForgeEssentials.Chat.commands.msg", RegGroup.GUESTS);
		event.registerPermissionLevel("ForgeEssentials.Chat.commands.mail", RegGroup.GUESTS);

		event.registerPermissionLevel("ForgeEssentials.Chat.commands.nickname", RegGroup.MEMBERS);

		event.registerPermissionLevel("ForgeEssentials.Chat.commands.nickname.others", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.Chat.commands.mute", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.Chat.commands.unmute", RegGroup.OWNERS);
		event.registerPermissionLevel("ForgeEssentials.Chat.commands.automessage", RegGroup.OWNERS);
	}

	private void removeTell(MinecraftServer server)
	{
		if (server.getCommandManager() instanceof CommandHandler)
		{
			try
			{
				Set<?> cmdList = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), "commandSet", "b");

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
							OutputHandler.finer("Can't remove " + cmd.getCommandName());
							OutputHandler.finer("" + e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
				}
				if (toRemove != null)
				{
					OutputHandler.finer("Removing command '" + toRemove.getCommandName() + "' from class: " + cmdClass.getName());
					cmdList.remove(toRemove);
				}
				ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), cmdList, "commandSet", "b");

				Map<String, CommandMsg> cmds = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), "commandMap", "a");
				if (cmds.containsKey("tell"))
				{
					OutputHandler.finer("Removing command tell from vanilla set.");
					cmds.remove("tell");
					cmds.put("tell", new CommandMsg());
				}
				ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), cmds, "commandMap", "a");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
