//package com.forgeessentials.compat;
//
//import com.forgeessentials.util.events.ServerEventHandler;
//import com.forgeessentials.util.events.player.FEPlayerEvent.InventoryGroupChange;
//import com.forgeessentials.util.output.logger.LoggingHandler;
//
//import lazy.baubles.api.BaublesAPI;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.ModList;
//import net.minecraftforge.items.IItemHandlerModifiable;
//
//public class BaublesCompat extends ServerEventHandler
//{
//    // TODO get the proper Mod id. I don't know "Baubles" anymore, the Wiki page for
//    // the Mod is down.
//    public BaublesCompat()
//    {
//        if (ModList.get().isLoaded("Baubles")) {
//            LoggingHandler.felog.info("Baubles compatibility enabled.");
//            register();
//        }
//    }
//
//    // @Optional(modid = "Baubles") //Use capabilities instead of directly
//    // implementing interfaces.
//    @SubscribeEvent
//    public void updateInventory(InventoryGroupChange e)
//    {
//        IItemHandlerModifiable inventory = BaublesAPI.getBaublesHandler(e.getPlayer()).orElse(null);
//        e.swapInventory("baubles", inventory);
//    }
//}
