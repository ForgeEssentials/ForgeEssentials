package com.forgeessentials.worldedit.compat;

import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.util.AreaSelector.Point;
import com.forgeessentials.util.FunctionHelper;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.world.World;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.EnumSet;

public class SelectionSyncHandler implements IScheduledTickHandler {

    private int syncInterval;

    public SelectionSyncHandler(int interval)
    {
        syncInterval = interval;
    }

    @Override
    public String getLabel()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void tickEnd(EnumSet<TickType> arg0, Object... arg1)
    {
        // the following code is a modified version of WorldEdit Bukkit's selection sharing API.
        for (String player : MinecraftServer.getServer().getAllUsernames())
        {
            EntityPlayerMP playermp = FunctionHelper.getPlayerForName(player);
            LocalSession session = ForgeWorldEdit.inst.getSession(playermp);
            RegionSelector selector = session.getRegionSelector(ForgeWorldEdit.inst.getWorld(playermp.worldObj));

            try
            {
                Region region = selector.getRegion();
                World world = session.getSelectionWorld();

                PlayerInfo info = PlayerInfo.getPlayerInfo(player);

                if (region instanceof CuboidRegion)
                {
                    Vector wepos1 = ((CuboidRegion) region).getPos1();
                    Vector wepos2 = ((CuboidRegion) region).getPos2();
                    Point fepos1 = new Point(wepos1.getBlockX(), wepos1.getBlockY(), wepos1.getBlockZ());
                    Point fepos2 = new Point(wepos2.getBlockX(), wepos2.getBlockY(), wepos2.getBlockZ());

                    info.setPoint1(fepos1);
                    info.setPoint2(fepos2);
                    return;
                }
                else if (region instanceof Polygonal2DRegion)
                {
                    Polygonal2DRegion polygon = (Polygonal2DRegion) region;
                    Point fepos1 = new Point(polygon.getMinimumPoint().getBlockX(), polygon.getMinimumPoint().getBlockY(),
                            polygon.getMinimumPoint().getBlockZ());
                    Point fepos2 = new Point(polygon.getMaximumPoint().getBlockX(), polygon.getMaximumPoint().getBlockY(),
                            polygon.getMaximumPoint().getBlockZ());
                    info.setPoint1(fepos1);
                    info.setPoint2(fepos2);
                    return;
                }
                else
                {
                    return;
                }
            }
            catch (IncompleteRegionException e)
            {
                return;
            }

        }
    }

    @Override
    public void tickStart(EnumSet<TickType> arg0, Object... arg1)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public EnumSet<TickType> ticks()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int nextTickSpacing()
    {
        // TODO Auto-generated method stub
        return syncInterval;
    }

}
