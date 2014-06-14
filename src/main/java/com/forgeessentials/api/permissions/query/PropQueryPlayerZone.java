package com.forgeessentials.api.permissions.query;

import com.forgeessentials.api.permissions.Zone;
import net.minecraft.entity.player.EntityPlayer;

public class PropQueryPlayerZone extends PropQueryPlayer {
    public Zone zone;
    public boolean checkParents;

    public PropQueryPlayerZone(EntityPlayer player, String permKey, Zone zone, boolean checkParents)
    {
        super(player, permKey);
        this.zone = zone;
        this.checkParents = checkParents;
    }

}
