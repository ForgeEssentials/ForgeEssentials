package com.ForgeEssentials.tickets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.ChatUtils;

public class Command extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "ticket";
	}

	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList("tickets");
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
		String c = FEChatFormatCodes.DARKAQUA.toString();
		if (args.length == 0)
		{
			String usage = "list|new|view";
			if (permcheck(sender, "tp"))
			{
				usage += "|tp <id>";
			}
			if (permcheck(sender, "admin"))
			{
				usage += "|del <id>";
			}
			OutputHandler.chatError(sender, "Usage: /ticket <" + usage + ">");
			return;
		}

		if (args[0].equalsIgnoreCase("view") && permcheck(sender, "view"))
		{
			if (args.length != 2)
			{
				OutputHandler.chatError(sender, Localization.get("command.ticket.view.usage"));
				return;
			}
			int id = parseIntBounded(sender, args[1], 0, ModuleTickets.currentID + 1);
			Ticket t = ModuleTickets.getID(id);
			ChatUtils.sendMessage(sender, c + "#" + t.id + " : " + t.creator + " - " + t.category + " - " + t.message);
		}

		if (args[0].equalsIgnoreCase("list") && permcheck(sender, "view"))
		{
			int page = 0;
			int pages = ModuleTickets.ticketList.size() / 7;
			if (args.length == 2)
			{
				page = parseIntBounded(sender, args[1], 0, pages);
			}
			ChatUtils.sendMessage(sender, c + Localization.get("message.other.ticketList.header"));
			for (int i = page * 7; i < (page + 1) * 7; i++)
			{
				try
				{
					Ticket t = ModuleTickets.ticketList.get(i);
					ChatUtils.sendMessage(sender, "#" + t.id + ": " + t.creator + " - " + t.category + " - " + t.message);
				}
				catch (Exception e)
				{
					break;
				}
			}
			ChatUtils.sendMessage(sender, c + Localization.format("message.other.ticketList.pages", page, pages));
			return;
		}

		if (args[0].equalsIgnoreCase("new") && permcheck(sender, "new"))
		{
			if (args.length < 3)
			{
				OutputHandler.chatError(sender, Localization.get("command.ticket.new.usage"));
				return;
			}
			if (!ModuleTickets.categories.contains(args[1]))
			{
				OutputHandler.chatError(sender, Localization.format("message.error.illegalCategory", args[1]));
				return;
			}
			String msg = "";
			for (String var : FunctionHelper.dropFirstString(FunctionHelper.dropFirstString(args)))
			{
				msg += " " + var;
			}
			msg = msg.substring(1);
			Ticket t = new Ticket(sender, args[1], msg);
			ModuleTickets.ticketList.add(t);
			ChatUtils.sendMessage(sender, c + Localization.format("message.confim.ticketPost", t.id));
			return;
		}

		if (args[0].equalsIgnoreCase("tp") && permcheck(sender, "tp"))
		{
			if (args.length != 2)
			{
				OutputHandler.chatError(sender, Localization.get("command.ticket.tp.usage"));
				return;
			}
			int id = parseIntBounded(sender, args[1], 0, ModuleTickets.currentID + 1);
			TeleportCenter.addToTpQue(ModuleTickets.getID(id).point, (EntityPlayer) sender);
		}

		if (args[0].equalsIgnoreCase("del") && permcheck(sender, "admin"))
		{
			if (args.length != 2)
			{
				OutputHandler.chatError(sender, Localization.get("command.ticket.dev.usage"));
				return;
			}
			int id = parseIntBounded(sender, args[1], 0, ModuleTickets.currentID);
			ModuleTickets.ticketList.remove(ModuleTickets.getID(id));
			ChatUtils.sendMessage(sender, c + Localization.format("message.confim.ticketPost", id));
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
		return ModuleTickets.PERMBASE + ".command";
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, "list", "new", "view", "tp", "del");

		if (args.length == 2 && args[0].equalsIgnoreCase("new"))
			return getListOfStringsFromIterableMatchingLastWord(args, ModuleTickets.categories);

		if (args.length == 2 && (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("del")))
		{
			List<String> list = new ArrayList<String>();
			for (Ticket t : ModuleTickets.ticketList)
			{
				list.add("" + t.id);
			}
			return getListOfStringsFromIterableMatchingLastWord(args, list);
		}
		return null;
	}

	public boolean permcheck(ICommandSender sender, String perm)
	{
		if (sender instanceof EntityPlayer)
			return APIRegistry.perms.checkPermAllowed(new PermQueryPlayer((EntityPlayer) sender, ModuleTickets.PERMBASE + "." + perm));
		else
			return true;
	}
}
