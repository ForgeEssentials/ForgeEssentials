package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.CommandEvent;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action02Command;

public class LogEventCommand extends PlayerLoggerEvent<CommandEvent>
{

    public LogEventCommand(CommandEvent event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
        Action02Command action = new Action02Command();
        action.time = date;
        action.command = event.command.getCommandName();
        if (event.parameters.length > 0)
            action.arguments = StringUtils.join(event.parameters, ' ');
        if (event.sender instanceof EntityPlayer)
        {
            EntityPlayer player = ((EntityPlayer) event.sender);
            action.player = getPlayer(player);
            action.world = getWorld(player.worldObj.provider.getDimensionId());
            action.x = (int) player.posX;
            action.y = (int) player.posY;
            action.z = (int) player.posZ;
        }
        else if (event.sender instanceof CommandBlockLogic)
        {
            CommandBlockLogic block = ((CommandBlockLogic) event.sender);
            action.player = getPlayer(APIRegistry.IDENT_CMDBLOCK);
            action.world = getWorld(block.getEntityWorld().provider.getDimensionId());
            BlockPos pos = block.getPosition();
            action.x = pos.getX();
            action.y = pos.getY();
            action.z = pos.getZ();
        }
        em.persist(action);
    }

}