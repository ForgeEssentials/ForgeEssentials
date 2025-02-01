package com.forgeessentials.chat.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandNickname extends ForgeEssentialsCommandBase
{

    public static final String PERM = ModuleChat.PERM + ".nickname";

    public static final String PERM_OTHERS = PERM + ".others";

    @Override
    public String getCommandName()
    {
        return "nickname";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "nick" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/nick <username> [nickname|del> Edit a player's nickname.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_OTHERS, PermissionLevel.OP);
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("del"))
            {
                ModuleChat.setPlayerNickname(sender, null);
                ChatOutputHandler.chatConfirmation(sender, "Nickname removed.");
            }
            else
            {
                ModuleChat.setPlayerNickname(sender, args[0]);
                ChatOutputHandler.chatConfirmation(sender, "Nickname set to " + args[0]);
            }
        }
        else if (args.length == 2)
        {
            if (!PermissionManager.checkPermission(sender, PERM_OTHERS))
                throw new TranslatedCommandException("You don't have permissions for that.");

            EntityPlayerMP player = getPlayer(sender, args[0]);
            if (args[1].equalsIgnoreCase("del"))
            {
                ModuleChat.setPlayerNickname(player, null);
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Removed nickname of %s", args[0]));
            }
            else
            {
                ModuleChat.setPlayerNickname(player, args[1]);
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Set nickname of %s to %s", args[0], args[1]));
            }
        }
        else
        {
            throw new TranslatedCommandException("Improper syntax. Please try this instead: <username> [nickname|del]");
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 2)
        {
            EntityPlayerMP player = getPlayer(sender, args[0]);
            if (args[1].equalsIgnoreCase("del"))
            {
                ModuleChat.setPlayerNickname(player, null);
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Removed nickname of %s", args[0]));
            }
            else
            {
                ModuleChat.setPlayerNickname(player, args[1]);
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Set nickname of %s to %s", args[0], args[1]));
            }
        }
        else
        {
            throw new TranslatedCommandException("Improper syntax. Please try this instead: <username> [nickname|del]");
        }
    }

}
