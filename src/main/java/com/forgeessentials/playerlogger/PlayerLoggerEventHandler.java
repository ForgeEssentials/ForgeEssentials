package com.forgeessentials.playerlogger;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.events.ServerEventHandler;

public class PlayerLoggerEventHandler extends ServerEventHandler
{

    private static PlayerLoggerEventHandler instance = null;

    //public static int pickerRange = 0;

    public static int eventType = 0b1111;

    public static String searchCriteria = "";

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();
        if (stack == ItemStack.EMPTY || stack.getItem() != Items.CLOCK)
            return;
        if (event instanceof RightClickEmpty)
            return;
        if (!APIRegistry.perms.checkPermission(event.getEntityPlayer(), ModulePlayerLogger.PERM_WAND))
            return;
        event.setCanceled(true);

        WorldPoint point;
        if (event instanceof RightClickBlock)
            point = new WorldPoint(event.getEntityPlayer().dimension, //
                    event.getPos().getX() + event.getFace().getFrontOffsetX(), //
                    event.getPos().getY() + event.getFace().getFrontOffsetY(), //
                    event.getPos().getZ() + event.getFace().getFrontOffsetZ());
        else
            point = new WorldPoint(event.getEntityPlayer().dimension, event.getPos());

        PlayerLoggerChecker.instance.CheckBlock(point,FilterConfig.getDefaultPlayerConfig(UserIdent.get(event.getEntityPlayer())),event.getEntityPlayer());
    }

}
