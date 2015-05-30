package com.forgeessentials.util.events;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import cpw.mods.fml.common.eventhandler.Event;

public class FEModuleEvent extends Event
{

    protected FMLStateEvent event;

    public FMLStateEvent getFMLEvent()
    {
        return event;
    }

    public static class FEModulePreInitEvent extends FEModuleEvent
    {
        public FEModulePreInitEvent(FMLPreInitializationEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModuleInitEvent extends FEModuleEvent
    {
        public FEModuleInitEvent(FMLInitializationEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModulePostInitEvent extends FEModuleEvent
    {
        private FMLPostInitializationEvent parentEvent;

        public FEModulePostInitEvent(FMLPostInitializationEvent event)
        {
            this.parentEvent = event;
        }

        /**
         * bouncer for FML event method
         *
         * @param modId
         * @param className
         * @return
         */
        public Object buildSoftDependProxy(String modId, String className)
        {
            return parentEvent.buildSoftDependProxy(modId, className);
        }

    }

    public static class FEModuleServerPreInitEvent extends FEModuleEvent
    {
        public FEModuleServerPreInitEvent(FMLServerAboutToStartEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModuleServerInitEvent extends FEModuleEvent
    {
        public FEModuleServerInitEvent(FMLServerStartingEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModuleServerPostInitEvent extends FEModuleEvent
    {
        public FEModuleServerPostInitEvent(FMLServerStartedEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModuleServerStopEvent extends FEModuleEvent
    {
        public FEModuleServerStopEvent(FMLServerStoppingEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModuleServerStoppedEvent extends FEModuleEvent
    {
        public FEModuleServerStoppedEvent(FMLServerStoppedEvent event)
        {
            this.event = event;
        }
    }

}
