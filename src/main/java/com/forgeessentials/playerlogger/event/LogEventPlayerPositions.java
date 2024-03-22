package com.forgeessentials.playerlogger.event;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action04PlayerPosition;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class LogEventPlayerPositions extends PlayerLoggerEvent<Object>
{

    public LogEventPlayerPositions()
    {
        super(null);
    }

    @Override
    public void process(EntityManager em)
    {
        List<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        date = new Date();

        for (ServerPlayer player : players) {
            // Action03PlayerEvent action = new Action03PlayerEvent();
            // action.type = PlayerEventType.MOVE;
            Action04PlayerPosition action = new Action04PlayerPosition();
            action.time = date;
            action.player = getPlayer(player);
            action.world = player.level.dimension().location().toString();
            action.x = (int) player.position().x;
            action.y = (int) player.position().y;
            action.z = (int) player.position().z;
            // em.persist(action);
        }
    }

}
