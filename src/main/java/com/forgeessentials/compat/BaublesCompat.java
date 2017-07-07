package com.forgeessentials.compat;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.inventory.IInventory;

import com.forgeessentials.util.events.FEPlayerEvent.InventoryGroupChange;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.LoggingHandler;

import baubles.api.BaublesApi;

public class BaublesCompat extends ServerEventHandler
{
    public BaublesCompat()
    {
        if (Loader.isModLoaded("Baubles"))
            LoggingHandler.felog.info("Baubles compatibility enabled.");
        register();
    }
    @Method(modid = "Baubles")
    @SubscribeEvent
    public void updateInventory(InventoryGroupChange e)
    {
        IInventory inventory = BaublesApi.getBaubles(e.getPlayer());
        e.swapInventory("baubles", inventory);
    }
}
