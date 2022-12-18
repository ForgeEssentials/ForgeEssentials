package com.forgeessentials.protection.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.CommandParserArgs;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandProtectionDebug extends BaseCommand
{

    public CommandProtectionDebug(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "protectdebug";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.protection.cmd.protectdebug";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {

        ServerPlayerEntity player = arguments.senderPlayer;
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
