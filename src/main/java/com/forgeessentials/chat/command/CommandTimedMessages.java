package com.forgeessentials.chat.command;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.TimedMessageHandler;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandTimedMessages extends ForgeEssentialsCommandBase
{
    @Override
    public String getCommandName()
    {
        return "timedmessage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            ChatOutputHandler.chatConfirmation(sender, "Possible options: select, broadcast, add, del.");
            return;
        }

        if (args[0].equalsIgnoreCase("select"))
        {
            try
            {
                int id = parseIntBounded(sender, args[1], 0, TimedMessageHandler.getInstance().messages.size());
                TimedMessageHandler.getInstance().currentMessageIdx = id;
                ChatOutputHandler
                        .chatConfirmation(sender, "You have selected \"" + TimedMessageHandler.getInstance().messages.get(id) + "\" as the next message.");
                return;
            }
            catch (Exception e)
            {
                throw new TranslatedCommandException("You have to select a message to broadcast next. Options: %s",
                        TimedMessageHandler.getInstance().messages.size());
            }
        }

        if (args[0].equalsIgnoreCase("broadcast"))
        {
            try
            {
                int idx = parseIntBounded(sender, args[1], 0, TimedMessageHandler.getInstance().messages.size());
                TimedMessageHandler.getInstance().send(idx);
                return;
            }
            catch (Exception e)
            {
                throw new TranslatedCommandException("You have to select a message to broadcast. Options: %s",
                        TimedMessageHandler.getInstance().messages.size());
            }
        }

        if (args[0].equalsIgnoreCase("add"))
        {
            try
            {
                String msg = StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
                ChatOutputHandler.chatConfirmation(sender, msg);
                TimedMessageHandler.getInstance().messages.add(msg);
                ForgeEssentials.getConfigManager().save(ModuleChat.CONFIG_CATEGORY);
                return;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new TranslatedCommandException("Dafuq?");
            }
        }

        if (args[0].equalsIgnoreCase("del"))
        {
            try
            {
                int id = parseIntBounded(sender, args[1], 0, TimedMessageHandler.getInstance().messages.size());
                ChatOutputHandler.chatConfirmation(sender, "Message \"" + TimedMessageHandler.getInstance().messages.get(id) + "\" removed.");
                TimedMessageHandler.getInstance().messages.remove(id);
                return;
            }
            catch (Exception e)
            {
                throw new TranslatedCommandException("You have to select a message to remove. Options: %s", TimedMessageHandler.getInstance().messages.size());
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
    public List<String> addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "select", "broadcast", "add", "del");
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/TimedMessageHandler [select|broadcast|add|del] Select, broadcast, add or remove messages";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {

        return PermissionLevel.OP;
    }
}
