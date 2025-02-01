package com.forgeessentials.playerlogger;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
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
        ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
        if (stack == null || stack.getItem() != Items.clock)
            return;
        if (event.action == Action.RIGHT_CLICK_AIR)
            return;
        if (!APIRegistry.perms.checkPermission(event.entityPlayer, ModulePlayerLogger.PERM_WAND))
            return;
        event.setCanceled(true);

        WorldPoint point;
        if (event.action == Action.RIGHT_CLICK_BLOCK)
            point = new WorldPoint(event.entityPlayer.dimension, //
                    event.pos.getX() + event.face.getFrontOffsetX(), //
                    event.pos.getY() + event.face.getFrontOffsetY(), //
                    event.pos.getZ() + event.face.getFrontOffsetZ());
        else
            point = new WorldPoint(event.entityPlayer.dimension, event.pos);

        PlayerLoggerChecker.instance.CheckBlock(point,FilterConfig.getDefaultPlayerConfig(UserIdent.get(event.entityPlayer)),event.entityPlayer);
    }

}
