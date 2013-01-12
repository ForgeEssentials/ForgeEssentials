package com.ForgeEssentials.playerLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.TickTaskHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Rollback command. WIP!
 * @author Dries007
 *
 */

public class CommandRollback extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "rollback";
	}
	
	@Override
	public List getCommandAliases()
	{
		return Arrays.asList(new String[] {"rb"});
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
		if(args.length == 0)
		{
			//CMD usage
		}
		else if(args.length == 1)
		{
			try
			{
				TickTaskHandler.addTask(new TickTaskRollback(sender, args[0], false));
				sender.sendChatToPlayer("Starting rollback.");
			}
			catch (Exception e)
			{
				sender.sendChatToPlayer("Error.");
				e.printStackTrace();
			}
		}
		else if(args.length == 2 && args[1].equalsIgnoreCase("undo"))
		{
			try
			{
				TickTaskHandler.addTask(new TickTaskRollback(sender, args[0], true));
				sender.sendChatToPlayer("Starting rollback of rollback.");	
			}
			catch (Exception e)
			{
				sender.sendChatToPlayer("Error.");
				e.printStackTrace();
			}
		}
		else if(args.length == 2 && args[1].equalsIgnoreCase("clear"))
		{
			try
			{
				Connection connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
				Statement st = connection.createStatement();
				st.execute("DELETE FROM `blockchange` WHERE `player` LIKE '" + args[0] + "'");
				sender.sendChatToPlayer("Done.");
				st.close();
				connection.close();
			}
			catch (Exception e)
			{
				sender.sendChatToPlayer("Error.");
				e.printStackTrace();
			}
		}
		else
		{
			sender.sendChatToPlayer("dafuq?");
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
		return "";
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getAvailablePlayerDat());
		}
		else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, "undo", "clear");
		}
		else
		{
			return null;
		}
	}
}
