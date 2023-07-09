package com.forgeessentials.afterlife;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.Blocks;
import net.minecraft.tileentity.SkullTileEntity;

public class TileEntitySkullGrave extends SkullTileEntity
{

    public TileEntitySkullGrave()
    {
    }

    public TileEntitySkullGrave(GameProfile player)
    {
        if (player != null)
            setOwner(player);
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();
        WorldPoint point = new WorldPoint(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
        Grave grave = Grave.graves.get(point);
        if (grave == null)
            return;

        if (grave.isProtected)
        {
            UserIdent owner = UserIdent.get(grave.owner);
            if (owner.hasPlayer())
            {
                // createPlayerSkull(owner.getPlayer(), world, point.getX(), point.getY(),
                // point.getZ());
                level.setBlockAndUpdate(worldPosition, Blocks.CHEST.defaultBlockState());
            }
        }
        else
        {
            grave.remove(true);
        }
    }

}
