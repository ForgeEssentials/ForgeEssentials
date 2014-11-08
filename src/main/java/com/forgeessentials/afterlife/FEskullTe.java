package com.forgeessentials.afterlife;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.world.World;

import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.WorldPoint;

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

        WorldPoint point = new WorldPoint(this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord);
        Grave grave = ModuleAfterlife.instance.deathchest.gravemap.get(point.toString());
        if (grave == null)
        {
            return;
        }
        if (grave.protEnable)
        {
            UserIdent owner = new UserIdent(grave.owner);
            if (owner.hasPlayer())
                createPlayerSkull(owner.getPlayer(), worldObj, point.getX(), point.getY(), point.getZ());
        }
        else
        {
            ModuleAfterlife.instance.deathchest.removeGrave(grave, true);
        }
    }
}
