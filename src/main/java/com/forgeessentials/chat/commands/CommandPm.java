package com.forgeessentials.chat.commands;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandPm extends ForgeEssentialsCommandBase {
    private static Map<String, String> persistentMessage;
    private List<String> aliasList;

    public CommandPm()
    {
        super();
        persistentMessage = new HashMap<String, String>();
        aliasList = new LinkedList<String>();
        aliasList.add("persistentmessage");
        FMLCommonHandler.instance().bus().register(this);
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
                OutputHandler.sendMessage(MinecraftServer.getServer(), receiverMessage);
                OutputHandler.sendMessage(sender, senderMessage);
            }
            else
            {
                EntityPlayerMP receiver = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (receiver == null)
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
                
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
                OutputHandler.sendMessage(sender, senderMessage);
                OutputHandler.sendMessage(receiver, receiverMessage);
            }
        }
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
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
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
                OutputHandler.chatNotification(sender, "Use /pm <player> to engage persistent message. /pm to return to normal chat.");
            }
            else
            {
                EntityPlayerMP target = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (target == null)
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);

                if (persistentMessage.containsKey(sender.getCommandSenderName()))
                    persistentMessage.remove(sender.getCommandSenderName());
                persistentMessage.put(sender.getCommandSenderName(), target.getCommandSenderName());

                OutputHandler.chatConfirmation(sender, Translator.format("Persistent message to %s enabled.", target.getCommandSenderName()));
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
                OutputHandler.chatNotification(sender, "Use /pm <player> to engage persistent message. /pm to return to normal chat.");
            }
            else
            {
                EntityPlayer target = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (target == null)
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);

                if (persistentMessage.containsKey(sender.getCommandSenderName()))
                    persistentMessage.remove(sender.getCommandSenderName());
                persistentMessage.put(sender.getCommandSenderName(), target.getCommandSenderName());
                OutputHandler.chatConfirmation(sender, Translator.format("Persistent message to %s enabled.", target.getCommandSenderName()));
            }
            return;
        }
        if (args.length > 1)
        {
            EntityPlayer receiver = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (receiver == null)
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
            
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
            OutputHandler.sendMessage(sender, senderMessage);
            OutputHandler.sendMessage(receiver, receiverMessage);
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
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

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e)
    {
        persistentMessage.remove(e.player.getPersistentID());
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/pm <player> Enable persistent message for a player. Use /pm to turn off.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.TRUE;
    }
}