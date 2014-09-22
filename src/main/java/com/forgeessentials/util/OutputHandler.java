package com.forgeessentials.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class OutputHandler {
	
    public static LogWrapper felog;

    public static boolean debugmode;

    public OutputHandler()
    {
        felog = new LogWrapper(LogManager.getLogger("ForgeEssentials"));
    }

    /**
     * outputs a message in bright green to the chat box of the given player.
     *
     * @param msg    the message to be chatted
     * @param player player to chat to.
     */
    public static void chatColored(ICommandSender sender, String msg, EnumChatFormatting color)
    {
    	ChatComponentText cmsg = new ChatComponentText(FunctionHelper.formatColors(msg));
    	cmsg.getChatStyle().setColor(color);
    	sender.addChatMessage(cmsg);
    }

    /**
     * outputs a message in red text to the chat box of the given player.
     *
     * @param msg    the message to be chatted
     * @param player player to chat to.
     */
    public static void chatError(ICommandSender sender, String msg)
    {
    	chatColored(sender, msg, EnumChatFormatting.RED);
    }

    /**
     * outputs a message in bright green to the chat box of the given player.
     *
     * @param msg    the message to be chatted
     * @param player player to chat to.
     */
    public static void chatConfirmation(ICommandSender sender, String msg)
    {
    	chatColored(sender, msg, EnumChatFormatting.GREEN);
    }

    /**
     * outputs a message in yellow to the chat box of the given player.
     *
     * @param msg    the message to be chatted
     * @param player player to chat to.
     */
    public static void chatWarning(ICommandSender sender, String msg)
    {
    	chatColored(sender, msg, EnumChatFormatting.YELLOW);
    }

    /**
     * Use this to throw errors that can continue without crashing the server.
     *
     * @param level
     * @param message
     * @param error
     */
    public static void exception(java.util.logging.Level level, String message, Throwable error)
    {
        felog.log(Level.toLevel(level.getName()), message, error);
    }

    /**
     * outputs a string to the console if the code is in MCP
     *
     * @param msg message to be outputted
     */
    public static void debug(Object msg)
    {
        if (debugmode)
        {
            System.out.println(" {DEBUG} >>>> " + msg);
        }
    }

    public class LogWrapper
    {
        private Logger wrapped;

        protected LogWrapper(Logger logger)
        {
            wrapped = logger;
        }

        public void finest(String message){wrapped.log(Level.ALL, message);}

        public void finer(String message){wrapped.log(Level.DEBUG, message);}

        public void fine(String message){wrapped.log(Level.INFO, message);}

        public void info(String message){wrapped.log(Level.INFO, message);}

        public void warning(String message){wrapped.log(Level.WARN, message);}

        public void severe(String message){wrapped.log(Level.ERROR, message);}

        public void log(Level level, String message, Throwable error){wrapped.log(level, message, error);}

        public Logger getWrapper(){return wrapped;}
    }

}
