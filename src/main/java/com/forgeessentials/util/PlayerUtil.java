package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.RayTraceResult;

public abstract class PlayerUtil {

	/**
	 * Swaps the player's inventory with the one provided and returns the old.
	 * 
	 * @param player
	 * @param newItems
	 * @return
	 */
	public static List<ItemStack> swapInventory(PlayerEntity player, List<ItemStack> newItems) {
		List<ItemStack> oldItems = new ArrayList<>();
		for (int slotIdx = 0; slotIdx < player.inventory.getContainerSize(); slotIdx++) {
			oldItems.add(player.inventory.getItem(slotIdx));
			if (newItems != null && slotIdx < newItems.size())
				player.inventory.setItem(slotIdx, newItems.get(slotIdx));
			else
				player.inventory.setItem(slotIdx, ItemStack.EMPTY);
		}
		return oldItems;
	}

	/**
	 * Give player the item stack or drop it if his inventory is full
	 * 
	 * @param player
	 * @param item
	 */
	public static void give(PlayerEntity player, ItemStack item) {
		ItemEntity entityitem = player.drop(item, false);
		if(entityitem!=null) {
			entityitem.setNoPickUpDelay();
			entityitem.setOwner(player.getUUID());
		}
	}

	/**
	 * Apply potion effects to the player
	 * 
	 * @param player
	 * @param effectString Comma separated list of id:duration:amplifier or
	 *                     id:duration tuples
	 */
	public static void applyPotionEffects(PlayerEntity player, String effectString) {
		String[] effects = effectString.replaceAll("\\s", "").split(","); // example = 9:5:0
		for (String poisonEffect : effects) {
			String[] effectValues = poisonEffect.split(":");
			if (effectValues.length < 2) {
				// LoggingHandler.felog.warn("Too few arguments for potion effects");
			} else if (effectValues.length > 3) {
				LoggingHandler.felog.warn("Too many arguments for potion effects");
			} else {
				try {
					int potionID = Integer.parseInt(effectValues[0]);
					int effectDuration = Integer.parseInt(effectValues[1]);
					int amplifier = 0;
					if (effectValues.length == 3)
						amplifier = Integer.parseInt(effectValues[2]);
					if (Effect.byId(potionID) == null) {
						LoggingHandler.felog.warn("Invalid potion ID {}", potionID);
						continue;
					}
					player.addEffect(new EffectInstance(Effect.byId(potionID), effectDuration * 20, amplifier, false,
							true, true));
				} catch (NumberFormatException e) {
					LoggingHandler.felog.warn("Invalid potion ID:duration:amplifier data.");
				}
			}
		}
	}

	/**
	 * Get the player persisted NBT tag
	 * 
	 * @param player
	 * @return
	 */
	public static CompoundNBT getPersistedTag(PlayerEntity player, boolean createIfMissing) {
		CompoundNBT tag = player.getPersistentData();
		if (createIfMissing)// ?
			player.getPersistentData();
		return tag;
	}

	/* ------------------------------------------------------------ */

	/**
	 * Get player's looking-at spot.
	 *
	 * @param player
	 * @return The position as a MovingObjectPosition Null if not existent.
	 */
	public static RayTraceResult getPlayerLookingSpot(PlayerEntity player) {
		if (player instanceof PlayerEntity)
			return getPlayerLookingSpot(player,
					player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue());
		else
			return getPlayerLookingSpot(player, 5.0);
	}

	/**
	 * Get player's looking spot.
	 *
	 * @param player
	 * @param maxDistance Keep max distance to 5.
	 * @return The position as a MovingObjectPosition Null if not existent.
	 */
	public static RayTraceResult getPlayerLookingSpot(PlayerEntity player, double maxDistance) {
		return player.pick(maxDistance, 1.0F, true);
	}

}
