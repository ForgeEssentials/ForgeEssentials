package com.forgeessentials.chat.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.IRCHelper;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

public class CommandMsg extends ForgeEssentialsCommandBase {
    private static Map<String, String> playerReply;
    private List<String> aliasList;

    public CommandMsg()
    {
        super();
        playerReply = new HashMap<String, String>();
        aliasList = new LinkedList<String>();
        aliasList.add("tell");
        aliasList.add("whisper");
    }

    public static String getPlayerReply(String player)
    {
        return playerReply.get(player);
    }

    public static void clearReply(String player)
    {
        if (playerReply.containsKey(player))
        {
            playerReply.remove(player);
        }
    }

    public static void addReply(String player, String target)
    {
        playerReply.put(player, target);
    }

    @Override
    public String getCommandName()
    {
        return "msg";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return aliasList;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length > 1)
        {
            if (args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("console"))
            {
                clearReply(sender.getCommandSenderName());
                clearReply("server");
                addReply(sender.getCommandSenderName(), "server");
                addReply("server", sender.getCommandSenderName());
                String senderMessage = EnumChatFormatting.GOLD + "[ me -> " + EnumChatFormatting.DARK_PURPLE + "Server" + EnumChatFormatting.GOLD + "] "
                        + EnumChatFormatting.GRAY;
                String receiverMessage = EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_PURPLE + "Server" + EnumChatFormatting.GOLD + " -> me ] ";
                for (int i = 0; i < args.length; i++)
                {
                    receiverMessage += args[i];
                    senderMessage += args[i];
                    if (i != args.length - 1)
                    {
                        receiverMessage += " ";
                        senderMessage += " ";
                    }
                }
                OutputHandler.sendMessage(MinecraftServer.getServer(), receiverMessage);
                OutputHandler.sendMessage(sender, senderMessage);
            }

            // IRC messages.

            if (ModuleChat.connectToIRC && args[0].equalsIgnoreCase("irc")) // To leverage short-circuit operation AKA: skip IRC if it is off.
            {
                clearReply(sender.getCommandSenderName());
                clearReply("irc" + args[1].toLowerCase());
                addReply(sender.getCommandSenderName(), "irc" + args[1].toLowerCase());
                addReply("irc" + args[1].toLowerCase(), sender.getCommandSenderName());
                String senderMessage = EnumChatFormatting.GOLD + "(IRC)[me -> " + args[1] + "] " + EnumChatFormatting.GRAY;
                String receiverMessage = new String();
                for (int i = 2; i < args.length; i++)
                {
                    receiverMessage += args[i];
                    senderMessage += args[i];
                    if (i != args.length - 1)
                    {
                        receiverMessage += " ";
                        senderMessage += " ";
                    }
                }
                try
                {
                    IRCHelper.privateMessage(sender.getCommandSenderName(), args[1], receiverMessage);
                    OutputHandler.sendMessage(sender, senderMessage);
                }
                catch (Exception e)
                {
                    OutputHandler.sendMessage(sender, "Unable to send message to: " + args[1]);
                }
            }

            // Other messages.

            else
            {
                EntityPlayerMP receiver = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (receiver == null)
                {
                    OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                    return;
                }
                clearReply(sender.getCommandSenderName());
                clearReply(receiver.getCommandSenderName());
                addReply(sender.getCommandSenderName(), receiver.getCommandSenderName());
                addReply(receiver.getCommandSenderName(), sender.getCommandSenderName());
                String senderMessage =
                        EnumChatFormatting.GOLD + "[ me -> " + EnumChatFormatting.GRAY + receiver.getCommandSenderName() + EnumChatFormatting.GOLD + "] "
                                + EnumChatFormatting.WHITE;
                String receiverMessage =
                        EnumChatFormatting.GOLD + "[" + EnumChatFormatting.GRAY + sender.getCommandSenderName() + EnumChatFormatting.GOLD + " -> me ] "
                                + EnumChatFormatting.WHITE;
                for (int i = 1; i < args.length; i++)
                {
                    receiverMessage += args[i];
                    senderMessage += args[i];
                    if (i != args.length - 1)
                    {
                        receiverMessage += " ";
                        senderMessage += " ";
                    }
                }
                OutputHandler.sendMessage(sender, senderMessage);
                OutputHandler.sendMessage(receiver, receiverMessage);
            }
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length > 1)
        {
            EntityPlayerMP receiver = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (receiver == null)
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                return;
            }
            else
            {
                clearReply(receiver.getCommandSenderName());
                clearReply("server");
                addReply(receiver.getCommandSenderName(), "server");
                addReply("server", receiver.getCommandSenderName());
                String senderMessage = "[ me -> " + receiver.getCommandSenderName() + "] ";
                String receiverMessage = EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_PURPLE + "Server" + EnumChatFormatting.GOLD + " -> me ] "
                        + EnumChatFormatting.GRAY;
                for (int i = 1; i < args.length; i++)
                {
                    receiverMessage += args[i];
                    senderMessage += args[i];
                    if (i != args.length - 1)
                    {
                        receiverMessage += " ";
                        senderMessage += " ";
                    }
                }
                OutputHandler.sendMessage(sender, senderMessage);
                OutputHandler.sendMessage(receiver, receiverMessage);
            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat." + getCommandName();
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/msg <irc> <player> <message> Send a message to another player, who is either logged on to the server or online in IRC.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.TRUE;
    }
}
