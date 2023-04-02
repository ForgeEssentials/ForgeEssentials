package com.forgeessentials.compat.worldedit;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.forgeessentials.util.selections.SelectionHandler;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * This class checks for player interactions which could modify the WorldEdit selection and sends a selection update to the client if it might be necessary.
 */
public class CUIComms
{

    public CUIComms()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static final String[] worldEditSelectionCommands = new String[] { "/pos1", "/pos2", "/sel", "/desel", "/hpos1", "/hpos2", "/chunk", "/expand",
            "/contract", "/outset", "/inset", "/shift" };

    protected List<ServerPlayerEntity> updatedSelectionPlayers = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void checkWECommands(CommandEvent e)
    {
        if (e.getParseResults().getContext().getSource().getEntity() instanceof ServerPlayerEntity)
        {
            if(e.getParseResults().getContext().getNodes().isEmpty())
                return;
            String cmd = e.getParseResults().getContext().getNodes().get(0).toString();
            for (String weCmd : worldEditSelectionCommands)
            {
                if (cmd.equals(weCmd) && !(e.getParseResults().getContext().getSource().getEntity() instanceof FakePlayer))
                {
                    updatedSelectionPlayers.add((ServerPlayerEntity) e.getParseResults().getContext().getSource().getEntity());
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent e)
    {
        for (ServerPlayerEntity player : updatedSelectionPlayers)
            SelectionHandler.sendUpdate(player);
        updatedSelectionPlayers.clear();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        if (event.getPlayer() instanceof ServerPlayerEntity)
            updatedSelectionPlayers.add((ServerPlayerEntity) event.getPlayer());
    }

}
