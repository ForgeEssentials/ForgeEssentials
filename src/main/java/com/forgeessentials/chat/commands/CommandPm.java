package com.forgeessentials.chat.commands;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandPm extends ForgeEssentialsCommandBase implements IPlayerTracker {
    private static Map<String, String> persistentMessage;
    private List<String> aliasList;

    public CommandPm()
    {
        super();
        persistentMessage = new HashMap<String, String>();
        aliasList = new LinkedList<String>();
        aliasList.add("persistentmessage");
        GameRegistry.registerPlayerTracker(this);
    }

    @Override
    public String getCommandName()
    {
        return "pm";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return aliasList;
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 0)
        {
            if (persistentMessage.containsKey(sender.getCommandSenderName()))
            {
                persistentMessage.remove(sender.getCommandSenderName());
                OutputHandler.chatConfirmation(sender, "Persistent message has been disabled.");
            }
            else
            {
                OutputHandler.chatWarning(sender, "Persistent message is already disabled.");
            }
            return;
        }
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                OutputHandler.chatConfirmation(sender, "Use /pm <player> to engage persistent message. /pm to return to normal chat.");
            }
            else
            {
                EntityPlayerMP target = FunctionHelper.getPlayerForName(sender, args[0]);
                if (target == null)
                {
                    OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                    return;
                }
                if (persistentMessage.containsKey(sender.getCommandSenderName()))
                {
                    persistentMessage.remove(sender.getCommandSenderName());
                }
                persistentMessage.put(sender.getCommandSenderName(), target.getCommandSenderName());

                OutputHandler.chatConfirmation(sender, String.format("Persistent message to %s enabled.", target.getCommandSenderName()));
            }
            return;
        }
        if (args.length > 1)
        {
            String[] args2 = new String[args.length - 1];
            for (int i = 1; i < args.length; i++)
            {
                args2[i - 1] = args[i];
            }
            processChat(sender, args2);
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            if (persistentMessage.containsKey(sender.getCommandSenderName()))
            {
                persistentMessage.remove(sender.getCommandSenderName());
                OutputHandler.chatConfirmation(sender, "Persistent message has been disabled.");
            }
            else
            {
                OutputHandler.chatWarning(sender, "Persistent message is already disabled.");
            }
            return;
        }
        if (args.length > 0)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                OutputHandler.chatConfirmation(sender, "Use /pm <player> to engage persistent message. /pm to return to normal chat.");
            }
            else
            {
                EntityPlayer target = FunctionHelper.getPlayerForName(sender, args[0]);
                if (target == null)
                {
                    OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                    return;
                }
                if (persistentMessage.containsKey(sender.getCommandSenderName()))
                {
                    persistentMessage.remove(sender.getCommandSenderName());
                }
                persistentMessage.put(sender.getCommandSenderName(), target.getCommandSenderName());
                OutputHandler.chatConfirmation(sender, String.format("Persistent message to %s enabled.", target.getCommandSenderName()));
            }
            return;
        }
        if (args.length > 1)
        {
            EntityPlayer receiver = FunctionHelper.getPlayerForName(sender, args[0]);
            if (receiver == null)
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                return;
            }
            else
            {
                CommandMsg.clearReply(receiver.getCommandSenderName());
                CommandMsg.addReply(receiver.getCommandSenderName(), "server");
                if (persistentMessage.containsKey("server"))
                {
                    persistentMessage.remove("server");
                }
                persistentMessage.put(sender.getCommandSenderName(), receiver.getCommandSenderName());
                OutputHandler.chatConfirmation(sender, "Persistent message to " + receiver.getCommandSenderName() + " enabled.");
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
                ChatUtils.sendMessage(sender, senderMessage);
                ChatUtils.sendMessage(receiver, receiverMessage);
            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        return APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(player, getCommandPerm()));
    }

    @Override
    public String getCommandPerm()
    {
        return "fe.chat." + getCommandName();
    }

    public static boolean isMessagePersistent(String username)
    {
        return persistentMessage.containsKey(username);
    }

    public static void processChat(ICommandSender sender, String[] args)
    {
        if (sender instanceof EntityPlayer)
        {
            String target = persistentMessage.get(sender.getCommandSenderName());
            if (target.equalsIgnoreCase("server") || target.equalsIgnoreCase("console"))
            {
                CommandMsg.clearReply("server");
                CommandMsg.addReply("server", sender.getCommandSenderName());
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
                ChatUtils.sendMessage(MinecraftServer.getServer(), receiverMessage);
                ChatUtils.sendMessage(sender, senderMessage);
            }
            else
            {
                EntityPlayerMP receiver = FunctionHelper.getPlayerForName(sender, args[0]);
                if (receiver == null)
                {
                    OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                    return;
                }
                CommandMsg.clearReply(receiver.getCommandSenderName());
                CommandMsg.addReply(receiver.getCommandSenderName(), sender.getCommandSenderName());
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
                ChatUtils.sendMessage(sender, senderMessage);
                ChatUtils.sendMessage(receiver, receiverMessage);
            }
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }

    @Override
    public void onPlayerLogin(EntityPlayer player)
    {
    }

    @Override
    public void onPlayerLogout(EntityPlayer player)
    {
        persistentMessage.remove(player.username);
    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player)
    {
    }

    @Override
    public void onPlayerRespawn(EntityPlayer player)
    {
    }

    @Override
    public int compareTo(Object o)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        // TODO Auto-generated method stub
        return "/pm <player> Enable persistent message for a player. Use /pm to turn off.";
    }

    @Override
    public RegGroup getReggroup()
    {
        // TODO Auto-generated method stub
        return RegGroup.MEMBERS;
    }
}