package com.forgeessentials.commands.player;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet2Reach;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandReach extends BaseCommand
{

    public CommandReach(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "reach";
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
        return ModuleCommands.PERM + ".reach";
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        float distance = 0;
        if (params.toString() == "blank")
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/reach <distance>: Set block reach distance. Set to 0 to reset.");
            return Command.SINGLE_SUCCESS;
        }

        if (!PlayerInfo.get(((PlayerEntity) ctx.getSource().getEntity()).getUUID()).getHasFEClient())
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You need the FE client addon to use this command");
            return Command.SINGLE_SUCCESS;
        }

        if(params.toString() == "reset") 
        {
            distance = 0;
        }
        else if(params.toString() == "distance")
        {
        distance = (float) DoubleArgumentType.getDouble(ctx, "distance");
        }
        if (distance < 1)
            distance = 5;

        NetworkUtils.sendTo(new Packet2Reach(distance), (ServerPlayerEntity) ctx.getSource().getEntity());
        ((ServerPlayerEntity) ctx.getSource().getEntity()).getAttribute(null).interactionManager.setBlockReachDistance(distance);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Set reach distance to %d", (int) distance));
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("reset")
                        .executes(CommandContext -> execute(CommandContext, "reset")
                                )
                        )
                .then(Commands.literal("distance")
                        .then(Commands.argument("distance", DoubleArgumentType.doubleArg(0, 100))
                                .executes(CommandContext -> execute(CommandContext, "distance")
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "blank")
                        );
    }

}
