package com.forgeessentials.util.events;

import com.forgeessentials.commons.selections.WarpPoint;
import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.forgeessentials.util.selections.WarpPoint;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class PlayerMoveEvent extends PlayerEvent {
    public final WarpPoint before;
    public final WarpPoint after;

    public PlayerMoveEvent(EntityPlayer player, WarpPoint before, WarpPoint after)
    {
        super(player);
        this.before = before;
        this.after = after;
    }

    public boolean isViewMove()
    {
        return before.getYaw() != after.getYaw() && before.getPitch() != after.getPitch();
    }

    public boolean isCoordMove()
    {
        return before.getX() != after.getX() && before.getY() != after.getY() && before.getZ() != after.getZ();
    }

    public boolean isBlockMove()
    {
        return before.getBlockX() != after.getBlockX() && before.getBlockY() != after.getBlockY() && before.getBlockZ() != after.getBlockZ();
    }

}
