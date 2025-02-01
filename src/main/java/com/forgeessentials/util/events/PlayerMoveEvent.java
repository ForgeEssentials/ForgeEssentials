package com.forgeessentials.util.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import com.forgeessentials.commons.selections.WarpPoint;

@Cancelable
public class PlayerMoveEvent extends FEPlayerEvent
{
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
