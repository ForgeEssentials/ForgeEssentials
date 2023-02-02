package com.forgeessentials.commands.world;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandPush extends BaseCommand
{

    public CommandPush(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    @Override
    public String getPrimaryAlias()
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
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".push";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(CommandContext -> execute(CommandContext)
                                )
                        );
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
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
            throw new TranslatedCommandException("Button/Lever Not Found");
        }
        else
        {
            if(state.getBlock() instanceof AbstractButtonBlock){
                AbstractButtonBlock button = (AbstractButtonBlock) state.getBlock();
                button.press(state, world, pos);
            }
            if(state.getBlock() instanceof LeverBlock){
                LeverBlock lever = (LeverBlock) state.getBlock();
                lever.pull(state, world, pos);
            }
            ChatOutputHandler.chatConfirmation(sender, "Button/Lever Pushed");
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
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
        
        if ((state == Blocks.AIR.defaultBlockState() || !(state.getBlock() instanceof AbstractButtonBlock) && !(state.getBlock() instanceof LeverBlock)))
        {
            throw new TranslatedCommandException("Button/Lever Not Found");
        }
        else
        {
            if(state.getBlock() instanceof AbstractButtonBlock){
                AbstractButtonBlock button = (AbstractButtonBlock) state.getBlock();
                button.press(state, world, pos);
            }
            if(state.getBlock() instanceof LeverBlock){
                LeverBlock lever = (LeverBlock) state.getBlock();
                lever.pull(state, world, pos);
            }
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Button/Lever Pushed");
        }
        return Command.SINGLE_SUCCESS;
    }
}
