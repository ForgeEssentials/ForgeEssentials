package com.ForgeEssentials.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import cpw.mods.fml.common.FMLLog;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public final class OutputHandler
{
	public static Logger	felog = getLogger();
	
	public static boolean debugmode;
	
	private static Logger getLogger(){
		Logger log = Logger.getLogger("ForgeEssentials");
        log.setParent(FMLLog.getLogger());
        return log;
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
		if (debugmode)
		{
			System.out.println(" {DEBUG} >>>> " + msg);
		}
	}

}
