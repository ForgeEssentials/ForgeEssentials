package com.forgeessentials.chat;

import java.util.ArrayList;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.CommandEvent;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.chat.irc.IRCHelper;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommandMuter {
    public static ArrayList<String> mutedCommands = new ArrayList<String>();

    public static boolean muteCmdBlocks;

    @SubscribeEvent
    public void commandEvent(CommandEvent e)
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            return;
        }

        if (e.sender instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) e.sender;
            if (player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getBoolean("mute"))
            {
                if (mutedCommands.contains(e.command.getCommandName()))
                {
                    OutputHandler.chatWarning(player, "You are currently muted.");
                    e.setCanceled(true);
                    return;
                }
                else
                {
                    if (e.command.getCommandAliases() == null)
                    {
                        return;
                    }

                    for (Object obj : e.command.getCommandAliases())
                    {
                        if (mutedCommands.contains(obj.toString()))
                        {
                            OutputHandler.chatWarning(player, "You are currently muted.");
                            e.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }

        if (ConfigChat.logcmd && ModuleChat.cmdLog != null)
        {
            ModuleChat.cmdLog.printf("%s %s [%s] /%s %s", FunctionHelper.getCurrentDateString(), FunctionHelper.getCurrentTimeString(),
                    e.sender.getCommandSenderName(), e.command.getCommandName(), StringUtils.join(e.parameters, " "));
        }

        StringBuilder unpacked = new StringBuilder();

        if (e.command.getCommandName().equalsIgnoreCase("me"))
        {
            for (String takeout : e.parameters)
            {
                unpacked.append(takeout + " ");
            }
            IRCHelper.postIRC(e.sender.getCommandSenderName() + " " + unpacked.toString());
        }
        if (e.command.getCommandName().equalsIgnoreCase("say"))
        {
            for (String takeout : e.parameters)
            {
                unpacked.append(takeout + " ");
            }
            IRCHelper.postIRC("[CONSOLE] " + unpacked.toString());
        }

    }

    private void handleSayCommand(String print, ICommandSender sender)
    {
        if (sender instanceof CommandBlockLogic)
        {
            if (!muteCmdBlocks)
            {
                IRCHelper.postIRC("[CommandBlock] " + print);
            }
        }
        else IRCHelper.postIRC("[CONSOLE] " + print);
    }
}
