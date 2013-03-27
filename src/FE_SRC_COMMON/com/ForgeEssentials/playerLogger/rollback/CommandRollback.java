package com.ForgeEssentials.playerLogger.rollback;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

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
		if (args.length == 0)
		{
			sender.sendChatToPlayer("--- Rollback usage ---");
			sender.sendChatToPlayer("'/rollback <username> clear' => Removes a players data.");
			sender.sendChatToPlayer("'/rollback <username> undo' => Undo a rollback. You can specify time and radius");
			sender.sendChatToPlayer("'/rollback <username>' => Rolls back a players. All the way!");
			sender.sendChatToPlayer("'/rollback <username> [undo] <rad>' => Format like this: 10r");
			sender.sendChatToPlayer("'/rollback <username> [undo] <time>' => Format time like this: 10d = 10 days, 10h = 10 hours.");
			sender.sendChatToPlayer("'/rollback <username> [undo] <time> <rad>' => Combo of the above.");
		}
		else if (args.length > 1 && args[1].equalsIgnoreCase("clear"))
		{
			try
			{
				Connection connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
				Statement st = connection.createStatement();
				st.execute("DELETE FROM `blockchange` WHERE `player` LIKE '" + args[0] + "'");
				OutputHandler.chatConfirmation(sender, "Done.");
				st.close();
				connection.close();
			}
			catch (Exception e)
			{
				OutputHandler.chatError(sender, "Error. " + e.getLocalizedMessage());
				e.printStackTrace();
			}
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
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getAvailablePlayerDat());
		else if (args.length == 2)
			return getListOfStringsMatchingLastWord(args, "clear", "undo");
		else
			return null;
	}
}
