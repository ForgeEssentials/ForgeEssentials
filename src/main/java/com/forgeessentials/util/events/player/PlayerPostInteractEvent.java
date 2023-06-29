package com.forgeessentials.util.events.player;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.b3d.B3DModel.Face;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * NB: Forge PlayerInteractEvent should be re-implemented to include Post stage
 */
public class PlayerPostInteractEvent extends PlayerEvent {

	public final World world;

	public final ItemStack stack;

	public final BlockState block;

	public final BlockPos pos;

	public final Face side;

	public final float hitX, hitY, hitZ;

	protected PlayerPostInteractEvent(PlayerEntity player, World world, BlockState block, ItemStack stack, BlockPos pos,
			Face side, float hitX, float hitY, float hitZ) {
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

	public PlayerPostInteractEvent(PlayerEntity player, World world, ItemStack stack, BlockPos pos, Face side,
			float hitX, float hitY, float hitZ) {
		this(player, world, null, stack, pos, side, hitX, hitY, hitZ);
	}

	public PlayerPostInteractEvent(PlayerEntity player, World world, BlockState block, BlockPos pos, Face side,
			float hitX, float hitY, float hitZ) {
		this(player, world, block, null, pos, side, hitX, hitY, hitZ);
	}

}
