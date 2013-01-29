package com.ForgeEssentials.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.logging.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public final class OutputHandler
{

	public static boolean	verbose;

	public static Logger	felog	= Logger.getLogger("Forge Essentials");

	/**
	 * outputs a message in red text to the chat box of the given player.
	 * 
	 * @param msg the message to be chatted
	 * @param player player to chat to.
	 */
	public static void chatError(EntityPlayer player, String msg)
	{
		player.addChatMessage(FEChatFormatCodes.DARKRED + FunctionHelper.formatColors(msg));
	}

	/**
	 * outputs a message in bright green to the chat box of the given player.
	 * 
	 * @param msg the message to be chatted
	 * @param player player to chat to.
	 */
	public static void chatConfirmation(EntityPlayer player, String msg)
	{
		player.addChatMessage(FEChatFormatCodes.GREEN + FunctionHelper.formatColors(msg));
	}

	/**
	 * outputs a message in yellow to the chat box of the given player.
	 * 
	 * @param msg the message to be chatted
	 * @param player player to chat to.
	 */
	public static void chatWarning(EntityPlayer player, String msg)
	{
		player.addChatMessage(FEChatFormatCodes.YELLOW + FunctionHelper.formatColors(msg));
	}

	/**
	 * outputs a string to the console if the code is in MCP
	 * 
	 * @param msg message to be outputted
	 */
	public static void devdebug(Object msg)
	{
		if (!ObfuscationReflectionHelper.obfuscation)
		{
			System.out.println("DEBUG: >>>> " + msg);
		}
	}

	/**
	 * outputs a string to the console. Messages here are also logged.
	 * 
	 * @param msg message to be outputted
	 */
	public static void SOP(Object msg)
	{
		if (FMLCommonHandler.instance().getSide().isServer())
		{
			MinecraftServer.getServer().sendChatToPlayer("{Forge Essentials} " + msg);
			felog.info("" + msg);
		}
		else
		{
			System.out.println("{Forge Essentials} " + msg);
			felog.info("" + msg);
		}

	}

	/**
	 * only outputs in MCP or if the verbose mode is activated.
	 * @param msg
	 */
	public static void debug(Object msg)
	{
		if (verbose || !ObfuscationReflectionHelper.obfuscation)
		{
			SOP(msg);
		}
	}

	public static void logConfigChange(String category, String prop, String oldVal, String newVal)
	{
		SOP("Config Changed: " + prop + " under " + "category" + " changed from " + oldVal + " to " + newVal);
	}

}
