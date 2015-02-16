package com.forgeessentials.chat.irc;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.commands.ircCommands;
import com.forgeessentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.exception.NickAlreadyInUseException;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;

import java.io.IOException;

public class IRCHelper extends ListenerAdapter implements Listener {

    public static int port;
    public static String server, name, channel, password, serverPass;
    public static boolean suppressEvents, silentMode, twitchMode, debugMode;
    public static ircCommands ircCmds;
    private static PircBotX bot;

    public static void connectToServer()
    {
        OutputHandler.felog.info("Initializing IRC connection");
        bot = new PircBotX();
        bot.setName(name);
        bot.getListenerManager().addListener(new IRCHelper());
        bot.setLogin(name);
        bot.setVerbose(debugMode);
        bot.setAutoNickChange(true);
        
        if ( !twitchMode ) 
        {
        	bot.setCapEnabled(true);
        }
        else
        {
        	bot.setCapEnabled(false);
        	// Prevent pesky messages from jtv because we are sending too fast
        	bot.setMessageDelay(3000); 
        }
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
            OutputHandler.felog.warning("IRC connection failed, the assigned nick is already in use.");
        }
        catch (IOException e1)
        {
            OutputHandler.felog.warning("IRC connection failed, could not reach the server.");
        }
        catch (IrcException e2)
        {
            OutputHandler.felog.warning("IRC connection failed, the server actively refused it. Error is: " + e2.getMessage());
        }

    }

    public static String getBotName()
    {
        return bot.getName();
    }

    public static void postIRC(String message)
    {
        if (ModuleChat.connectToIRC && !silentMode)
        {
            bot.sendMessage(channel, message);
        }
    }

    // For /msg
    public static void privateMessage(String from, String to, String message)
    {
        if (ModuleChat.connectToIRC)
        {
            if ( !twitchMode )
            {
            	bot.sendMessage(bot.getUser(to), "(IRC)[" + from + " -> me] " + message);
            }
            else
            {
            	bot.sendMessage(channel,"[ " + from + " -> " + to + " ] " + message);
            }
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

    // Automatically decide where to send a message.
    // Private if IRC mode
    // Public if twitch mode ( does not support direct messages)
    public static void privateMessage(User user, String message)
    {
    	 if (ModuleChat.connectToIRC)
         {
    		 if ( !twitchMode )
    		 {
    			 user.sendMessage(message);
    		 }
    		 else
    		 {
    			 // ignore messages to jtv
    			 if ( user.getNick() == "jtv" )
    				 return;
    	        	
    			 bot.sendMessage(channel, message);
    		 }
         }    	
    }
    
    private static void postMinecraft(String message)
    {
        OutputHandler.sendMessage(MinecraftServer.getServer().getConfigurationManager(), message);
    }

    public static void shutdown()
    {
        if (bot != null && bot.isConnected())
        {
            bot.disconnect();
        }
    }

    public static void reconnect(ICommandSender sender)
    {
        try
        {
        	if ( bot.isConnected() )
        	{
        		bot.disconnect();
        	}
            bot.reconnect();
        }
        catch (NickAlreadyInUseException e)
        {
            OutputHandler.chatError(sender, "Reconnection failed - the assigned nick is already in use. Try again in a few minutes.");
        }
        catch (IOException e)
        {
            OutputHandler.chatError(sender, "Reconnection failed - could not reach the IRC server.");
        }
        catch (IrcException e)
        {
            OutputHandler.chatError(sender, "Reconnection failed - server actively refused it, or you are already connected to the server. Error is: " + e.getMessage());
        }
    }

    public static void status(ICommandSender sender)
    {
    	OutputHandler.sendMessage(sender,"IRC Connection is " + ( bot.isConnected() ? "online" : "offline") );
    }
    
    public static String getConnectionStatus()
    {
    	   return ( bot.isConnected() ? "online" : "offline"); 	
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
        	// ignore messages from jtv
        	if ( twitchMode && ( e.getUser().getNick() == "jtv" ) )
        		return;
        	
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
                String send = IRCChatFormatter.formatIRCHeader(e.getChannel().getName(), e.getUser().getNick()) + " " + e.getMessage().trim();
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

	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onJoin(org.pircbotx.hooks.events.JoinEvent)
	 */
	@Override
	public void onJoin(JoinEvent e) throws Exception 
	{
		if ( !suppressEvents )
		{
			String send = IRCChatFormatter.formatIRCHeader(e.getChannel().getName(), e.getUser().getNick()) + " joined the channel";
			postMinecraft(send);
		}		
	}

	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onPart(org.pircbotx.hooks.events.PartEvent)
	 */
	@Override
	public void onPart(PartEvent e) throws Exception 
	{
		if ( !suppressEvents )
		{
			String send = IRCChatFormatter.formatIRCHeader(e.getChannel().getName(), e.getUser().getNick()) + " left the channel";
			postMinecraft(send);
		}		
	}

	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onConnect(org.pircbotx.hooks.events.ConnectEvent)
	 */
	@Override
	public void onConnect(ConnectEvent event) throws Exception 
	{
		if ( !suppressEvents )
		{
			String send = IRCChatFormatter.formatIRCHeader(channel, "System") + " Connection established";
			postMinecraft(send);
		}	
	}

	/* (non-Javadoc)
	 * @see org.pircbotx.hooks.ListenerAdapter#onDisconnect(org.pircbotx.hooks.events.DisconnectEvent)
	 */
	@Override
	public void onDisconnect(DisconnectEvent event) throws Exception 
	{
		if ( !suppressEvents )
		{
			String send = IRCChatFormatter.formatIRCHeader(channel, "System") + " Connection lost";
			postMinecraft(send);
		}
	}
    
    

    
}
