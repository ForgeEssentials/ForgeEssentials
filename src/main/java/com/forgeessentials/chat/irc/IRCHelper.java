package com.forgeessentials.chat.irc;

import java.io.IOException;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.commands.ircCommands;
import com.forgeessentials.util.OutputHandler;

public class IRCHelper extends ListenerAdapter implements Listener {

    public static int port;
    public static String server, name, channel, password, serverPass;
    public static boolean suppressEvents;
    public static ircCommands ircCmds;
    private static PircBotX bot;

    public static void connectToServer()
    {
        OutputHandler.felog.info("Initializing IRC connection");
        bot = new PircBotX();
        bot.setName(name);
        bot.getListenerManager().addListener(new IRCHelper());
        bot.setLogin(name);
        bot.setVerbose(false);
        bot.setAutoNickChange(true);
        bot.setCapEnabled(true);
        try
        {
            OutputHandler.felog.info("Attempting to join IRC server: " + server + " on port: " + port);
            if (serverPass == "")
            {
                bot.connect(server, port);
            }
            else
            {
                bot.connect(server, port, serverPass);
            }
            bot.identify(password);
            OutputHandler.felog.info("Successfully joined IRC server!");
            OutputHandler.felog.info("Attempting to join " + server + " channel: " + channel);
            bot.joinChannel(channel);
            OutputHandler.felog.info("Successfully joined IRC Channel!");

        }
        catch (NickAlreadyInUseException e)
        {
            OutputHandler.felog.warning("Could not connect to IRC server = someone is already using the name you have assigned.");
        }
        catch (IOException e1)
        {
            OutputHandler.felog.warning("Could not connect to IRC server.");
        }
        catch (IrcException e2)
        {
            OutputHandler.felog.warning("Could not connect to IRC server.");
        }

    }

    public static String getBotName()
    {
        return bot.getName();
    }

    public static void postIRC(String message)
    {
        if (ModuleChat.connectToIRC)
        {
            bot.sendMessage(channel, message);
        }
    }

    // For /msg
    public static void privateMessage(String from, String to, String message)
    {
        if (ModuleChat.connectToIRC)
        {
            bot.sendMessage(bot.getUser(to), "(IRC)[" + from + " -> me] " + message);
        }
    }

    //	In case something else wants to send something
    public static void privateMessage(String to, String message)
    {
        if (ModuleChat.connectToIRC)
        {
            bot.sendMessage(bot.getUser(to), message);
        }
    }

    private static void postMinecraft(String message)
    {
        OutputHandler.sendMessage(MinecraftServer.getServer().getConfigurationManager(), message);
    }

    public static void shutdown()
    {
        if (bot != null)
        {
            bot.disconnect();
        }
    }

    public static void reconnect(ICommandSender sender)
    {
        try
        {
            bot.reconnect();
        }
        catch (NickAlreadyInUseException e)
        {
            OutputHandler.sendMessage(sender, "Could not reconnect to the IRC server - the assigned nick is already in use. Try again in a few minutes.");
        }
        catch (IOException e)
        {
            OutputHandler.sendMessage(sender, "Could not reconnect to the IRC server - something went wrong.");
        }
        catch (IrcException e)
        {
            OutputHandler.sendMessage(sender, "Could not reconnect to the IRC server - something went wrong, or you are already connected to the server.");
        }
    }

    // IRC events
    @Override
    public void onPrivateMessage(PrivateMessageEvent e)
    {
        // Good
        String raw = e.getMessage().trim();

        // Just in case
        // Remove excess :
        while (raw.startsWith(":"))
        {
            raw.replace(":", "");
        }

        // Check to see if it is a command
        if (raw.startsWith("%"))
        {
            ircCommands.executeCommand(raw, e.getUser());
        }

        else
        {
            privateMessage(e.getUser().getNick(), "Hello... use %help for commands.");
        }
    }

    @Override
    public void onMessage(MessageEvent e)
    {
        if (!e.getUser().getNick().equalsIgnoreCase(name))
        {
            // Check to see if it is a command
            if (e.getMessage().trim().startsWith("%"))
            {
                ircCommands.executeCommand(e.getMessage().trim(), e.getUser());
            }

            else
            {
                String send = "(IRC)[" + e.getChannel().getName() + "] <" + e.getUser().getNick() + "> " + e.getMessage().trim();
                postMinecraft(send);
            }
        }
    }

    @Override
    public void onQuit(QuitEvent e)
    {
        if (!suppressEvents)
        {
            if (!e.getUser().getNick().equalsIgnoreCase(channel))
            {
                postMinecraft(EnumChatFormatting.YELLOW + e.getUser().getNick() + " left the channel");
            }
        }
    }

    @Override
    public void onKick(KickEvent e)
    {
        if (!suppressEvents)
        {
            if (!e.getRecipient().getNick().equalsIgnoreCase(channel))
            {
                postMinecraft(EnumChatFormatting.YELLOW + e.getRecipient().getNick() + " was kicked from " + e.getChannel().getName() + " by " + e.getSource()
                        .getNick() + " with reason " + e.getReason());
            }
        }
        OutputHandler.felog.warning(
                "The IRC bot was kicked from " + e.getChannel().getName() + " by " + e.getSource().getNick() + " with reason " + e.getReason()
                        + " , please attempt to reconnect.");

    }

    @Override
    public void onNickChange(NickChangeEvent e)
    {
        if (!suppressEvents)
        {
            postMinecraft(EnumChatFormatting.YELLOW + e.getOldNick() + " changed nick to " + e.getNewNick());
        }
    }

}
