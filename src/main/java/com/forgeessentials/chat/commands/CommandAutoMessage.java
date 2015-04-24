package com.forgeessentials.chat.commands;

import com.forgeessentials.chat.AutoMessage;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.Arrays;
import java.util.List;

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
                int id = parseIntBounded(sender, args[1], 0, AutoMessage.msg.size());
                AutoMessage.currentMsgID = id;
                OutputHandler.chatConfirmation(sender, "You have selected \"" + AutoMessage.msg.get(id) + "\" as the next message.");
                return;
            }
            catch (Exception e)
            {
                throw new TranslatedCommandException("You have to select a message to broadcast next. Options: %s", AutoMessage.msg.size());
            }
        }

        if (args[0].equalsIgnoreCase("broadcast"))
        {
            try
            {
                int id = parseIntBounded(sender, args[1], 0, AutoMessage.msg.size());
                OutputHandler.broadcast(new ChatComponentText(AutoMessage.msg.get(id)));
                return;
            }
            catch (Exception e)
            {
                throw new TranslatedCommandException("You have to select a message to broadcast. Options: %s", AutoMessage.msg.size());
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
                AutoMessage.msg.add(msg.substring(1));
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
                int id = parseIntBounded(sender, args[1], 0, AutoMessage.msg.size());
                OutputHandler.chatConfirmation(sender, "Message \"" + AutoMessage.msg.get(id) + "\" removed.");
                AutoMessage.msg.remove(id);
                return;
            }
            catch (Exception e)
            {
                throw new TranslatedCommandException("You have to select a message to remove. Options: %s", AutoMessage.msg.size());
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
