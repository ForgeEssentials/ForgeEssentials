package com.forgeessentials.compat;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
