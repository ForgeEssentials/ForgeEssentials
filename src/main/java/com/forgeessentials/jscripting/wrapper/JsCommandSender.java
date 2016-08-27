package com.forgeessentials.jscripting.wrapper;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class JsCommandSender
{

    private ICommandSender that;

    public JsCommandSender(ICommandSender sender)
    {
        this.that = sender;
    }

    public ICommandSender getThat()
    {
        return that;
    }

    public String getName()
    {
        return that.getCommandSenderName();
    }

    public JsPlayer getPlayer()
    {
        return that instanceof EntityPlayer ? new JsPlayer((EntityPlayer) that) : null;
    }

}
