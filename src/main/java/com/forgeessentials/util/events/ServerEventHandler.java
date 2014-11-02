package com.forgeessentials.util.events;

import net.minecraftforge.common.MinecraftForge;

import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ServerEventHandler {

    private boolean registered = false;

    public ServerEventHandler()
    {
        FMLCommonHandler.instance().bus().register(this);
    }

    public ServerEventHandler(boolean forceRegister)
    {
        this();
        if (forceRegister)
            register();
    }

    private void register()
    {
        if (registered) return;
        registered = true;
        MinecraftForge.EVENT_BUS.register(this);
        FunctionHelper.FE_INTERNAL_EVENTBUS.register(this);
    }

    private void unregister()
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
            try
            {
                FunctionHelper.FE_INTERNAL_EVENTBUS.unregister(this);
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
