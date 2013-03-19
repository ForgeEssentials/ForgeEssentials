package com.ForgeEssentials.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public final class OutputHandler
{

	public static Logger	felog;

	public static void init(Logger logger)
	{
		felog = logger;
	}

	/**
	 * outputs a message in red text to the chat box of the given player.
	 * @param msg
	 * the message to be chatted
	 * @param player
	 * player to chat to.
	 */
	public static void chatError(ICommandSender sender, String msg)
	{
		if (sender instanceof EntityPlayer)
		{
			sender.sendChatToPlayer(FEChatFormatCodes.RED + FunctionHelper.formatColors(msg));
		}
		else
		{
			sender.sendChatToPlayer(FunctionHelper.formatColors(msg));
		}
	}

	/**
	 * outputs a message in bright green to the chat box of the given player.
	 * @param msg
	 * the message to be chatted
	 * @param player
	 * player to chat to.
	 */
	public static void chatConfirmation(ICommandSender sender, String msg)
	{
		if (sender instanceof EntityPlayer)
		{
			sender.sendChatToPlayer(FEChatFormatCodes.GREEN + FunctionHelper.formatColors(msg));
		}
		else
		{
			sender.sendChatToPlayer(FunctionHelper.formatColors(msg));
		}
	}

	/**
	 * outputs a message in yellow to the chat box of the given player.
	 * @param msg
	 * the message to be chatted
	 * @param player
	 * player to chat to.
	 */
	public static void chatWarning(ICommandSender sender, String msg)
	{
		if (sender instanceof EntityPlayer)
		{
			sender.sendChatToPlayer(FEChatFormatCodes.YELLOW + FunctionHelper.formatColors(msg));
		}
		else
		{
			sender.sendChatToPlayer(FunctionHelper.formatColors(msg));
		}
	}

	public static void severe(Object msg)
	{
		felog.severe(msg.toString());
	}

	public static void warning(Object msg)
	{
		felog.warning(msg.toString());
	}

	public static void info(Object msg)
	{
		felog.info(msg.toString());
	}

	public static void fine(Object msg)
	{
		felog.fine(msg.toString());
	}

	public static void finer(Object msg)
	{
		felog.finer(msg.toString());
	}

	public static void finest(Object msg)
	{
		felog.finest(msg.toString());
	}

	public static void severe(String msg)
	{
		felog.severe(msg);
	}

	public static void warning(String msg)
	{
		felog.warning(msg);
	}

	public static void info(String msg)
	{
		felog.info(msg);
	}

	public static void fine(String msg)
	{
		felog.fine(msg);
	}

	public static void finer(String msg)
	{
		felog.finer(msg);
	}

	public static void finest(String msg)
	{
		felog.finest(msg);
	}

	/**
	 * Use this to throw errors that can continue without crashing the server.
	 * @param level
	 * @param message
	 * @param error
	 */
	public static void exception(Level level, String message, Throwable error)
	{
		felog.log(level, message, error);
	}

	/**
	 * outputs a string to the console if the code is in MCP
	 * @param msg
	 * message to be outputted
	 */
	public static void debug(Object msg)
	{
		if (!ObfuscationReflectionHelper.obfuscation)
		{
			System.out.println(" {DEBUG} >>>> " + msg);
		}
	}

}
