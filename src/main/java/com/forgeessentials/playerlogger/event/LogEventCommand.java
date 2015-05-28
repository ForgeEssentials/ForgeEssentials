package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
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
        action.time = date;
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
        else if (event.sender instanceof CommandBlockLogic)
        {
            CommandBlockLogic block = ((CommandBlockLogic) event.sender);
            action.player = getPlayer("commandblock");
            action.world = getWorld(block.getEntityWorld().provider.dimensionId);
            ChunkCoordinates coords = block.getPlayerCoordinates();
            action.x = coords.posX;
            action.y = coords.posY;
            action.z = coords.posZ;
        }
        em.persist(action);
    }

}