package com.ForgeEssentials;

import java.util.logging.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.RConConsoleSource;

public final class OutputHandler
{
	// colors
	public static final String BLACK = "\u00a70";
	public static final String DARKBLUE = "\u00a71";
	public static final String DARKGREEN = "\u00a72";
	public static final String DARKAQUA = "\u00a73";
	public static final String DARKRED = "\u00a74";
	public static final String PURPLE = "\u00a75";
	public static final String GOLD = "\u00a76";
	public static final String GREY = "\u00a77";
	public static final String DARKGREY = "\u00a78";
	public static final String INDIGO = "\u00a79";
	public static final String GREEN = "\u00a7a";
	public static final String AQUA = "\u00a7b";
	public static final String RED = "\u00a7c";
	public static final String PINK = "\u00a7d";
	public static final String YELLOW = "\u00a7e";
	public static final String WHITE = "\u00a7f";

	// special format codes
	public static final String RANDOM = "\u00a7k";
	public static final String BOLD = "\u00a7l";
	public static final String STRIKE = "\u00a7m";
	public static final String UNDERLINE = "\u00a7n";
	public static final String ITALICS = "\u00a7o";
	public static final String RESET = "\u00a7r";

	/**
	 * outputs a message in red text to the chat box of the given player.
	 * @param msg the message to be chatted
	 * @param player player to chat to.
	 */
	public static void chatError(EntityPlayer player, String msg)
	{
		player.addChatMessage(RED + msg);
	}

	/**
	 * outputs a message in bright green and bold to the chat box of the given player.
	 * @param msg the message to be chatted
	 * @param player player to chat to.
	 */
	public static void chatConfirmation(EntityPlayer player, String msg)
	{
		player.addChatMessage(GREEN + msg);
	}

	/**
	 * outputs a string to the console if MCP is installed.
	 * @param msg message to be outputted
	 */
	public static void debug(String msg)
	{
		if (Loader.instance().isModLoaded("mcp"))
			System.out.println("DEBUG: >>>> " + msg);
	}

	/**
	 * outputs a string to the console;
	 * @param msg message to be outputted
	 */
	public static void SOP(String msg)
	{

		if (FMLCommonHandler.instance().getSide().isServer())
		{
			RConConsoleSource.consoleBuffer.sendChatToPlayer("{Forge Essentials} " + msg);
		}
		
		FMLCommonHandler.instance().getFMLLogger().log(Level.FINE, ("{Forge Essentials} " + msg));
	}

}
