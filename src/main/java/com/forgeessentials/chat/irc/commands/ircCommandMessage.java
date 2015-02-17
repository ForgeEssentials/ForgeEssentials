package com.forgeessentials.chat.irc.commands;

import com.forgeessentials.chat.commands.CommandMsg;
import com.forgeessentials.chat.irc.IRCChatFormatter;
import com.forgeessentials.chat.irc.IRCHelper;
import com.forgeessentials.util.OutputHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;


import org.pircbotx.User;

public class ircCommandMessage extends ircCommand {

    @Override
    public String getCommandInfo()
    {
        return "Privately messages a player from IRC.";
    }

    @Override
    public String getCommandUsage()
    {
        return "%Message <player> [message]";
    }

    @Override
    public String[] getAliases()
    {
        return new String[] { "msg", "m" };
    }

    @Override
    public void execute(String[] args, User user)
    {
        try
        {
        	
            if (args.length < 1)
            {
                IRCHelper.privateMessage(user, "Unable to send message: No player.");
                IRCHelper.privateMessage(user, "Did you forget the :?");
                IRCHelper.privateMessage(user, "Sytax: /privmsg " + IRCHelper.getBotName() + " :%msg playername message");
                IRCHelper.privateMessage(user, "Or to reply to a previous private message.");
                IRCHelper.privateMessage(user, "Sytax: /privmsg " + IRCHelper.getBotName() + " :%r message");
                return;
            }

            String playername = args[0].toLowerCase();
            String message = "";

            for (int i = 1; i < args.length; i++)
            {
                message += " " + args[i];
            }

            EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(playername);

            if (player == null)
            {
                IRCHelper.privateMessage(user, "Unable to send message: Player not found.");
                return;
            }

            String send = IRCChatFormatter.formatIRCHeader(IRCChatFormatter.ircPrivateHeader, IRCHelper.channel, user.getNick() + " -> me") + " " + message;
           
            OutputHandler.sendMessage(player, send);
            IRCHelper.privateMessage(user.getNick(),player.getCommandSenderName(),message);

            // Add in /r stuff
            CommandMsg.clearReply(user.getNick());
            CommandMsg.clearReply(player.getCommandSenderName());
            CommandMsg.addReply("irc" + user.getNick().toLowerCase(), player.getCommandSenderName());
            CommandMsg.addReply(player.getCommandSenderName(), "irc" + user.getNick().toLowerCase());
        }
        catch (Exception ex)
        {
            IRCHelper.privateMessage(user, "Unable to send message: Something went really wrong.");
            return;
        }
    }

}
