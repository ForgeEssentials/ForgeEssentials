package com.forgeessentials.auth;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandVIP extends ForgeEssentialsCommandBuilder
{

    public CommandVIP(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "vip";
    }

    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("add")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "add"))))
                .then(Commands.literal("add").then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext, "remove"))));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        Player arg = EntityArgument.getPlayer(ctx, "player");
        if (params.equals("add"))
        {
            APIRegistry.perms.setPlayerPermission(UserIdent.get(arg), "fe.auth.vip", true);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Player added to vip list");
        }
        else if (params.equals("remove"))
        {
            APIRegistry.perms.setPlayerPermission(UserIdent.get(arg), "fe.auth.vip", false);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Player removed from vip list");
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

}
