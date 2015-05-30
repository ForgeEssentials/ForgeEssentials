package com.forgeessentials.util.events;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.command.ICommandSender;

public class ConfigReloadEvent extends Event
{

    public ICommandSender sender;

    public ConfigReloadEvent(ICommandSender sender)
    {
        super();
        this.sender = sender;
    }
}
