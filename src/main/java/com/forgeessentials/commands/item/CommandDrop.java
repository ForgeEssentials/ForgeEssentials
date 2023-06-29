package com.forgeessentials.commands.item;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class CommandDrop extends ForgeEssentialsCommandBuilder {

	public CommandDrop(boolean enabled) {
		super(enabled);
	}

	@Override
	public String getPrimaryAlias() {
		return "drop";
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public DefaultPermissionLevel getPermissionLevel() {
		return DefaultPermissionLevel.OP;
	}

	@Override
	public String getPermissionNode() {
		return ModuleCommands.PERM + ".drop";
	}

	@Override
	public LiteralArgumentBuilder<CommandSource> setExecution() {
		return baseBuilder.then(Commands.argument("pos", Vec3Argument.vec3())
				.then(Commands.argument("count", IntegerArgumentType.integer(0, 64))
						.then(Commands.argument("item", ItemArgument.item())
								.executes(CommandContext -> execute(CommandContext, "blank")))));
	}

	@Override
	public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException {
		Vector3d vector = Vec3Argument.getVec3(ctx, "pos");
		World world = null;
		int x = (int) vector.x;
		int y = (int) vector.y;
		int z = (int) vector.z;

		if (ctx.getSource().getEntity() instanceof ServerPlayerEntity) {
			world = ((Entity) ctx.getSource().getEntity()).level;
		} else {
			ICommandSource source = CommandUtils.GetSource(ctx.getSource());
			if (source instanceof MinecraftServer) {
				world = ((DedicatedServer) source).getLevel(World.OVERWORLD);
			}
		}
		BlockPos pos = new BlockPos(x, y, z);

		int count = IntegerArgumentType.getInteger(ctx, "count");
		int j = Math.min(new ItemStack(ItemArgument.getItem(ctx, "item").getItem(), count).getMaxStackSize(), count);
		ItemStack itemstack = ItemArgument.getItem(ctx, "item").createItemStack(j, false);
		ItemStack tmpStack;

		TileEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof IInventory) {
			IInventory inventory = (IInventory) tileEntity;
			for (int slot = 0; slot < inventory.getContainerSize(); ++slot) {
				itemstack.setCount(count);
				if (inventory.getItem(slot) == ItemStack.EMPTY) {
					inventory.setItem(slot, itemstack);
					count = 0;
					break;
				}

				if (inventory.getItem(slot).sameItemStackIgnoreDurability(itemstack)) {
					if (inventory.getItem(slot).getMaxStackSize() - inventory.getItem(slot).getCount() >= count) {
						tmpStack = inventory.getItem(slot);
						tmpStack.setCount(tmpStack.getCount() + count);
						count = 0;
						break;
					}

					count -= (inventory.getItem(slot).getMaxStackSize() - inventory.getItem(slot).getCount());
					inventory.getItem(slot).setCount(inventory.getItem(slot).getMaxStackSize());
				}
			}
		} else {
			ChatOutputHandler.chatError(ctx.getSource(), "No viable container found to put item in.");
			return Command.SINGLE_SUCCESS;
		}
		if (count > 0) {
			ChatOutputHandler.chatError(ctx.getSource(), "Not enough room for all the items.");
		}
		ChatOutputHandler.chatConfirmation(ctx.getSource(), "Items dropped into container.");
		return Command.SINGLE_SUCCESS;

	}

}