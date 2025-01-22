package com.forgeessentials.playerlogger.event;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action04PlayerPosition;

public class LogEventPlayerPositions extends PlayerLoggerEvent<Object>
{

    public LogEventPlayerPositions()
    {
        super(null);
    }

    @Override
    public void process(EntityManager em)
    {
        List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        date = new Date();
        
        for (Iterator<EntityPlayerMP> it = players.iterator(); it.hasNext();)
        {
            EntityPlayerMP player = it.next();

            // Action03PlayerEvent action = new Action03PlayerEvent();
            // action.type = PlayerEventType.MOVE;
            Action04PlayerPosition action = new Action04PlayerPosition();
            action.time = date;
            action.player = getPlayer(player);
            action.world = getWorld(player.worldObj.provider.getDimensionId());
            action.x = (int) player.posX;
            action.y = (int) player.posY;
            action.z = (int) player.posZ;
            //em.persist(action);
        }
    }

}
