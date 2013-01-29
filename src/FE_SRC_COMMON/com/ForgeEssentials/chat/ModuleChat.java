package com.ForgeEssentials.chat;

import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.FEModule.Config;
import com.ForgeEssentials.api.modules.FEModule.Init;
import com.ForgeEssentials.api.modules.FEModule.ModuleDir;
import com.ForgeEssentials.api.modules.FEModule.PostInit;
import com.ForgeEssentials.api.modules.FEModule.PreInit;
import com.ForgeEssentials.api.modules.FEModule.ServerInit;
import com.ForgeEssentials.api.modules.FEModule.ServerPostInit;
import com.ForgeEssentials.api.modules.event.FEModuleInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePostInitEvent;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerInitEvent;
import com.ForgeEssentials.api.modules.event.FEModuleServerPostInitEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.chat.commands.CommandMsg;
import com.ForgeEssentials.chat.commands.CommandMute;
import com.ForgeEssentials.chat.commands.CommandNickname;
import com.ForgeEssentials.chat.commands.CommandR;
import com.ForgeEssentials.chat.commands.CommandUnmute;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.permission.PermissionRegistrationEvent;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import java.io.File;
import java.util.Map;
import java.util.Set;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;

@FEModule(name = "Chat", parentMod = ForgeEssentials.class, configClass = ConfigChat.class)
public class ModuleChat
{
	@Config
	public static ConfigChat	conf;

	@ModuleDir
	public static File			moduleDir;

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
	}

	@PostInit
	public void postLoad(FEModulePostInitEvent e)
	{
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
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.Chat.r", false);
		event.registerPerm(this, RegGroup.GUESTS, "ForgeEssentials.Chat.msg", false);

		event.registerPerm(this, RegGroup.MEMBERS, "ForgeEssentials.Chat.commands.msg", true);
		event.registerPerm(this, RegGroup.MEMBERS, "ForgeEssentials.Chat.commands.r", true);
		event.registerPerm(this, RegGroup.MEMBERS, "ForgeEssentials.Chat.commands.nickname", true);

		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.Chat.commands.nickname.others", true);
		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.Chat.commands.mute", true);
		event.registerPerm(this, RegGroup.OWNERS, "ForgeEssentials.Chat.commands.unmute", true);
	}

	private void removeTell(MinecraftServer server)
	{
		if (server.getCommandManager() instanceof CommandHandler)
		{
			try
			{
				Set cmdList = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), "commandSet", "b");

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
							OutputHandler.debug("" + e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
				}
				if (toRemove != null)
				{
					OutputHandler.debug("Removing command '" + toRemove.getCommandName() + "' from class: " + cmdClass.getName());
					cmdList.remove(toRemove);
				}
				ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), cmdList, "commandSet", "b");

				Map cmds = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), "commandMap", "a");
				if (cmds.containsKey("tell"))
				{
					OutputHandler.debug("Removing command tell from vanilla set.");
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
