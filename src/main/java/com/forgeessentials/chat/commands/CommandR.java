package com.forgeessentials.chat.commands;

import com.forgeessentials.chat.irc.IRCHelper;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.List;

public class CommandR extends ForgeEssentialsCommandBase {
    public CommandR()
    {
        super();
    }

    @Override
    public String getCommandName()
    {
        return "r";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 0)
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: " + "/r <message>");
            return;
        }
        if (args.length > 0)
        {
            String target = CommandMsg.getPlayerReply(sender.getCommandSenderName());
            if (target == null)
            {
                OutputHandler.chatError(sender, "You have no previous recorded message recipient.");
                return;
            }
            if (target.equalsIgnoreCase("server"))
            {
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
            else if (target.toLowerCase().startsWith("irc"))
            {
                target = target.substring(3);
                String senderMessage = EnumChatFormatting.GOLD + "(IRC)[me -> " + target + "] " + EnumChatFormatting.GRAY;
                String receiverMessage = new String();
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
                try
                {
                    IRCHelper.privateMessage(sender.getCommandSenderName(), target, receiverMessage);
                    OutputHandler.sendMessage(sender, senderMessage);
                }
                catch (Exception e)
                {
                    OutputHandler.chatError(sender, "Unable to send message to: " + target);
                }
            }
            else
            {
                EntityPlayerMP receiver = MinecraftServer.getServer().getConfigurationManager().func_152612_a(target);
                if (receiver == null)
                {
                    OutputHandler.chatError(sender, target + " is not a valid username");
                    return;
                }
                String senderMessage =
                        EnumChatFormatting.GOLD + "[ me -> " + EnumChatFormatting.GRAY + receiver.getCommandSenderName() + EnumChatFormatting.GOLD + "] "
                                + EnumChatFormatting.GRAY;
                String receiverMessage =
                        EnumChatFormatting.GOLD + "[" + EnumChatFormatting.GRAY + sender.getCommandSenderName() + EnumChatFormatting.GOLD + " -> me ] "
                                + EnumChatFormatting.GRAY;
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
                OutputHandler.sendMessage(sender, senderMessage);
                OutputHandler.sendMessage(receiver, receiverMessage);
            }
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: /msg <player> <message>");
            return;
        }
        if (args.length > 0)
        {
            String target = CommandMsg.getPlayerReply("server");
            if (target == null)
            {
                OutputHandler.chatError(sender, "You have no previous recorded message recipient.");
                return;
            }
            EntityPlayer receiver = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (receiver == null)
            {
                OutputHandler.chatError(sender, target + " is not a valid username");
                return;
            }
            else
            {
                String senderMessage = "[ me -> " + receiver.getCommandSenderName() + "] ";
                String receiverMessage = EnumChatFormatting.GOLD + "[" + EnumChatFormatting.DARK_PURPLE + "Server" + EnumChatFormatting.GOLD + " -> me ] "
                        + EnumChatFormatting.GRAY;
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
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/r <message>";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.TRUE;
    }
}
