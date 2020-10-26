package com.forgeessentials.chat.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
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
    public String getPrimaryAlias()
    {
        return "nickname";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "nick" };
    }

    @Override
    public String getUsage(ICommandSender sender)
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
        APIRegistry.perms.registerPermission(PERM_OTHERS, DefaultPermissionLevel.OP, "Edit other players' nicknames");
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
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
            if (!PermissionAPI.hasPermission(sender, PERM_OTHERS))
                throw new TranslatedCommandException(FEPermissions.MSG_NO_COMMAND_PERM);

            EntityPlayerMP player = getPlayer(server, sender, args[0]);
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
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 2)
        {
            EntityPlayerMP player = getPlayer(server, sender, args[0]);
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
