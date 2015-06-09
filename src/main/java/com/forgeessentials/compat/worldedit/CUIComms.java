package com.forgeessentials.compat.worldedit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.forgeessentials.util.selections.SelectionHandler;

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

    protected List<EntityPlayerMP> updatedSelectionPlayers = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void checkWECommands(CommandEvent e)
    {
        if (e.sender instanceof EntityPlayerMP)
        {
            String cmd = e.command.getCommandName();
            for (String weCmd : worldEditSelectionCommands)
            {
                if (cmd.equals(weCmd)&& !(e.sender instanceof FakePlayer))
                {
                    updatedSelectionPlayers.add((EntityPlayerMP) e.sender);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent e)
    {
        for (EntityPlayerMP player : updatedSelectionPlayers)
            SelectionHandler.sendUpdate(player);
        updatedSelectionPlayers.clear();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        // if (ModuleLauncher.getModuleList().contains("WEIntegration") &&
        // FMLCommonHandler.instance().getEffectiveSide().isServer() && event.entityPlayer != null)
        if (FMLCommonHandler.instance().getEffectiveSide().isServer() && event.entityPlayer != null)
            updatedSelectionPlayers.add((EntityPlayerMP) event.entityPlayer);
    }

}
