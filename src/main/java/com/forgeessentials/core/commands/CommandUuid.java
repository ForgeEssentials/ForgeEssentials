package com.forgeessentials.core.commands;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandUuid extends ForgeEssentialsCommandBuilder
{

    public CommandUuid(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "uuid";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.commands.uuid";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return baseBuilder
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext)
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        PlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "UUID= "+ player.getStringUUID());
        return Command.SINGLE_SUCCESS;
    }
}
