package com.forgeessentials.util.events;

import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class ServerEventHandler {

    public ServerEventHandler()
    {
        FMLCommonHandler.instance().bus().register(this);
        FunctionHelper.FE_INTERNAL_EVENTBUS.register(this);
    }

    @SubscribeEvent
    public void serverAboutToStart(FEModuleServerPreInitEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

}
