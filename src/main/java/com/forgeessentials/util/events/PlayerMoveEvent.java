package com.forgeessentials.util.events;

import com.forgeessentials.commons.selections.WarpPoint;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class PlayerMoveEvent extends FEPlayerEvent
{
    public final WarpPoint before;
    public final WarpPoint after;

    public PlayerMoveEvent(PlayerEntity player, WarpPoint before, WarpPoint after)
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
