package com.forgeessentials.playerlogger.event;

import java.util.Date;

import javax.persistence.EntityManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.event.CommandEvent;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.ActionCommand;

public class LogEventCommand extends PlayerLoggerEvent<CommandEvent>
{

    public LogEventCommand(CommandEvent event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
        ActionCommand action = new ActionCommand();
        action.time = new Date();
        action.command = event.command.getCommandName();
        if (event.parameters.length > 0)
            action.arguments = StringUtils.join(event.parameters, ' ');
        if (event.sender instanceof EntityPlayer)
        {
            EntityPlayer player = ((EntityPlayer) event.sender);
            action.player = getPlayer(player.getPersistentID());
            action.world = getWorld(player.worldObj.provider.dimensionId);
            action.x = (int) player.posX;
            action.y = (int) player.posY;
            action.z = (int) player.posZ;
        }
        else if (event.sender instanceof TileEntityCommandBlock)
        {
            TileEntityCommandBlock block = ((TileEntityCommandBlock) event.sender);
            action.player = getPlayer("commandblock");
            action.world = getWorld(block.getWorldObj().provider.dimensionId);
            action.x = block.xCoord;
            action.y = block.yCoord;
            action.z = block.zCoord;
        }
        em.persist(action);
    }

}