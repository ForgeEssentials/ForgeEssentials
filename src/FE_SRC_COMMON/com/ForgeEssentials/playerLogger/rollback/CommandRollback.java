package com.ForgeEssentials.playerLogger.rollback;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.playerLogger.ModulePlayerLogger;
import com.ForgeEssentials.playerLogger.network.PacketRollback;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;
import com.ForgeEssentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

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
		ArrayList<String> userlist = new ArrayList<String>();
		userlist.addAll(Arrays.asList(MinecraftServer.getServer().getConfigurationManager().getAvailablePlayerDat()));
		/*
		 * Cmd info
		 */
		if (args.length == 0)
		{
			ChatUtils.sendMessage(sender, "--- Rollback usage ---");
			ChatUtils.sendMessage(sender, "All actions must be confirmed with '/rb ok'.");
			ChatUtils.sendMessage(sender, "All actions can be canceld with '/rb abort'.");
			ChatUtils.sendMessage(sender, "'/rb clear <username>' => Removes a players data.");
			ChatUtils.sendMessage(sender, "'/rb undo <username>' => Undo a rollback. You can specify time and radius");
			ChatUtils.sendMessage(sender, "'/rb <undo|rollback> <username>' => Rolls back a players. All the way!");
			ChatUtils.sendMessage(sender, "'/rb <undo|rollback> <username> <rad>' => Format like this: 10r");
			ChatUtils.sendMessage(sender, "'/rb <undo|rollback> <username> <time>' => Format time like this: 10d = 10 days, 10h = 10 hours.");
			ChatUtils.sendMessage(sender, "A combo of the above is possible too.");
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
				que.remove(sender);
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

				PacketDispatcher.sendPacketToPlayer(new PacketRollback(((EntityPlayer)sender).dimension, null).getPayload(), (Player) sender);

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
		if (args.length > 2)
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
		    parse(sender, args, true, false);
		}
		else if (args[0].equalsIgnoreCase("rollback") || args[0].equalsIgnoreCase("rb"))
		{
		    parse(sender, args, false, false);
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
		else if (args[0].equalsIgnoreCase("undo"))
        {
            parse(sender, args, true, true);
        }
        else if (args[0].equalsIgnoreCase("rollback") || args[0].equalsIgnoreCase("rb"))
        {
            parse(sender, args, false, true);
        }
	}

	public void parse(ICommandSender sender, String[] args, boolean undo, boolean execute)
	{
		int time = 0;
		WorldPoint point = (sender instanceof EntityPlayer) ? new WorldPoint((EntityPlayer) sender) : null;
		int rad = 0;

		for (int i = 1; i < args.length; i++)
		{
			String arg = args[i];
			if (arg.endsWith("d"))
			{
				time = 24 * parseInt(sender, arg.replaceAll("d", ""));
			}
			else if (arg.endsWith("h"))
			{
				time = parseInt(sender, arg.replaceAll("h", ""));
			}
			else if (arg.endsWith("r"))
			{
				rad = parseIntWithMin(sender, arg.replaceAll("r", ""), 0);
			}
		}

		if (execute)
		{
		    try
	        {
	            TaskRegistry.registerTask(new TickTaskRollback(sender, undo, ModulePlayerLogger.getBlockChangesWithinParameters(args[1], undo, time, point, rad)));
	        }
	        catch (SQLException e)
	        {
	            OutputHandler.chatError(sender, "Error. " + e.getLocalizedMessage());
	            e.printStackTrace();
	        }
		}
		else
		{
		    StringBuilder sb = new StringBuilder();
		    for (String arg : args)
		        sb.append(arg + " ");

		    que.put(sender, sb.toString().trim());
		    if (sender instanceof EntityPlayer)
		    {
		        PacketDispatcher.sendPacketToPlayer(new PacketRollback(((EntityPlayer)sender).dimension, ModulePlayerLogger.getBlockChangesWithinParameters(args[1], undo, time, point, rad)).getPayload(), (Player) sender);
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
