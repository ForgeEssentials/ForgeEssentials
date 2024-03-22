package com.forgeessentials.util;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.ForgeEssentials;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public abstract class WorldUtil
{

    /**
     * Checks if the blocks from [x,y,z] to [x,y+h-1,z] are either air or replacable
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @param h
     * @return y value
     */
    public static boolean isFree(Level world, int x, int y, int z, int h)
    {
        int testedH = 0;
        for (int i = 0; i < h; i++)
        {
            Block block = world.getBlockState(new BlockPos(x, y + i, z)).getBlock();
            if (block.isPossibleToRespawnInThis())
                testedH++;
        }
        return testedH == h;
    }

    /**
     * Allow a block to be replaced if it Rock and is not a tile entity
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param h
     * @return y value
     */
    public static boolean isSafeToReplace(Level world, int x, int y, int z, int h, boolean replaceRock)
    {
        int testedH = 0;
        for (int i = 0; i < h; i++)
        {
            BlockPos pos = new BlockPos(x, y + i, z);
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            float hardness = block.getExplosionResistance();// .getBlockHardness(state, world, pos);
            boolean replaceable = replaceRock && (state.getMaterial() == Material.STONE && hardness >= 0
                    && hardness <= 3 && world.getBlockEntity(pos) == null);
            if (block.isPossibleToRespawnInThis() || replaceable)
            {
                testedH++;
            }
        }

        return testedH == h;
    }

    /**
     * Returns a free spot of height h in the world at the coordinates [x,z] near y. If the blocks at [x,y,z] are free, it returns the next location that is on the ground. If the
     * blocks at [x,y,z] are not free, it goes up until it finds a free spot.
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @param h
     * @return y value
     */
    public static int placeInWorld(Level world, int x, int y, int z, int h, boolean replaceRock)
    {
        if (y >= 0 && isSafeToReplace(world, x, y, z, h, false))
        {
            while (isSafeToReplace(world, x, y - 1, z, h, false) && y > 0)
                y--;
        }
        else
        {
            if (!ForgeEssentials.isCubicChunksInstalled)
            {
                if (y < 0)
                    y = 0;
            }
            y++;
            while (y + h < world.getHeight() && !isSafeToReplace(world, x, y, z, h, replaceRock))
                y++;
        }
        if (y == 0)
            y = world.getHeight() - h;
        return y;
    }

    /**
     * Returns a free spot of height 2 in the world at the coordinates [x,z] near y. If the blocks at [x,y,z] are free, it returns the next location that is on the ground. If the
     * blocks at [x,y,z] are not free, it goes up until it finds a free spot.
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @return y value
     */
    public static int placeInWorld(Level world, int x, int y, int z)
    {
        return placeInWorld(world, x, y, z, 2, false);
    }

    public static WorldPoint placeInWorld(WorldPoint p)
    {
        return p.setY(placeInWorld(p.getWorld(), p.getX(), p.getY(), p.getZ()));
    }

    public static void placeInWorld(Player player)
    {
        WorldPoint p = placeInWorld(new WorldPoint(player));
        player.setPos(p.getX() + 0.5, p.getY(), p.getZ() + 0.5);
    }

    /* ------------------------------------------------------------ */

}
