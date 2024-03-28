package com.forgeessentials.afterlife;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.mojang.authlib.GameProfile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntitySkullGrave extends SkullBlockEntity
{
    public TileEntitySkullGrave(BlockPos p_155731_, BlockState p_155732_)
    {
        super(p_155731_, p_155732_);
    }

    @Override
    public void setOwner(GameProfile player)
    {
        if (player != null)
            super.setOwner(player);
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
