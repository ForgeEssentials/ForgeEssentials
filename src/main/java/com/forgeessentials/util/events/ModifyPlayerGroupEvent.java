package com.forgeessentials.util.events;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

import java.util.UUID;

@Cancelable
public class ModifyPlayerGroupEvent extends Event {
    public String group;
    public String zone;
    public UUID player;
    public Type type;
    private String reason;

    public ModifyPlayerGroupEvent(String group, UUID player, String zone, Type type)
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
        {
            return "";
        }
        else
        {
            return reason;
        }
    }

    public static enum Type {
        SET, ADD, REMOVE
    }

    @Cancelable
    public static class SetPlayerGroupEvent extends ModifyPlayerGroupEvent {
        public SetPlayerGroupEvent(String group, UUID player, String zone)
        {
            super(group, player, zone, Type.SET);
        }
    }

    @Cancelable
    public static class AddPlayerGroupEvent extends ModifyPlayerGroupEvent {
        public AddPlayerGroupEvent(String group, UUID player, String zone)
        {
            super(group, player, zone, Type.ADD);
        }
    }

    @Cancelable
    public static class RemovePlayerGroupEvent extends ModifyPlayerGroupEvent {
        public RemovePlayerGroupEvent(String group, UUID player, String zone)
        {
            super(group, player, zone, Type.REMOVE);
        }
    }
}