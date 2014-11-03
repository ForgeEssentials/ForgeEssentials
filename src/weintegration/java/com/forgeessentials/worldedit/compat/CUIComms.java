package com.forgeessentials.worldedit.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.PlayerInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

// i said no, but olee is a shithead -.-
// temporary until i can get around to proper implementation of WECUI protocol
public class CUIComms
{

    public CUIComms()
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    public static final String[] worldEditSelectionCommands = new String[] { "/pos1", "/pos2", "/sel", "/desel", "/hpos1", "/hpos2", "/chunk", "/expand",
            "/contract", "/outset", "/inset", "/shift" };

    protected List<PlayerInfo> updatedSelectionPlayers = new ArrayList<PlayerInfo>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void checkWECommands(CommandEvent e)
    {
        if (e.sender instanceof EntityPlayerMP)
        {
            String cmd = e.command.getCommandName();
            for (String weCmd : worldEditSelectionCommands)
            {
                if (cmd.equals(weCmd))
                {
                    updatedSelectionPlayers.add(PlayerInfo.getPlayerInfo((EntityPlayerMP) e.sender));
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent e)
    {
        for (PlayerInfo pi : updatedSelectionPlayers)
        {
            pi.sendSelectionUpdate();
        }
        updatedSelectionPlayers.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (ForgeEssentials.worldEditCompatilityPresent && FMLCommonHandler.instance().getEffectiveSide().isServer() && event.entityPlayer != null)
        {
            updatedSelectionPlayers.add(PlayerInfo.getPlayerInfo(event.entityPlayer));
        }
    }
}
