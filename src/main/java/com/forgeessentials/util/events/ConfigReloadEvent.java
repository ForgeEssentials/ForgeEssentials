package com.forgeessentials.util.events;

import net.minecraft.command.ICommandSender;
import cpw.mods.fml.common.eventhandler.Event;

public class ConfigReloadEvent extends Event
{

    public ICommandSender sender;

    public ConfigReloadEvent(ICommandSender sender)
    {
        super();
        this.sender = sender;
    }
}
