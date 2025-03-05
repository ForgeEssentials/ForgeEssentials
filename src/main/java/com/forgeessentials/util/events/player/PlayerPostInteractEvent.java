package com.forgeessentials.util.events.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * NB: Forge PlayerInteractEvent should be re-implemented to include Post stage
 */
//TODO: Is this used anymore or needed?
public class PlayerPostInteractEvent extends PlayerEvent
{

    public final Level world;

    public final ItemStack stack;

    public final BlockState block;

    public final BlockPos pos;

    public final Direction side;

    public final float hitX, hitY, hitZ;

    protected PlayerPostInteractEvent(Player player, Level world, BlockState block, ItemStack stack, BlockPos pos,
            Direction side, float hitX, float hitY, float hitZ)
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

    public PlayerPostInteractEvent(Player player, Level world, ItemStack stack, BlockPos pos, Direction side,
            float hitX, float hitY, float hitZ)
    {
        this(player, world, null, stack, pos, side, hitX, hitY, hitZ);
    }

    public PlayerPostInteractEvent(Player player, Level world, BlockState block, BlockPos pos, Direction side,
            float hitX, float hitY, float hitZ)
    {
        this(player, world, block, null, pos, side, hitX, hitY, hitZ);
    }

}
