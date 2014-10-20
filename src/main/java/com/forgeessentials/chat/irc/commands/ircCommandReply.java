package com.forgeessentials.chat.irc.commands;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import org.pircbotx.User;

import com.forgeessentials.chat.commands.CommandMsg;
import com.forgeessentials.util.OutputHandler;

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
                user.sendMessage("Unable to send message: Player not found.");
                return;
            }

            String send = EnumChatFormatting.GOLD + "(IRC)[" + user.getNick() + " -> me] " + EnumChatFormatting.GRAY + message;
            String recipt = "(IRC)[me -> " + player.getCommandSenderName() + "] " + message;

            OutputHandler.sendMessage(player, send);
            user.sendMessage(recipt);
        }
        catch (Exception ex)
        {
            user.sendMessage("Unable to send message: Something went really wrong.");
            return;
        }
    }

}
