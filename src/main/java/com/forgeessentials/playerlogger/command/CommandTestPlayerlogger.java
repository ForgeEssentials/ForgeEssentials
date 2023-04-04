package com.forgeessentials.playerlogger.command;

import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandTestPlayerlogger extends ForgeEssentialsCommandBuilder
{

    public CommandTestPlayerlogger(boolean enabled)
    {
        super(enabled);
    }

    public ServerPlayerEntity player;

    public boolean place;

    @Override
    public String getPrimaryAlias()
    {
        return "testpl";
    }

    @Override
    public String getPermissionNode()
    {
        return "testpl";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .executes(CommandContext -> execute(CommandContext, null)
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (player == null)
            player = getServerPlayer(ctx.getSource());
        else
            player = null;
        return Command.SINGLE_SUCCESS;
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event)
    {
        if (player != null)
        {
            int x = 0;
            int y = 200;
            int z = 0;
            BlockPos pos = new BlockPos(x, y, z);
            for (int i = 0; i < 300; i++)
                if (place)
                    ForgeEventFactory.onBlockPlace(player, BlockSnapshot.create(player.level.dimension(), player.level, pos), Direction.DOWN);
                else
                    MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(player.level, pos, Blocks.DIRT.defaultBlockState(), player));
            place = !place;
        }
    }

}
