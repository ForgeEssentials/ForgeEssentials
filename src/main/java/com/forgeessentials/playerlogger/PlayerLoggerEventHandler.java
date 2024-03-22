package com.forgeessentials.playerlogger;

import java.util.TimerTask;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.util.events.ServerEventHandler;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerLoggerEventHandler extends ServerEventHandler
{
    public static boolean disabled = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerInteractEvent(PlayerInteractEvent event)
    {
        ItemStack stack = event.getItemStack();
        if (stack == ItemStack.EMPTY || stack.getItem() != Items.CLOCK)
            return;
        if (!APIRegistry.perms.checkPermission(event.getPlayer(), ModulePlayerLogger.PERM_WAND))
            return;
        if (disabled)
            return;
        disabled = true;
        event.setCanceled(true);
        TaskRegistry.schedule(new TimerTask() {
            @Override
            public void run()
            {
                disabled = false;
            }
        }, 500L);
        WorldPoint point;
        if (event instanceof RightClickBlock)
            point = new WorldPoint(event.getPlayer().level, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
        else
            point = new WorldPoint(event.getPlayer().level, event.getPos());

        PlayerLoggerChecker.instance.CheckBlock(point,
                FilterConfig.getDefaultPlayerConfig(UserIdent.get(event.getPlayer())) != null
                        ? FilterConfig.getDefaultPlayerConfig(UserIdent.get(event.getPlayer()))
                        : FilterConfig.globalConfig,
                event.getPlayer().createCommandSourceStack(), 4, false, event);
    }

}
