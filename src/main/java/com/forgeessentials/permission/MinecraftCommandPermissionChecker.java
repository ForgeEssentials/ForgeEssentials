package com.forgeessentials.permission;

import com.forgeessentials.api.APIRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraft.command.ICommand;

// hehe.. the name of this class can be shortened to MCPC :D

public class MinecraftCommandPermissionChecker {

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    public void checkCommandPerm(CommandEvent e)
    {
        if (!(e.sender instanceof EntityPlayer)){return;}
        ICommand command = e.command;
        if (e.command.getClass().getCanonicalName().startsWith("net.minecraft.command"))
        {
            boolean allow = APIRegistry.perms.checkPermAllowed((EntityPlayer)e.sender, "mc." + e.command.getCommandName());
            if (!allow){
                e.setCanceled(true);
            }

        }
    }
}
