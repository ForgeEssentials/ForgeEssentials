package com.forgeessentials.commands.world;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.StoneButtonBlock;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        CommandSource sender = ctx.getSource();
        BlockPos posI = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        int x = posI.getX();
        int y = posI.getY();
        int z = posI.getZ();
        World world = null;

        if (GetSource(sender) instanceof TileEntity)
        {
            world = ((TileEntity) GetSource(sender)).getLevel();
        }
        else if (GetSource(sender) instanceof ServerPlayerEntity)
        {
            world = ((ServerPlayerEntity) GetSource(sender)).level;
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
            if (state.getBlock() instanceof AbstractButtonBlock)
            {
                AbstractButtonBlock button = (AbstractButtonBlock) state.getBlock();
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
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity playermp = getServerPlayer(ctx.getSource());
        BlockPos posI = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
        int x = posI.getX();
        int y = posI.getY();
        int z = posI.getZ();
        World world = null;

        world = playermp.level;
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(pos);

        if ((state == Blocks.AIR.defaultBlockState()
                || !(state.getBlock() instanceof AbstractButtonBlock) && !(state.getBlock() instanceof LeverBlock)))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Button/Lever Not Found");
            return Command.SINGLE_SUCCESS;
        }
        else
        {
            if (state.getBlock() instanceof AbstractButtonBlock)
            {
                AbstractButtonBlock button = (AbstractButtonBlock) state.getBlock();
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
