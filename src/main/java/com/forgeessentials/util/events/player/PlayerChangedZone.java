package com.forgeessentials.util.events.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.WarpPoint;

@Cancelable
public class PlayerChangedZone extends PlayerEvent
{
    public final Zone beforeZone;
    public final Zone afterZone;
    public final WarpPoint afterPoint;
    public final WarpPoint beforePoint;

    public PlayerChangedZone(PlayerEntity player, Zone beforeZone, Zone afterZone, WarpPoint beforePoint, WarpPoint afterPoint)
    {
        super(player);
        this.beforeZone = beforeZone;
        this.afterZone = afterZone;
        this.beforePoint = beforePoint;
        this.afterPoint = afterPoint;
    }
}
