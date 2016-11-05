package net.minecraftforge.fe.event.player;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * NB: Forge PlayerInteractEvent should be re-implemented to include Post stage
 */
public class PlayerPostInteractEvent extends PlayerEvent
{

    public final World world;

    public final ItemStack stack;

    public final Block block;

    public final int x, y, z, side;

    public final float hitX, hitY, hitZ;

    protected PlayerPostInteractEvent(EntityPlayer player, World world, Block block, ItemStack stack, int x, int y, int z, int side, float hitX, float hitY,
            float hitZ)
    {
        super(player);
        this.world = world;
        this.block = block;
        this.stack = stack;
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
    }

    public PlayerPostInteractEvent(EntityPlayer player, World world, ItemStack stack, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        this(player, world, null, stack, x, y, z, side, hitX, hitY, hitZ);
    }

    public PlayerPostInteractEvent(EntityPlayer player, World world, Block block, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        this(player, world, block, null, x, y, z, side, hitX, hitY, hitZ);
    }

}
