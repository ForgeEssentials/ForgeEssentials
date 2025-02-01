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
        	setPlayerProfile(player);
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        WorldPoint point = new WorldPoint(this.worldObj, this.pos);
        Grave grave = Grave.graves.get(point);
        if (grave == null)
            return;

        if (grave.isProtected)
        {
            UserIdent owner = UserIdent.get(grave.owner);
            if (owner.hasPlayer())
            {
                // createPlayerSkull(owner.getPlayer(), worldObj, point.getX(), point.getY(), point.getZ());
                worldObj.setBlockState(point.getBlockPos(), Blocks.chest.getDefaultState());
            }
        }
        else
        {
            grave.remove(true);
        }
    }

}
