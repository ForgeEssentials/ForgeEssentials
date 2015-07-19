package com.forgeessentials.afterlife;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.mojang.authlib.GameProfile;

public class FEskullTe extends TileEntitySkull
{

    public FEskullTe(GameProfile player)
    {
        // Set player profile
        if (player != null)
            setPlayerProfile(player);
    }

    public static FEskullTe createPlayerSkull(GameProfile player, World world, BlockPos pos)
    {
        FEskullTe skull = new FEskullTe(player);
        world.setBlockState(pos, Blocks.skull.getStateFromMeta(1), 1);
        world.setTileEntity(pos, skull);
        return skull;
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        WorldPoint point = new WorldPoint(worldObj, pos.getX(), pos.getY(), pos.getZ());
        Grave grave = Grave.graves.get(point.toString());
        if (grave == null)
            return;

        if (grave.isProtected)
        {
            UserIdent owner = UserIdent.get(grave.owner);
            if (owner.hasPlayer())
            {
                // createPlayerSkull(owner.getPlayer(), worldObj, point.getX(), point.getY(), point.getZ());
                worldObj.setBlockState(pos, Blocks.chest.getDefaultState());
            }
        }
        else
        {
            grave.remove(true);
        }
    }

}
