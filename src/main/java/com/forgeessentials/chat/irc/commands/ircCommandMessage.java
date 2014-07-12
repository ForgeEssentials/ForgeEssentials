package com.forgeessentials.chat.irc.commands;

import com.forgeessentials.chat.commands.CommandMsg;
import com.forgeessentials.chat.irc.IRCHelper;
import com.forgeessentials.util.ChatUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
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
                user.sendMessage("Unable to send message: No player.");
                user.sendMessage("Did you forget the :?");
                user.sendMessage("Sytax: /privmsg " + IRCHelper.getBotName() + " :%msg playername message");
                user.sendMessage("Or to reply to a previous private message.");
                user.sendMessage("Sytax: /privmsg " + IRCHelper.getBotName() + " :%r message");
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
                user.sendMessage("Unable to send message: Player not found.");
                return;
            }

            String send = EnumChatFormatting.GOLD + "(IRC)[" + user.getNick() + " -> me] " + EnumChatFormatting.GRAY + message;
            String recipt = "(IRC)[me -> " + player.getCommandSenderName() + "] " + message;

            ChatUtils.sendMessage(player, send);
            IRCHelper.privateMessage(user.getNick(), recipt);

            // Add in /r stuff
            CommandMsg.clearReply(user.getNick());
            CommandMsg.clearReply(player.getCommandSenderName());
            CommandMsg.addReply("irc" + user.getNick().toLowerCase(), player.getCommandSenderName());
            CommandMsg.addReply(player.getCommandSenderName(), "irc" + user.getNick().toLowerCase());
        }
        catch (Exception ex)
        {
            user.sendMessage("Unable to send message: Something went really wrong.");
            return;
        }
    }

}
