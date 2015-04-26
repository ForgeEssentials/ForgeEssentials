package com.forgeessentials.afterlife;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.world.World;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;

public class FEskullTe extends TileEntitySkull {

    public FEskullTe(EntityPlayer player)
    {
        // Set player profile
        func_152106_a(player.getGameProfile());
    }

    public static FEskullTe createPlayerSkull(EntityPlayer player, World world, int x, int y, int z)
    {
        FEskullTe skull = new FEskullTe(player);
        world.setBlock(x, y, z, Blocks.skull, 1, 1);
        world.setTileEntity(x, y, z, skull);
        return skull;
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        WorldPoint point = new WorldPoint(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        Grave grave = Grave.graves.get(point.toString());
        if (grave == null)
            return;
        
        if (grave.isProtected)
        {
            UserIdent owner = new UserIdent(grave.owner);
            if (owner.hasPlayer())
            {
                //createPlayerSkull(owner.getPlayer(), worldObj, point.getX(), point.getY(), point.getZ());
                worldObj.setBlock(point.getX(), point.getY(), point.getZ(), Blocks.chest);
            }
        }
        else
        {
            grave.remove(true);
        }
    }
    
}
