package com.forgeessentials.compat.worldedit;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.util.ISelectionProvider;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.EllipsoidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import net.minecraft.entity.player.EntityPlayerMP;

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

    // these methods don't do anything, because in this case the selection is read-only

    @Override
    public void setPoint1(EntityPlayerMP player, Point point){}

    @Override
    public void setPoint2(EntityPlayerMP player, Point point){}

    public Point[] getPoints(EntityPlayerMP player)
    {
        Point[] points = new Point[2];

        // the following code is a modified version of WorldEdit Bukkit's selection sharing API.
        LocalSession session = ForgeWorldEdit.inst.getSession(player);
        RegionSelector selector = session.getRegionSelector(ForgeWorldEdit.inst.getWorld(player.worldObj));

        try
        {
            Region region = selector.getRegion();
            
            //World world = session.getSelectionWorld();
            
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
                points[0] = new Point(polygon.getMinimumPoint().getBlockX(), polygon.getMinimumPoint().getBlockY(), polygon.getMinimumPoint().getBlockZ());
                points[1] = new Point(polygon.getMaximumPoint().getBlockX(), polygon.getMaximumPoint().getBlockY(), polygon.getMaximumPoint().getBlockZ());
                return points;
            }
            else if (region instanceof EllipsoidRegion)
            {
                EllipsoidRegion ellipsoid = (EllipsoidRegion) region;
                Vector c = ellipsoid.getCenter();
                Vector r = ellipsoid.getRadius();
                points[0] = new Point(c.getBlockX() - r.getBlockX(), c.getBlockY() - r.getBlockY(), c.getBlockZ() - r.getBlockZ());
                points[1] = new Point(c.getBlockX() + r.getBlockX(), c.getBlockY() + r.getBlockY(), c.getBlockZ() + r.getBlockZ());
                return points;
            }
            else if (region instanceof CylinderRegion)
            {
                CylinderRegion cyl = (CylinderRegion) region;
                Vector c = cyl.getCenter();
                Vector2D r = cyl.getRadius();
                points[0] = new Point(c.getBlockX() - r.getBlockX(), cyl.getMinimumY(), c.getBlockZ() - r.getBlockZ());
                points[1] = new Point(c.getBlockX() + r.getBlockX(), cyl.getMaximumY(), c.getBlockZ() + r.getBlockZ());
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
