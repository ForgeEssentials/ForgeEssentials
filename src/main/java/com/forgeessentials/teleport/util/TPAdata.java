package com.forgeessentials.teleport.util;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.teleport.TeleportModule;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class TPAdata {
    public EntityPlayerMP sender;
    public EntityPlayerMP receiver;
    public boolean tphere;
    private int timeout;
    private long startTime;

    public TPAdata(EntityPlayerMP sender, EntityPlayerMP receiver, boolean tpaHere)
    {
        this.sender = sender;
        this.receiver = receiver;

        timeout = FunctionHelper.parseIntDefault(APIRegistry.perms.getPermissionProperty(TeleportModule.PERM_TIMEOUT), 30);
        startTime = System.currentTimeMillis();
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

        if ((System.currentTimeMillis() - startTime) / 1000L > timeout)
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
    }
}
