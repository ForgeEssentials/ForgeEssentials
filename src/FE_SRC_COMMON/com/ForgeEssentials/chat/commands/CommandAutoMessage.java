package com.ForgeEssentials.chat.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.chat.AutoMessage;
import com.ForgeEssentials.chat.ModuleChat;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandAutoMessage extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "automessage";
	}

	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList("am");
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		doStuff(sender, args);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		doStuff(sender, args);
	}

	public void doStuff(ICommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			OutputHandler.chatConfirmation(sender, "Possible options: select, broadcast, add, del.");
			return;
		}

		if (args[0].equalsIgnoreCase("select"))
		{
			try
			{
				int id = parseIntBounded(sender, args[1], 0, AutoMessage.msg.size());
				AutoMessage.currentMsgID = id;
				OutputHandler.chatConfirmation(sender, "You have selected \"" + AutoMessage.msg.get(id) + "\" as the next message.");
				return;
			}
			catch (Exception e)
			{
				OutputHandler.chatError(sender, "You have to select a message to broadcast next. Options: " + AutoMessage.msg.size());
				return;
			}
		}

		if (args[0].equalsIgnoreCase("broadcast"))
		{
			try
			{
				int id = parseIntBounded(sender, args[1], 0, AutoMessage.msg.size());
				FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendChatMsg(AutoMessage.msg.get(id));
				return;
			}
			catch (Exception e)
			{
				OutputHandler.chatError(sender, "You have to select a message to broadcast. Options: " + AutoMessage.msg.size());
				return;
			}
		}

		if (args[0].equalsIgnoreCase("add"))
		{
			try
			{
				String msg = "";
				for (String var : FunctionHelper.dropFirstString(args))
				{
					msg += " " + var;
				}
				OutputHandler.chatConfirmation(sender, msg.substring(1));
				AutoMessage.msg.add(msg.substring(1));
				ModuleChat.conf.forceSave();
				return;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				OutputHandler.chatError(sender, "Dafuq?");
				return;
			}
		}

		if (args[0].equalsIgnoreCase("del"))
		{
			try
			{
				int id = parseIntBounded(sender, args[1], 0, AutoMessage.msg.size());
				OutputHandler.chatConfirmation(sender, "Message \"" + AutoMessage.msg.get(id) + "\" removed.");
				AutoMessage.msg.remove(id);
				return;
			}
			catch (Exception e)
			{
				OutputHandler.chatError(sender, "You have to select a message to remove. Options: " + AutoMessage.msg.size());
				return;
			}
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.Chat.commands." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, "select", "broadcast", "add", "del");
		else
			return null;
	}
}
