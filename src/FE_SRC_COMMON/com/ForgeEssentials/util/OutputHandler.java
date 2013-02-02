package com.ForgeEssentials.util;

import java.util.logging.Logger;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public final class OutputHandler
{

	public static boolean	verbose;

	public static Logger	felog;

	/**
	 * outputs a message in red text to the chat box of the given player.
	 * 
	 * @param msg the message to be chatted
	 * @param player player to chat to.
	 */
	public static void chatError(ICommandSender sender, String msg)
	{
		sender.sendChatToPlayer(FEChatFormatCodes.DARKRED + FunctionHelper.formatColors(msg));
	}

	/**
	 * outputs a message in bright green to the chat box of the given player.
	 * 
	 * @param msg the message to be chatted
	 * @param player player to chat to.
	 */
	public static void chatConfirmation(ICommandSender sender, String msg)
	{
		sender.sendChatToPlayer(FEChatFormatCodes.GREEN + FunctionHelper.formatColors(msg));
	}

	/**
	 * outputs a message in yellow to the chat box of the given player.
	 * 
	 * @param msg the message to be chatted
	 * @param player player to chat to.
	 */
	public static void chatWarning(ICommandSender sender, String msg)
	{
		sender.sendChatToPlayer(FEChatFormatCodes.YELLOW + FunctionHelper.formatColors(msg));
	}
	
	

	public static void sever(Object msg)
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
	

	/**
	 * outputs a string to the console if the code is in MCP
	 * 
	 * @param msg message to be outputted
	 */
	public static void debug(Object msg)
	{
		if (!ObfuscationReflectionHelper.obfuscation)
		{
			System.out.println(" {DEBUG} >>>> " + msg);
		}
	}

}
