package com.forgeessentials.compat;

import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.IItemHandler;

import com.forgeessentials.util.events.FEPlayerEvent.InventoryGroupChange;
import com.forgeessentials.util.events.ServerEventHandler;

import baubles.api.BaublesApi;

public class BaublesCompat extends ServerEventHandler
{
    @Method(modid = "baubles")
    @SubscribeEvent
    public void updateInventory(InventoryGroupChange e)
    {
        IItemHandler inventory = BaublesApi.getBaublesHandler(e.getPlayer());
        e.swapInventory("baubles", inventory);
    }
}
