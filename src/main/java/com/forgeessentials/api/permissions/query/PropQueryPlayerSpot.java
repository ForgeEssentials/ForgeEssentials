package com.forgeessentials.api.permissions.query;

import com.forgeessentials.util.AreaSelector.WorldPoint;
import net.minecraft.entity.player.EntityPlayer;

public class PropQueryPlayerSpot extends PropQueryPlayer {
    public WorldPoint spot;

    public PropQueryPlayerSpot(EntityPlayer player, String permKey)
    {
        super(player, permKey);
        spot = new WorldPoint(player);
    }

    public PropQueryPlayerSpot(EntityPlayer player, WorldPoint spot, String permKey)
    {
        super(player, permKey);
        this.spot = spot;
    }

}
