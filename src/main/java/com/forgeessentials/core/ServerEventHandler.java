package com.forgeessentials.core;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;

public class ServerEventHandler {

    public ServerEventHandler()
    {
        FMLCommonHandler.instance().bus().register(this);
    }

    @EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @EventHandler
    public void serverStopped(FMLServerStoppedEvent e)
    {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

}
