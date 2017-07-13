package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.CommandEvent;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.UserIdent;
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
        action.command = event.getCommand().getName();
        if (event.getParameters().length > 0)
            action.arguments = StringUtils.join(event.getParameters(), ' ');
        if (event.getSender() instanceof EntityPlayer)
        {
            EntityPlayer player = ((EntityPlayer) event.getSender());
            action.player = getPlayer(player);
            action.world = getWorld(player.world.provider.getDimension());
            action.x = (int) player.posX;
            action.y = (int) player.posY;
            action.z = (int) player.posZ;
        }
        else if (event.getSender() instanceof CommandBlockBaseLogic)
        {
            CommandBlockBaseLogic block = ((CommandBlockBaseLogic) event.getSender());
            action.player = getPlayer(UserIdent.getVirtualPlayer("commandblock"));
            action.world = getWorld(block.getEntityWorld().provider.getDimension());
            BlockPos pos = block.getPosition();
            action.x = pos.getX();
            action.y = pos.getY();
            action.z = pos.getZ();
        }
        em.persist(action);
    }

}