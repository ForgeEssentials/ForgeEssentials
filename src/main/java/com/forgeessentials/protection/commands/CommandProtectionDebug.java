package com.forgeessentials.protection.commands;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandProtectionDebug extends ForgeEssentialsCommandBuilder
{

    public CommandProtectionDebug(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "protectdebug";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
        		.then(Commands.argument("command", StringArgumentType.greedyString())
        				.executes(CommandContext -> execute(CommandContext, "setCmd")))
        		.executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {

        ServerPlayer player = getServerPlayer(ctx.getSource());
        if (player == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_CONSOLE_COMMAND);
            return Command.SINGLE_SUCCESS;
        }

        if (ModuleProtection.isDebugMode(player))
        {
            ModuleProtection.setDebugMode(player, null);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Disabled protection debug-mode");
        }
        else
        {
        	String cmd = "global deny";
        	if(params.equals("setCmd")) {
        		cmd = StringArgumentType.getString(ctx, "command");
        	}
            cmd = "/feperm " + cmd + " ";

            ModuleProtection.setDebugMode(player, cmd);
            if (!ModuleProtection.isDebugMode(player))
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Enabled protection debug-mode");
            ChatOutputHandler.chatNotification(ctx.getSource(), "Command: " + cmd + "<perm>");
        }
        return Command.SINGLE_SUCCESS;
    }
}
