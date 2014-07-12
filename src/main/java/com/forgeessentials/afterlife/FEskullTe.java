package com.forgeessentials.afterlife;

import com.forgeessentials.util.AreaSelector.WorldPoint;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntitySkull;

public class FEskullTe extends TileEntitySkull {
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
            this.worldObj.setBlock(point.x, point.y, point.z, Blocks.skull, 1, 1);
            FEskullTe te = new FEskullTe();
            te.func_152106_a(MinecraftServer.getServer().getConfigurationManager().func_152612_a(grave.owner).getGameProfile());
            this.worldObj.setTileEntity(point.x, point.y, point.z, te);
        }
        else
        {
            ModuleAfterlife.instance.deathchest.removeGrave(grave, true);
        }
    }
}
