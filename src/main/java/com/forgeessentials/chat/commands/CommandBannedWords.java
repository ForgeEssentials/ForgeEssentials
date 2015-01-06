package com.forgeessentials.chat.commands;

import com.forgeessentials.chat.ChatFormatter;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.google.common.base.Preconditions;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandBannedWords extends ForgeEssentialsCommandBase
{
    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0 || args.length == 1 && args[0].equals("list"))
        {
            OutputHandler.chatNotification(sender, "List of banned words");
            String out = "";
            boolean firstEntry = true;
            for (String banned : ChatFormatter.bannedWords)
            {
                out = out + (firstEntry ? banned : ", " + banned);
                firstEntry = false;
            }
            OutputHandler.chatNotification(sender, out);
        }
        if (args.length > 2)
        {
            switch (args[0])
            {
            case "add":
                try
                {
                    ChatFormatter.bannedWords.add(Preconditions.checkNotNull(args[1]));
                    ForgeEssentials.getConfigManager().save(ModuleChat.CONFIG_CATEGORY);
                    OutputHandler.chatNotification(sender, String.format("Added word %s to banned list", args[1]));
                }
                catch (NullPointerException e)
                {
                    OutputHandler.chatError(sender, "You need to specify a word to ban!");
                }
                break;
            case "remove":
                try
                {
                    ChatFormatter.bannedWords.remove(Preconditions.checkNotNull(args[1]));
                    ForgeEssentials.getConfigManager().save(ModuleChat.CONFIG_CATEGORY);
                    OutputHandler.chatNotification(sender, String.format("Removed word %s from banned list", args[1]));
                }
                catch (NullPointerException e)
                {
                    OutputHandler.chatError(sender, "You need to specify a word to unban!");
                }
                break;
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
        return "fe.chat.bannedwords.admin";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandName()
    {
        return "bannedwords";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/bannedwords [list|add|remove] <word> List, add or remove banned words.";
    }
}
