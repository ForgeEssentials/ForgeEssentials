package com.forgeessentials.protection.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.CommandParserArgs;

public class CommandProtectionDebug extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "protectdebug";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/protectdebug: Toggles protection-module debug-mode";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.protection.cmd.protectdebug";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isTabCompletion)
            return;

        EntityPlayerMP player = arguments.senderPlayer;
        if (player == null)
            throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);

        if (ModuleProtection.isDebugMode(player) && arguments.isEmpty())
        {
            ModuleProtection.setDebugMode(player, null);
            arguments.confirm("Disabled protection debug-mode");
        }
        else
        {
            String cmd = arguments.toString();
            if (cmd.isEmpty())
                cmd = "global deny";
            cmd = "/p " + cmd + " ";

            ModuleProtection.setDebugMode(player, cmd);
            if (!ModuleProtection.isDebugMode(player))
                arguments.confirm("Enabled protection debug-mode");
            arguments.notify("Command: " + cmd + "<perm>");
        }
    }

}
