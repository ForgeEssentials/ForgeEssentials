package com.forgeessentials.chat.irc.commands;

import com.forgeessentials.chat.commands.CommandMsg;
import com.forgeessentials.chat.irc.IRCChatFormatter;
import com.forgeessentials.chat.irc.IRCHelper;
import com.forgeessentials.util.OutputHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;


import org.pircbotx.User;

public class ircCommandReply extends ircCommand {
    @Override
    public String getCommandInfo()
    {
        return "Replies to the last message";
    }

    @Override
    public String getCommandUsage()
    {
        return "%reply [message]";
    }

    @Override
    public String[] getAliases()
    {
        return new String[] { "r" };
    }

    @Override
    public void execute(String[] args, User user)
    {
        try
        {
            String playername = CommandMsg.getPlayerReply("irc" + user.getNick().toLowerCase());
            String message = "";

            for (int i = 1; i < args.length; i++)
            {
                message += " " + args[i];
            }

            EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(playername);

            if (player == null)
            {
                IRCHelper.privateMessage(user,"Unable to send message: Player not found.");
                return;
            }

            String send =  IRCChatFormatter.formatIRCPrivateHeader(IRCHelper.channel, "me -> "+user.getNick()) + " " + message;            

            OutputHandler.sendMessage(player, send);
            IRCHelper.privateMessage(player.getCommandSenderName(), user.getNick(), message);
            
        }
        catch (Exception ex)
        {
            IRCHelper.privateMessage(user,"Unable to send message: Something went really wrong.");
            return;
        }
    }

}
