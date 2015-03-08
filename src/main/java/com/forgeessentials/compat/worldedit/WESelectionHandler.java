package com.forgeessentials.compat.worldedit;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.selections.ISelectionProvider;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.forge.ForgeWorld;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;

public class WESelectionHandler implements ISelectionProvider {

    public WESelectionHandler()
    {
        OutputHandler.felog.info("WorldEdit selection provider started.");
    }

    @Override
    public Selection getSelection(EntityPlayerMP player)
    {
        try
        {
            LocalSession session = ForgeWorldEdit.inst.getSession(player);
            Region region = session.getSelection(session.getSelectionWorld());
            World world = ((ForgeWorld) session.getSelectionWorld()).getWorld();

            if (region instanceof CuboidRegion)
            {
                Vector wepos1 = ((CuboidRegion) region).getPos1();
                Vector wepos2 = ((CuboidRegion) region).getPos2();
                return new Selection(world, 
                        new Point(wepos1.getBlockX(), wepos1.getBlockY(), wepos1.getBlockZ()), 
                        new Point(wepos2.getBlockX(), wepos2.getBlockY(), wepos2.getBlockZ()));
            }
            else if (region instanceof Polygonal2DRegion)
            {
                Polygonal2DRegion polygon = (Polygonal2DRegion) region;
                return new Selection(world, 
                        new Point(polygon.getMinimumPoint().getBlockX(), polygon.getMinimumPoint().getBlockY(), polygon.getMinimumPoint().getBlockZ()), 
                        new Point(polygon.getMaximumPoint().getBlockX(), polygon.getMaximumPoint().getBlockY(), polygon.getMaximumPoint().getBlockZ()));
            }
            else if (region instanceof EllipsoidRegion)
            {
                EllipsoidRegion ellipsoid = (EllipsoidRegion) region;
                Vector c = ellipsoid.getCenter();
                Vector r = ellipsoid.getRadius();
                return new Selection(world, 
                        new Point(c.getBlockX() - r.getBlockX(), c.getBlockY() - r.getBlockY(), c.getBlockZ() - r.getBlockZ()), 
                        new Point(c.getBlockX() + r.getBlockX(), c.getBlockY() + r.getBlockY(), c.getBlockZ() + r.getBlockZ()));
            }
            else if (region instanceof CylinderRegion)
            {
                CylinderRegion cyl = (CylinderRegion) region;
                Vector c = cyl.getCenter();
                Vector2D r = cyl.getRadius();
                return new Selection(world, 
                        new Point(c.getBlockX() - r.getBlockX(), cyl.getMinimumY(), c.getBlockZ() - r.getBlockZ()), 
                        new Point(c.getBlockX() + r.getBlockX(), cyl.getMaximumY(), c.getBlockZ() + r.getBlockZ()));
            }
        }
        catch (IncompleteRegionException e)
        {
            /* do nothing */
        }
        return null;
    }

    @Override
    public void setDimension(EntityPlayerMP player, int dim)
    {
        LocalSession session = ForgeWorldEdit.inst.getSession(player);
        session.getRegionSelector(session.getSelectionWorld()).setWorld(ForgeWorldEdit.inst.getWorld(DimensionManager.getWorld(dim)));
    }

    @Override
    public void setStart(EntityPlayerMP player, Point start)
    {
        LocalSession session = ForgeWorldEdit.inst.getSession(player);
        session.getRegionSelector(session.getSelectionWorld()).selectPrimary(new Vector(start.getX(), start.getY(), start.getZ()), null);
    }

    @Override
    public void setEnd(EntityPlayerMP player, Point end)
    {
        LocalSession session = ForgeWorldEdit.inst.getSession(player);
        session.getRegionSelector(session.getSelectionWorld()).selectSecondary(new Vector(end.getX(), end.getY(), end.getZ()), null);
    }

}
