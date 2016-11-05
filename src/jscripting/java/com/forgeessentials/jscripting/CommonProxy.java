package com.forgeessentials.jscripting;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy
{


    @SubscribeEvent
    public void init(FMLInitializationEvent event)
    {
        /* do nothing */
    }

    @SubscribeEvent
    public void serverStartEvent(FMLServerStartedEvent event)
    {
    }

}
