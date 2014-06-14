package com.forgeessentials.util.events;

import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.util.AreaSelector.WarpPoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;

@Cancelable
public class PlayerChangedZone extends PlayerEvent {
    public final Zone beforeZone;
    public final Zone afterZone;
    public final WarpPoint afterPoint;
    public final WarpPoint beforePoint;

    public PlayerChangedZone(EntityPlayer player, Zone beforeZone, Zone afterZone, WarpPoint beforePoint, WarpPoint afterPoint)
    {
        super(player);
        this.beforeZone = beforeZone;
        this.afterZone = afterZone;
        this.beforePoint = beforePoint;
        this.afterPoint = afterPoint;
    }
}
