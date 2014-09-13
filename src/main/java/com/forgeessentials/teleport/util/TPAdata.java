package com.forgeessentials.teleport.util;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.OutputHandler;

public class TPAdata {
    public EntityPlayerMP sender;
    public EntityPlayerMP receiver;
    public boolean tphere;
    int timeout;

    public TPAdata(EntityPlayerMP sender, EntityPlayerMP receiver, boolean tpaHere)
    {
        this.sender = sender;
        this.receiver = receiver;

        timeout = TeleportModule.timeout;
        tphere = tpaHere;
    }

    public void count()
    {
        if (sender == null)
        {
            TeleportModule.tpaListToRemove.add(this);
            return;
        }

        if (receiver == null)
        {
            TeleportModule.tpaListToRemove.add(this);
            return;
        }

        if (timeout == 0)
        {
            TeleportModule.tpaListToRemove.add(this);
            if (tphere)
            {
                OutputHandler.chatWarning(receiver, String.format("TPA from %s timed out.", sender.getCommandSenderName()));
                OutputHandler.chatWarning(sender, String.format("TPA to %s timed out.", receiver.getCommandSenderName()));
            }
            else
            {
                OutputHandler.chatWarning(receiver, String.format("TPA from %s timed out", sender.getCommandSenderName()));
                OutputHandler.chatWarning(sender, String.format("TPA to %s timed out.", receiver.getCommandSenderName()));
            }
        }
        timeout--;
    }
}
