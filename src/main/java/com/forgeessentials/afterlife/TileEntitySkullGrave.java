package com.forgeessentials.afterlife;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySkull;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.mojang.authlib.GameProfile;

public class TileEntitySkullGrave extends TileEntitySkull
{

    public TileEntitySkullGrave()
    {
    }

    public TileEntitySkullGrave(GameProfile player)
    {
        if (player != null)
            func_152106_a(player);
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        WorldPoint point = new WorldPoint(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        Grave grave = Grave.graves.get(point);
        if (grave == null)
            return;

        if (grave.isProtected)
        {
            UserIdent owner = UserIdent.get(grave.owner);
            if (owner.hasPlayer())
            {
                // createPlayerSkull(owner.getPlayer(), worldObj, point.getX(), point.getY(), point.getZ());
                worldObj.setBlock(point.getX(), point.getY(), point.getZ(), Blocks.chest);
            }
        }
        else
        {
            grave.remove(true);
        }
    }

}
