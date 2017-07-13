package com.forgeessentials.compat.worldedit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.forgeessentials.util.selections.SelectionHandler;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * This class checks for player interactions which could modify the WorldEdit selection and sends a selection update to
 * the client if it might be necessary.
 */
public class CUIComms
{

    public CUIComms()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static final String[] worldEditSelectionCommands = new String[] { "/pos1", "/pos2", "/sel", "/desel", "/hpos1", "/hpos2", "/chunk", "/expand",
            "/contract", "/outset", "/inset", "/shift" };

    protected List<EntityPlayerMP> updatedSelectionPlayers = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void checkWECommands(CommandEvent e)
    {
        if (e.getSender() instanceof EntityPlayerMP)
        {
            String cmd = e.getCommand().getName();
            for (String weCmd : worldEditSelectionCommands)
            {
                if (cmd.equals(weCmd) && !(e.getSender() instanceof FakePlayer))
                {
                    updatedSelectionPlayers.add((EntityPlayerMP) e.getSender());
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
        if (event.getEntityPlayer() instanceof EntityPlayerMP)
            updatedSelectionPlayers.add((EntityPlayerMP) event.getEntityPlayer());
    }

}
