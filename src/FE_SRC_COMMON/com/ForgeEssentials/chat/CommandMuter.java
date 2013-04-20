package com.ForgeEssentials.chat;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ForgeSubscribe;

import com.ForgeEssentials.util.Localization;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandMuter
{
    public static ArrayList<String> mutedCommands = new ArrayList<String>();
    
    @ForgeSubscribe
    public void commandEvent(CommandEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        
        if (e.sender instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) e.sender;
            if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean("mute"))
            {
                if (mutedCommands.contains(e.command.getCommandName()))
                {
                    player.sendChatToPlayer(Localization.get("message.muted"));
                    e.setCanceled(true);
                    return;
                }
                else
                {
                    for (Object obj : e.command.getCommandAliases())
                    {
                        if(mutedCommands.contains(obj.toString()))
                        {
                            player.sendChatToPlayer(Localization.get("message.muted"));
                            e.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }
        
        ModuleChat.logCmd("[" + e.sender.getCommandSenderName() + "] /" + e.command.getCommandName() + " " + join(e.parameters));
    }
    
    public String join(String[] args)
    {
        StringBuilder sb = new StringBuilder();
        for (String agr : args)
            sb.append(agr + " ");
        return sb.toString();
    }
}
