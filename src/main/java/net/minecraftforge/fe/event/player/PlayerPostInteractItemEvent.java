package net.minecraftforge.fe.event.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * NB: Forge PlayerInteractEvent should be re-implemented to include Post stage
 */
public class PlayerPostInteractItemEvent extends PlayerEvent
{
    public final World world;
    public final ItemStack stack;
    public final int x, y, z, side;
    public final float hitX, hitY, hitZ;

    public PlayerPostInteractItemEvent(EntityPlayer player, World world, ItemStack stack, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        super(player);
        this.world = world;
        this.stack = stack;
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
    }
}
