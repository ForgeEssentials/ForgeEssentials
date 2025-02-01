package com.forgeessentials.playerlogger.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.permission.PermissionLevel;

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
    public String getCommandName()
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
    public String getCommandUsage(ICommandSender sender)
    {
        return "/testpl";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public void parse(CommandParserArgs arguments)
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
            for (int i = 0; i < 300; i++)
                if (place)
                    ForgeEventFactory.onPlayerBlockPlace(player, new BlockSnapshot(player.worldObj, new BlockPos(x, y, z), Blocks.air.getDefaultState()), EnumFacing.DOWN);
                else
                    MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(player.worldObj, new BlockPos(x, y, z), Blocks.dirt.getDefaultState(), player));
            place = !place;
        }
    }

}
