package com.ForgeEssentials.playerLogger.rollback;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.playerLogger.ModulePlayerLogger;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;
import com.ForgeEssentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Rollback command. WIP!
 * @author Dries007
 */
public class CommandRollback extends ForgeEssentialsCommandBase
{
	HashMap<ICommandSender, String> que = new HashMap<ICommandSender, String>();
	
	@Override
	public String getCommandName()
	{
		return "rollback";
	}

	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList(new String[]
		{ "rb" });
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		doRollback(sender, args);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		doRollback(sender, args);
	}

	/*
	 * We want: /rollback <username> [undo|clear]
	 */
	private void doRollback(ICommandSender sender, String[] args)
	{
		ArrayList userlist = new ArrayList();
		userlist.addAll(Arrays.asList(MinecraftServer.getServer().getConfigurationManager().getAvailablePlayerDat()));
		/*
		 * Cmd info
		 */
		if (args.length == 0)
		{
			sender.sendChatToPlayer("--- Rollback usage ---");
			sender.sendChatToPlayer("All actions must be confirmed with '/rb ok'.");
			sender.sendChatToPlayer("All actions can be canceld with '/rb abort'.");
			sender.sendChatToPlayer("'/rb clear <username>' => Removes a players data.");
			sender.sendChatToPlayer("'/rb undo <username>' => Undo a rollback. You can specify time and radius");
			sender.sendChatToPlayer("'/rb <undo|rollback> <username>' => Rolls back a players. All the way!");
			sender.sendChatToPlayer("'/rb <undo|rollback> <username> <rad>' => Format like this: 10r");
			sender.sendChatToPlayer("'/rb <undo|rollback> <username> <time>' => Format time like this: 10d = 10 days, 10h = 10 hours.");
			sender.sendChatToPlayer("A combo of the above is possible too.");
			return;
		}
		
		/* 
		 * Only 1 arg
		 */
		if (args[0].equalsIgnoreCase("ok"))
		{
			if (que.containsKey(sender))
			{
				execute(sender, que.get(sender).split(" "));
			}
			else
			{
				OutputHandler.chatError(sender, "No pending commands.");
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("abort"))
		{
			if (que.containsKey(sender))
			{
				que.remove(sender);
				OutputHandler.chatConfirmation(sender, "Command aborted");
			}
			else
			{
				OutputHandler.chatError(sender, "No pending commands.");
			}
			return;
		}
		
		/* 
		 * 2 or more args
		 * Arg 1 must be a username.
		 * Arg 0 should be a command.
		 */
		if (args.length <= 2)
		{
			OutputHandler.chatError(sender, "You have to provide a username!");
			return;
		}
		else if (!userlist.contains(args[1]))
		{
			OutputHandler.chatError(sender, "That player is not in the database.");
			return;
		}
		
		/*
		 * So it is a command.
		 */
		if (args[0].equalsIgnoreCase("clear"))
		{
			OutputHandler.chatWarning(sender, "Confirm the clearing of all blockchanges for player " + args[1]);
			que.put(sender, "clear " + args[1]);
		}
		else if (args[0].equalsIgnoreCase("undo"))
		{
			
		}
		else if (args[0].equalsIgnoreCase("rollback") || args[0].equalsIgnoreCase("rb"))
		{
			
		}
		else if (args.length > 1 && args[1].equalsIgnoreCase("undo"))
		{
			parse(sender, args, true);
		}
		else
		{
			parse(sender, args, false);
		}
	}

	private void execute(ICommandSender sender, String[] args)
	{
		if (args[0].equalsIgnoreCase("clear"))
		{
			try
			{	
				Statement st = ModulePlayerLogger.getConnection().createStatement();
				st.execute("DELETE FROM `blockchange` WHERE `player` LIKE '" + args[1] + "'");
				OutputHandler.chatConfirmation(sender, "Removed all records of " + args[1]);
				st.close();
			}
			catch (Exception e)
			{
				OutputHandler.chatError(sender, "Error. " + e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}

	public void parse(ICommandSender sender, String[] args, boolean undo)
	{
		int time = 0;
		WorldPoint point = (sender instanceof EntityPlayer) ? new WorldPoint((EntityPlayer) sender) : null;
		int rad = 0;
		
		for (int i = 1; i < args.length; i++)
		{
			String arg = args[i];
			if (arg.contains("d"))
			{
				time = 24 * parseInt(sender, arg.replaceAll("d", ""));
			}
			else if (arg.contains("h"))
			{
				time = parseInt(sender, arg.replaceAll("h", ""));
			}
			else if (arg.contains("r"))
			{
				rad = parseIntWithMin(sender, arg.replaceAll("r", ""), 0);
			}
		}
		try
		{
			TaskRegistry.registerTask(new TickTaskRollback(sender, args[0], false, time, point, rad));
		}
		catch (SQLException e)
		{
			OutputHandler.chatError(sender, "Error. " + e.getLocalizedMessage());
			e.printStackTrace();
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
		return "ForgeEssentials.playerLogger." + getCommandName();
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			 return getListOfStringsMatchingLastWord(args, "ok", "abort", "clear", "undo", "rollback");
		else if (args.length == 2)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getAvailablePlayerDat());
		else if (args.length == 2)
			return getListOfStringsMatchingLastWord(args, "clear", "undo");
		else
			return null;
	}
}
