package com.forgeessentials.compat;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.IItemHandlerModifiable;

import com.forgeessentials.util.events.FEPlayerEvent.InventoryGroupChange;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.LoggingHandler;

import baubles.api.BaublesApi;

public class BaublesCompat extends ServerEventHandler
{
    public BaublesCompat()
    {
        if (ModList.get().isLoaded("Baubles"))
            LoggingHandler.felog.info("Baubles compatibility enabled.");
        register();
    }
    @Method(modid = "Baubles")
    @SubscribeEvent
    public void updateInventory(InventoryGroupChange e)
    {
        IItemHandlerModifiable inventory = BaublesApi.getBaublesHandler(e.getPlayer());
        e.swapInventory("baubles", inventory);
    }
}
