package com.forgeessentials.chat.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.chat.Mail;
import com.forgeessentials.chat.MailSystem;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.Localization;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandMail extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "mail";
	}
	
	@Override
	public List<String> getCommandAliases()
    {
        return Arrays.asList("femail");
    }

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (!Arrays.asList(MinecraftServer.getServer().getConfigurationManager().getAvailablePlayerDat()).contains(args[0]))
		{
			OutputHandler.chatError(sender, Localization.format("command.mail.unknown", args[0]));
			return;
		}
		StringBuilder cmd = new StringBuilder(args.toString().length());
		for (int i = 1; i < args.length; i++)
		{
			cmd.append(args[i]);
			cmd.append(" ");
		}
		MailSystem.AddMail(new Mail("", sender.getCommandSenderName(), args[0], cmd.toString()));
		OutputHandler.chatConfirmation(sender, Localization.format("command.mail.posted", args[0]));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (!Arrays.asList(MinecraftServer.getServer().getConfigurationManager().getAvailablePlayerDat()).contains(args[0]))
		{
			OutputHandler.chatError(sender, Localization.format("command.mail.unknown", args[0]));
			return;
		}
		StringBuilder cmd = new StringBuilder(args.toString().length());
		for (int i = 1; i < args.length; i++)
		{
			cmd.append(args[i]);
			cmd.append(" ");
		}
		MailSystem.AddMail(new Mail("", sender.getCommandSenderName(), args[0], cmd.toString()));
		OutputHandler.chatConfirmation(sender, Localization.format("command.mail.posted", args[0]));
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
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 2)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
