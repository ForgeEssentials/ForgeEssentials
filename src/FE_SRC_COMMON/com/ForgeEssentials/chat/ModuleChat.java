package com.ForgeEssentials.chat;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
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
import com.ForgeEssentials.chat.irc.IRCThread;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.compat.DuplicateCommandRemoval;
import com.ForgeEssentials.core.misc.packetInspector.PacketAnalyzerRegistry;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.events.modules.FEModuleInitEvent;
import com.ForgeEssentials.util.events.modules.FEModulePostInitEvent;
import com.ForgeEssentials.util.events.modules.FEModulePreInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerPostInitEvent;
import com.ForgeEssentials.util.events.modules.FEModuleServerStopEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;

@FEModule(name = "Chat", parentMod = ForgeEssentials.class, configClass = ConfigChat.class)
public class ModuleChat
{
	@FEModule.Config
	public static ConfigChat   conf;

	@FEModule.ModuleDir
	public static File         moduleDir;
	
	public static PrintWriter  chatLog;
	public static PrintWriter  cmdLog;
	public static File logdir;

	private MailSystem         mailsystem;
	private IRCThread irc;

	public ModuleChat()
	{
	}

	@FEModule.PreInit
	public void load(FEModulePreInitEvent e)
	{
		Packet3Chat.maxChatLength = 200;
	}

	@FEModule.Init
	public void load(FEModuleInitEvent e)
	{
		MinecraftForge.EVENT_BUS.register(new ChatFormatter());
		MinecraftForge.EVENT_BUS.register(new CommandMuter());
		
		PacketAnalyzerRegistry.register(new PacketAnalyzerChat(), new int [] { 201 });
		//System.out.println("Starting irc connection");
		//irc = new IRCThread();
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
		
		try{
			logdir = new File(moduleDir, "logs/");
			logdir.mkdirs();
		}catch (Exception e1){
			OutputHandler.felog.warning("Could not create chat log directory!");
		}
		try
		{
		    File file = new File(logdir, "chat-" + FunctionHelper.getCurrentDateString() + ".log");
		    if (!file.exists()) file.createNewFile();
		    chatLog = new PrintWriter(file);
		}
		catch (Exception e1)
		{
			OutputHandler.felog.warning("Could not create chat log file!");
		}
		
		try
        {
            File file = new File(logdir, "cmd-" + FunctionHelper.getCurrentDateString() + ".log");
            if (!file.exists()) file.createNewFile();
            cmdLog = new PrintWriter(file);
        }
        catch (Exception e1)
        {
        	OutputHandler.felog.warning("Could not create command log file!");
        }
	}

	@FEModule.ServerPostInit()
	public void serverStarted(FEModuleServerPostInitEvent e)
	{
		removeTell(FMLCommonHandler.instance().getMinecraftServerInstance());
		new AutoMessage(FMLCommonHandler.instance().getMinecraftServerInstance());
		MailSystem.LoadAll();
		GameRegistry.registerPlayerTracker(mailsystem);
		//irc.joinChannels();
	}

	@FEModule.ServerStop()
	public void serverStopping(FEModuleServerStopEvent e)
	{
		MailSystem.SaveAll();
		
		chatLog.close();
		cmdLog.close();
		//irc.endConnection();
	}

	@PermRegister
	public static void registerPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel("ForgeEssentials.Chat.commands.r", RegGroup.GUESTS);
		event.registerPermissionLevel("ForgeEssentials.Chat.commands.msg", RegGroup.GUESTS);
		event.registerPermissionLevel("ForgeEssentials.Chat.commands.mail", RegGroup.GUESTS);

		event.registerPermissionLevel("ForgeEssentials.Chat.commands.nickname", RegGroup.MEMBERS);
		event.registerPermissionLevel("ForgeEssentials.Chat.usecolor", RegGroup.MEMBERS);

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
				Set<?> cmdList = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), DuplicateCommandRemoval.FIELDNAME);

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
							OutputHandler.felog.finer("Can't remove " + cmd.getCommandName());
							OutputHandler.felog.finer("" + e.getLocalizedMessage());
							e.printStackTrace();
						}
					}
				}
				if (toRemove != null)
				{
					OutputHandler.felog.finer("Removing command '" + toRemove.getCommandName() + "' from class: " + cmdClass.getName());
					cmdList.remove(toRemove);
				}
				ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), cmdList, "commandSet", "b", "field_71561_b");

				Map<String, CommandMsg> cmds = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), "commandMap", "a", "field_71562_a");
				if (cmds.containsKey("tell"))
				{
					OutputHandler.felog.finer("Removing command tell from vanilla set.");
					cmds.remove("tell");
					cmds.put("tell", new CommandMsg());
				}
				ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), cmds, "commandMap", "a", "field_71562_a");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
