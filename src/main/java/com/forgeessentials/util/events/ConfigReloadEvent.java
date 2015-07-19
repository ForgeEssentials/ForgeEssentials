package com.forgeessentials.util.events;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ConfigReloadEvent extends Event
{

    public ICommandSender sender;

    public ConfigReloadEvent(ICommandSender sender)
    {
        super();
        this.sender = sender;
    }
}
