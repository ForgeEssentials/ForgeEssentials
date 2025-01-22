package net.minecraftforge.fe.event.player;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * NB: Forge PlayerInteractEvent should be re-implemented to include Post stage
 */
public class PlayerPostInteractEvent extends PlayerEvent
{

    public final World world;

    public final ItemStack stack;

    public final IBlockState block;

    public final BlockPos pos;

    public final EnumFacing side;

    public final float hitX, hitY, hitZ;

    protected PlayerPostInteractEvent(EntityPlayer player, World world, IBlockState block, ItemStack stack, BlockPos pos, EnumFacing side, float hitX, float hitY,
            float hitZ)
    {
        super(player);
        this.world = world;
        this.block = block;
        this.stack = stack;
        this.pos = pos;
        this.side = side;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
    }

    public PlayerPostInteractEvent(EntityPlayer player, World world, ItemStack stack, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        this(player, world, null, stack, pos, side, hitX, hitY, hitZ);
    }

    public PlayerPostInteractEvent(EntityPlayer player, World world, IBlockState block, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        this(player, world, block, null, pos, side, hitX, hitY, hitZ);
    }

}
