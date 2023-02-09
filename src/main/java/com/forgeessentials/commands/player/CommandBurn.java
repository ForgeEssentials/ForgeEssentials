package com.forgeessentials.commands.player;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.PermissionDeniedException;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandBurn extends ForgeEssentialsCommandBuilder
{

    public CommandBurn(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "burn";
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

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".burn";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("me")
                        .then(Commands.argument("time", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                .executes(CommandContext -> execute(CommandContext, "meT")
                                        )
                                )
                        .executes(CommandContext -> execute(CommandContext, "me")
                                )
                        )
                .then(Commands.literal("others")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("time", IntegerArgumentType.integer(0, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "othersT")
                                                )
                                        )
                                .executes(CommandContext -> execute(CommandContext, "others")
                                        )
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "me")
        {
            getServerPlayer(ctx.getSource()).setSecondsOnFire(15);
            ChatOutputHandler.chatError(ctx.getSource(), "Ouch! Hot!");
        }
        if (params.toString() == "meT")
        {
            getServerPlayer(ctx.getSource()).setSecondsOnFire(IntegerArgumentType.getInteger(ctx, "time"));
            ChatOutputHandler.chatError(ctx.getSource(), "Ouch! Hot!");
        }
        if (params.toString() == "others")
        {
            if (PermissionAPI.hasPermission(getServerPlayer(ctx.getSource()), getPermissionNode() + ".others"))
            {
                ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
                if (player != null)
                {
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "You should feel bad about doing that.");
                    player.setSecondsOnFire(15);
                }
                else
                    throw new TranslatedCommandException("Player does not exist, or is not online.");
            }
            else
                throw new PermissionDeniedException();
        }
        if (params.toString() == "othersT")
        {
            if (PermissionAPI.hasPermission(getServerPlayer(ctx.getSource()), getPermissionNode() + ".others"))
            {
                ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
                if (player != null)
                {
                    player.setSecondsOnFire(IntegerArgumentType.getInteger(ctx, "time"));
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "You should feel bad about doing that.");
                }
                else
                    throw new TranslatedCommandException("Player does not exist, or is not online.");
            }
            else
                throw new PermissionDeniedException();
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        int time = 15;
        if (params.toString()=="othersT")
        {
            time = IntegerArgumentType.getInteger(ctx, "time");
        }

        if (params.toString() =="me")
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Do you really want the server console to burn?.");
            return Command.SINGLE_SUCCESS;
        }

        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        if (player != null)
        {
            player.setSecondsOnFire(time);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "You should feel bad about doing that.");
        }
        else
            throw new TranslatedCommandException("Player does not exist, or is not online.");
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", DefaultPermissionLevel.OP, "Apply burn effect on others");
    }
}
