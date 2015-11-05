package com.forgeessentials.compat.worldedit;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.util.output.LoggingHandler;
import com.forgeessentials.util.selections.ISelectionProvider;
import com.forgeessentials.util.selections.SelectionHandler;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.forge.ForgeWorld;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.CylinderRegionSelector;
import com.sk89q.worldedit.regions.selector.EllipsoidRegionSelector;
import com.sk89q.worldedit.regions.selector.ExtendingCuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector;

public class WESelectionHandler implements ISelectionProvider
{

    public WESelectionHandler()
    {
        LoggingHandler.felog.info("WorldEdit selection provider started.");
    }

    @Override
    public Selection getSelection(EntityPlayerMP player)
    {
        LocalSession session = ForgeWorldEdit.inst.getSession(player);
        if (session.getSelectionWorld() == null)
            return null;
        World world = ((ForgeWorld) session.getSelectionWorld()).getWorld();
        RegionSelector regionSelector = session.getRegionSelector(session.getSelectionWorld());

        if (regionSelector instanceof CuboidRegionSelector)
        {
            CuboidRegionSelector rs = (CuboidRegionSelector) regionSelector;
            try
            {
                Vector wepos1 = rs.getPrimaryPosition();
                Vector wepos2 = rs.isDefined() ? rs.getRegion().getPos2() : null;
                return new Selection(world, new Point(wepos1.getBlockX(), wepos1.getBlockY(), wepos1.getBlockZ()), wepos2 == null ? null : new Point(
                        wepos2.getBlockX(), wepos2.getBlockY(), wepos2.getBlockZ()));
            }
            catch (IncompleteRegionException e)
            {
                return null;
            }
        }
        else if (regionSelector instanceof Polygonal2DRegionSelector)
        {
            Polygonal2DRegionSelector rs = (Polygonal2DRegionSelector) regionSelector;
            try
            {
                Vector wepos1 = rs.isDefined() ? rs.getRegion().getMinimumPoint() : rs.getPrimaryPosition();
                Vector wepos2 = rs.isDefined() ? rs.getRegion().getMaximumPoint() : null;
                return new Selection(world, new Point(wepos1.getBlockX(), wepos1.getBlockY(), wepos1.getBlockZ()), wepos2 == null ? null : new Point(
                        wepos2.getBlockX(), wepos2.getBlockY(), wepos2.getBlockZ()));
            }
            catch (IncompleteRegionException e)
            {
                return null;
            }
        }
        else if (regionSelector instanceof EllipsoidRegionSelector)
        {
            EllipsoidRegionSelector rs = (EllipsoidRegionSelector) regionSelector;
            try
            {
                Vector wepos1 = rs.isDefined() ? rs.getRegion().getMinimumPoint() : rs.getPrimaryPosition();
                Vector wepos2 = rs.isDefined() ? rs.getRegion().getMaximumPoint() : null;
                return new Selection(world, new Point(wepos1.getBlockX(), wepos1.getBlockY(), wepos1.getBlockZ()), wepos2 == null ? null : new Point(
                        wepos2.getBlockX(), wepos2.getBlockY(), wepos2.getBlockZ()));
                // Vector c = ellipsoid.getCenter();
                // Vector r = ellipsoid.getRadius();
                // return new Selection(world,
                // new Point(c.getBlockX() - r.getBlockX(), c.getBlockY() - r.getBlockY(), c.getBlockZ() -
                // r.getBlockZ()),
                // new Point(c.getBlockX() + r.getBlockX(), c.getBlockY() + r.getBlockY(), c.getBlockZ() +
                // r.getBlockZ()));
            }
            catch (IncompleteRegionException e)
            {
                return null;
            }
        }
        else if (regionSelector instanceof CylinderRegionSelector)
        {
            CylinderRegionSelector rs = (CylinderRegionSelector) regionSelector;
            try
            {
                Vector wepos1 = rs.isDefined() ? rs.getRegion().getMinimumPoint() : rs.getPrimaryPosition();
                Vector wepos2 = rs.isDefined() ? rs.getRegion().getMaximumPoint() : null;
                return new Selection(world, new Point(wepos1.getBlockX(), wepos1.getBlockY(), wepos1.getBlockZ()), wepos2 == null ? null : new Point(
                        wepos2.getBlockX(), wepos2.getBlockY(), wepos2.getBlockZ()));
                // Vector c = cyl.getCenter();
                // return new Selection(world,
                // new Point(c.getBlockX() - r.getBlockX(), cyl.getMinimumY(), c.getBlockZ() - r.getBlockZ()),
                // new Point(c.getBlockX() + r.getBlockX(), cyl.getMaximumY(), c.getBlockZ() + r.getBlockZ()));
            }
            catch (IncompleteRegionException e)
            {
                return null;
            }
        }
        return null;
    }

    @Override
    public void setDimension(EntityPlayerMP player, int dim)
    {
        LocalSession session = ForgeWorldEdit.inst.getSession(player);
        ForgeWorld world = ForgeWorldEdit.inst.getWorld(DimensionManager.getWorld(dim));
        session.getRegionSelector(world).setWorld(world);
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

    @Override
    public void select(EntityPlayerMP player, int dimension, AreaBase area)
    {
        LocalSession session = ForgeWorldEdit.inst.getSession(player);
        ForgeWorld world = ForgeWorldEdit.inst.getWorld(DimensionManager.getWorld(dimension));
        CuboidRegionSelector selector;
        if (session.getRegionSelector(world) instanceof ExtendingCuboidRegionSelector)
            selector = new ExtendingCuboidRegionSelector(world);
        else
            selector = new CuboidRegionSelector(world);
        selector.selectPrimary(new Vector(area.getLowPoint().getX(), area.getLowPoint().getY(), area.getLowPoint().getZ()), null);
        selector.selectSecondary(new Vector(area.getHighPoint().getX(), area.getHighPoint().getY(), area.getHighPoint().getZ()), null);
        session.setRegionSelector(world, selector);
        SelectionHandler.sendUpdate(player);
    }

}
