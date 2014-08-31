package com.forgeessentials.util.events;

import com.forgeessentials.util.selections.WarpPoint;
import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

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
        return before.yaw != after.yaw && before.pitch != after.pitch;
    }

    public boolean isCoordMove()
    {
        return before.xd != after.xd && before.yd != after.yd && before.zd != after.zd;
    }

    public boolean isBlockMove()
    {
        return before.x != after.x && before.y != after.y && before.z != after.z;
    }

}
