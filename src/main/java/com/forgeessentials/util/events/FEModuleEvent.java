package com.forgeessentials.util.events;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fmlserverevents.FMLServerStartedEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppingEvent;
import net.minecraftforge.fmlserverevents.ServerLifecycleEvent;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;

public class FEModuleEvent extends Event
{

    protected ServerLifecycleEvent event;

    public ServerLifecycleEvent getServerLifecycleEvent()
    {
        return event;
    }

    public static class FEModuleServerAboutToStartEvent extends FEModuleEvent
    {
        public FEModuleServerAboutToStartEvent(FMLServerAboutToStartEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModuleServerStartingEvent extends FEModuleEvent
    {
        public FEModuleServerStartingEvent(FMLServerStartingEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModuleServerStartedEvent extends FEModuleEvent
    {
        public FEModuleServerStartedEvent(FMLServerStartedEvent event)
        {
            this.event = event;
        }
    }

    public static class FEModuleServerStoppingEvent extends FEModuleEvent
    {
        public FEModuleServerStoppingEvent(FMLServerStoppingEvent event)
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
