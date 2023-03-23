package com.forgeessentials.playerlogger;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.events.ServerEventHandler;

public class PlayerLoggerEventHandler extends ServerEventHandler
{

    //private static PlayerLoggerEventHandler instance = null;

    // public static int pickerRange = 0;

    public static int eventType = 0b1111;

    public static String searchCriteria = "";

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        ItemStack stack = event.getItemStack();
        if (stack == ItemStack.EMPTY || stack.getItem() != Items.CLOCK)
            return;
        if (event instanceof RightClickEmpty)
            return;
        if (!APIRegistry.perms.checkPermission(event.getPlayer(), ModulePlayerLogger.PERM_WAND))
            return;
        event.setCanceled(true);

        WorldPoint point;
        if (event instanceof RightClickBlock)
            point = new WorldPoint(event.getPlayer().level, //
                    event.getPos().getX(), // + event.getFace().getFrontOffsetX(), //
                    event.getPos().getY(), // + event.getFace().getFrontOffsetY(), //
                    event.getPos().getZ());// + event.getFace().getFrontOffsetZ());
        else
            point = new WorldPoint(event.getPlayer().level, event.getPos());

        PlayerLoggerChecker.instance.CheckBlock(point, FilterConfig.getDefaultPlayerConfig(UserIdent.get(event.getPlayer())),
                event.getPlayer().createCommandSourceStack());
    }

}
