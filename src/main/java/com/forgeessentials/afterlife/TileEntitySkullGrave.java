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
        WorldPoint point = new WorldPoint(world, pos.getX(), pos.getY(), pos.getZ());
        Grave grave = Grave.graves.get(point);
        if (grave == null)
            return;

        if (grave.isProtected)
        {
            UserIdent owner = UserIdent.get(grave.owner);
            if (owner.hasPlayer())
            {
                // createPlayerSkull(owner.getPlayer(), world, point.getX(), point.getY(), point.getZ());
                world.setBlockState(pos, Blocks.CHEST.getDefaultState());
            }
        }
        else
        {
            grave.remove(true);
        }
    }

}
