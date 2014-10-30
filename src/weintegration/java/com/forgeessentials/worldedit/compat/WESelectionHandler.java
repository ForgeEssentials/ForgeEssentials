package com.forgeessentials.worldedit.compat;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.util.selections.ISelectionProvider;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.Selection;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.world.World;

public class WESelectionHandler implements ISelectionProvider {

    @Override
    public Point getPoint1(EntityPlayerMP player)
    {
        Point[] points = getPoints(player);
        if (points != null)
            return points[0];
        return null;
    }

    @Override
    public Point getPoint2(EntityPlayerMP player)
    {
        Point[] points = getPoints(player);
        if (points != null)
            return points[1];
        return null;
    }

    @Override
    public Selection getSelection(EntityPlayerMP player)
    {
        Point[] points = getPoints(player);
        if (points != null)
            return new Selection(points[0], points[1]);
        return null;
    }

    public Point[] getPoints(EntityPlayerMP player)
    {
        Point[] points = new Point[2];

        // the following code is a modified version of WorldEdit Bukkit's selection sharing API.
        LocalSession session = ForgeWorldEdit.inst.getSession(player);
        RegionSelector selector = session.getRegionSelector(ForgeWorldEdit.inst.getWorld(player.worldObj));

        try
        {
            Region region = selector.getRegion();
            World world = session.getSelectionWorld();

            if (region instanceof CuboidRegion)
            {
                Vector wepos1 = ((CuboidRegion) region).getPos1();
                Vector wepos2 = ((CuboidRegion) region).getPos2();
                Point fepos1 = new Point(wepos1.getBlockX(), wepos1.getBlockY(), wepos1.getBlockZ());
                Point fepos2 = new Point(wepos2.getBlockX(), wepos2.getBlockY(), wepos2.getBlockZ());

                points[0] = fepos1;
                points[1] = fepos2;
                return points;
            }
            else if (region instanceof Polygonal2DRegion)
            {
                Polygonal2DRegion polygon = (Polygonal2DRegion) region;
                Point fepos1 = new Point(polygon.getMinimumPoint().getBlockX(), polygon.getMinimumPoint().getBlockY(), polygon.getMinimumPoint().getBlockZ());
                Point fepos2 = new Point(polygon.getMaximumPoint().getBlockX(), polygon.getMaximumPoint().getBlockY(), polygon.getMaximumPoint().getBlockZ());
                points[0] = fepos1;
                points[1] = fepos2;
                return points;
            }
            else
            {
                return null;
            }
        }
        catch (IncompleteRegionException e)
        {
            return null;
        }
    }

}
