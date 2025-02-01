package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;



public class JsAchievementEvent extends JsPlayerEvent<AchievementEvent>
{

    @SubscribeEvent
    public final void _handle(AchievementEvent event)
    {
        _callEvent(event);
    }

}
