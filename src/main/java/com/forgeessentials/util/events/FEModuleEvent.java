package com.forgeessentials.util.events;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraftforge.server.CommandHandlerForge;

public class FEModuleEvent extends Event{

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
        private FMLPostInitializationEvent event;

        public FEModulePostInitEvent(FMLPostInitializationEvent event)
        {
            this.event = event;
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
            return event.buildSoftDependProxy(modId, className);
        }

    }

    public static class FEModuleServerInitEvent extends FEModuleEvent
    {
        public FEModuleServerInitEvent(FMLServerStartingEvent event)
        {
            this.event = event;
        }

        public void registerServerCommand(ForgeEssentialsCommandBase command)
        {
            if (command.getPermissionNode() != null && command.getDefaultPermission() != null)
            {
                CommandHandlerForge.registerCommand(command, command.getPermissionNode(), command.getDefaultPermission());
            }
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
}
