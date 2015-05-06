package com.forgeessentials.chat.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.chat.AutoMessage;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class CommandAutoMessage extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "automessage";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("am");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            OutputHandler.chatConfirmation(sender, "Possible options: select, broadcast, add, del.");
            return;
        }

        if (args[0].equalsIgnoreCase("select"))
        {
            try
            {
                int id = parseIntBounded(sender, args[1], 0, AutoMessage.getInstance().messages.size());
                AutoMessage.getInstance().currentMessageIdx = id;
                OutputHandler.chatConfirmation(sender, "You have selected \"" + AutoMessage.getInstance().messages.get(id) + "\" as the next message.");
                return;
            }
            catch (Exception e)
            {
                throw new TranslatedCommandException("You have to select a message to broadcast next. Options: %s", AutoMessage.getInstance().messages.size());
            }
        }

        if (args[0].equalsIgnoreCase("broadcast"))
        {
            try
            {
                int idx = parseIntBounded(sender, args[1], 0, AutoMessage.getInstance().messages.size());
                AutoMessage.getInstance().send(idx);
                return;
            }
            catch (Exception e)
            {
                throw new TranslatedCommandException("You have to select a message to broadcast. Options: %s", AutoMessage.getInstance().messages.size());
            }
        }

        if (args[0].equalsIgnoreCase("add"))
        {
            try
            {
                String msg = "";
                for (String var : FunctionHelper.dropFirstString(args))
                {
                    msg += " " + var;
                }
                OutputHandler.chatConfirmation(sender, msg.substring(1));
                AutoMessage.getInstance().messages.add(msg.substring(1));
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
                int id = parseIntBounded(sender, args[1], 0, AutoMessage.getInstance().messages.size());
                OutputHandler.chatConfirmation(sender, "Message \"" + AutoMessage.getInstance().messages.get(id) + "\" removed.");
                AutoMessage.getInstance().messages.remove(id);
                return;
            }
            catch (Exception e)
            {
                throw new TranslatedCommandException("You have to select a message to remove. Options: %s", AutoMessage.getInstance().messages.size());
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
        return "/automessage [select|broadcast|add|del] Select, broadcast, add or remove messages";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.OP;
    }
}
