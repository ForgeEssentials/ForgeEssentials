package com.forgeessentials.util.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;




public class ServerEventHandler
{

    private boolean registered = false;

    public ServerEventHandler()
    {
        APIRegistry.getFEEventBus().register(this);
    }

    public ServerEventHandler(boolean forceRegister)
    {
        this();
        if (forceRegister)
            register();
    }

    protected void register()
    {
        if (registered)
            return;
        registered = true;
        MinecraftForge.EVENT_BUS.register(this);
    }

    protected void unregister()
    {
        if (registered)
        {
            try
            {
                MinecraftForge.EVENT_BUS.unregister(this);
            }
            catch (NullPointerException ex)
            {
                // event handler was not registered to begin with
            }
            registered = false;
        }
    }

    @SubscribeEvent
    public void serverAboutToStart(FEModuleServerPreInitEvent e)
    {
        register();
    }

    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        unregister();
    }

}
