package com.forgeessentials.worldborder.effect;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

public class EffectKick extends WorldBorderEffect
{

    public static final int TIMEOUT = 10 * 1000;

    @Override
    public void activate(WorldBorder border, EntityPlayerMP player)
    {
        // TODO: Maybe check if this is singleplayer and disable then?
        ChatOutputHandler.chatError(player, Translator.format("You have %d seconds to return inside the world border, or you will get kicked!", TIMEOUT));
        PlayerInfo pi = PlayerInfo.get(player);
        pi.startTimeout(this.getClass().getName(), TIMEOUT);
    }

    @Override
    public void tick(WorldBorder border, EntityPlayerMP player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            player.playerNetServerHandler.kickPlayerFromServer("You left the world border");
            // For safety restart the timeout
            pi.startTimeout(this.getClass().getName(), TIMEOUT);
        }
    }

}
