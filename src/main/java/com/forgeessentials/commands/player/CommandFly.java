package com.forgeessentials.commands.player;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandFly extends ForgeEssentialsCommandBuilder
{
    public CommandFly(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "fly";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".fly";
    }

    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("toggle", BoolArgumentType.bool())
                        .executes(CommandContext -> execute(CommandContext, null)
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        boolean toggle = BoolArgumentType.getBool(ctx, "toggle");
        ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
        
        player.abilities.mayfly = toggle;
        
        if (!player.isOnGround())
            player.abilities.flying = player.abilities.mayfly;
        if (!player.abilities.mayfly)
            WorldUtil.placeInWorld(player);
        player.onUpdateAbilities();
        ChatOutputHandler.chatNotification(player, "Flying " + (player.abilities.mayfly ? "enabled" : "disabled"));
        return Command.SINGLE_SUCCESS;
    }
}
