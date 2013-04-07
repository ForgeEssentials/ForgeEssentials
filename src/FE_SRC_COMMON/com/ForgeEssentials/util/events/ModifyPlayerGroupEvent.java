package com.ForgeEssentials.util.events;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class ModifyPlayerGroupEvent extends Event
{
    public String   group;
    public String   zone;
    public String   player;
    public Type     type;
    private String reason;
    
    public ModifyPlayerGroupEvent(String group, String player, String zone, Type type)
    {
        this.group = group;
        this.zone = zone;
        this.player = player;
        this.type = type;
    }
    
    public void setCanceled(boolean cancel, String reason)
    {
        if (cancel)
        {
            this.reason = reason;
        }

        super.setCanceled(cancel);
    }
    
    public String getCancelReason()
    {
        if (!isCanceled())
            return "";
        else
            return reason;
    }
    
    @Cancelable
    public static class SetPlayerGroupEvent extends ModifyPlayerGroupEvent
    {
        public SetPlayerGroupEvent(String group, String player, String zone)
        {
            super(group, player, zone, Type.SET);
        }
    }
    
    @Cancelable
    public static class AddPlayerGroupEvent extends ModifyPlayerGroupEvent
    {
        public AddPlayerGroupEvent(String group, String player, String zone)
        {
            super(group, player, zone, Type.ADD);
        }
    }
    
    @Cancelable
    public static class RemovePlayerGroupEvent extends ModifyPlayerGroupEvent
    {
        public RemovePlayerGroupEvent(String group, String player, String zone)
        {
            super(group, player, zone, Type.REMOVE);
        }
    }
    
    public static enum Type
    {
        SET, ADD, REMOVE
    }
}