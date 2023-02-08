package com.forgeessentials.playerlogger.command;

import net.minecraft.block.Blocks;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.CommandParserArgs;

public class CommandTestPlayerlogger extends ForgeEssentialsCommandBuilder
{

    public ServerPlayerEntity player;

    public boolean place;

    public CommandTestPlayerlogger()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

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
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (player == null)
            player = arguments.senderPlayer;
        else
            player = null;
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
                    ForgeEventFactory.onBlockPlace(player, new BlockSnapshot(player.level, pos, Blocks.AIR.defaultBlockState()), Direction.DOWN);
                else
                    MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(player.level, pos, Blocks.DIRT.defaultBlockState(), player));
            place = !place;
        }
    }

}
