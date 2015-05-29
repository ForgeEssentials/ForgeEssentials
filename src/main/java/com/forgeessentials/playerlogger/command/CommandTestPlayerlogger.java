package com.forgeessentials.playerlogger.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.util.CommandParserArgs;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class CommandTestPlayerlogger extends ParserCommandBase
{

    public EntityPlayerMP player;

    public boolean place;

    public CommandTestPlayerlogger()
    {
        FMLCommonHandler.instance().bus().register(this);
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
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
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
                    ForgeEventFactory.onPlayerBlockPlace(player, new BlockSnapshot(player.worldObj, x, y, z, Blocks.air, 0), ForgeDirection.DOWN);
                else
                    MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(x, y, z, player.worldObj, Blocks.dirt, 0, player));
            place = !place;
        }
    }

}
