package com.forgeessentials.playerlogger.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.util.CommandParserArgs;

public class CommandTestPlayerlogger extends ParserCommandBase
{

    public EntityPlayerMP player;

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
    public String getUsage(ICommandSender sender)
    {
        return "/testpl";
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
                    ForgeEventFactory.onPlayerBlockPlace(player, new BlockSnapshot(player.world, pos, Blocks.AIR.getDefaultState()), EnumFacing.DOWN, EnumHand.MAIN_HAND);
                else
                    MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(player.world, pos, Blocks.DIRT.getDefaultState(), player));
            place = !place;
        }
    }

}
