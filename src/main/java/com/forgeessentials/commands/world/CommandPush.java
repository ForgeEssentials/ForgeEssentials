package com.forgeessentials.commands.world;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.StoneButtonBlock;
import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandPush extends ForgeEssentialsCommandBuilder
{

    public CommandPush(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "push";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        CommandSourceStack sender = ctx.getSource();
        BlockPos posI = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        int x = posI.getX();
        int y = posI.getY();
        int z = posI.getZ();
        Level world = null;

        if (GetSource(sender) instanceof BlockEntity)
        {
            world = ((BlockEntity) GetSource(sender)).getLevel();
        }
        else if (GetSource(sender) instanceof ServerPlayer)
        {
            world = ((ServerPlayer) GetSource(sender)).level;
        }
        else if (GetSource(sender) instanceof DedicatedServer)
        {
            world = ((DedicatedServer) GetSource(sender)).overworld();
        }
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(pos);

        if ((state == Blocks.AIR.defaultBlockState()) || !(state.getBlock() instanceof StoneButtonBlock)
                || !(state.getBlock() instanceof WoodButtonBlock) && !(state.getBlock() instanceof LeverBlock))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Button/Lever Not Found");
            return Command.SINGLE_SUCCESS;
        }
        else
        {
            if (state.getBlock() instanceof ButtonBlock)
            {
                ButtonBlock button = (ButtonBlock) state.getBlock();
                button.press(state, world, pos);
            }
            if (state.getBlock() instanceof LeverBlock)
            {
                LeverBlock lever = (LeverBlock) state.getBlock();
                lever.pull(state, world, pos);
            }
            ChatOutputHandler.chatConfirmation(sender, "Button/Lever Pushed");
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer playermp = getServerPlayer(ctx.getSource());
        BlockPos posI = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        int x = posI.getX();
        int y = posI.getY();
        int z = posI.getZ();
        Level world = null;

        world = playermp.level;
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(pos);

        if ((state == Blocks.AIR.defaultBlockState()
                || !(state.getBlock() instanceof ButtonBlock) && !(state.getBlock() instanceof LeverBlock)))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Button/Lever Not Found");
            return Command.SINGLE_SUCCESS;
        }
        else
        {
            if (state.getBlock() instanceof ButtonBlock)
            {
                ButtonBlock button = (ButtonBlock) state.getBlock();
                button.press(state, world, pos);
            }
            if (state.getBlock() instanceof LeverBlock)
            {
                LeverBlock lever = (LeverBlock) state.getBlock();
                lever.pull(state, world, pos);
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Button/Lever Pushed");
        }
        return Command.SINGLE_SUCCESS;
    }
}
