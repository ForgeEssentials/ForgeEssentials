package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.registries.ForgeRegistries;

import com.forgeessentials.util.output.LoggingHandler;

public abstract class PlayerUtil
{

    /**
     * Swaps the player's inventory with the one provided and returns the old.
     * 
     * @param player
     * @param newItems
     * @return
     */
    public static List<ItemStack> swapInventory(PlayerEntity player, List<ItemStack> newItems)
    {
        List<ItemStack> oldItems = new ArrayList<>();
        for (int slotIdx = 0; slotIdx < player.inventory.getContainerSize(); slotIdx++)
        {
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
    public static void give(PlayerEntity player, ItemStack item)
    {
        ItemEntity entityitem = player.drop(item, false);
        entityitem.setNoPickUpDelay();
        entityitem.setOwner(player.getUUID());
    }

    /**
     * Apply potion effects to the player
     * 
     * @param player
     * @param effectString
     *            Comma separated list of id:duration:amplifier or id:duration tuples
     */
    public static void applyPotionEffects(PlayerEntity player, String effectString)
    {
        String[] effects = effectString.replaceAll("\\s", "").split(","); // example = 9:5:0
        for (String poisonEffect : effects)
        {
            String[] effectValues = poisonEffect.split(":");
            if (effectValues.length < 2)
            {
                // LoggingHandler.felog.warn("Too few arguments for potion effects");
            }
            else if (effectValues.length > 3)
            {
                LoggingHandler.felog.warn("Too many arguments for potion effects");
            }
            else
            {
                try
                {
                    int potionID = Integer.parseInt(effectValues[0]);
                    int effectDuration = Integer.parseInt(effectValues[1]);
                    int amplifier = 0;
                    if (effectValues.length == 3)
                        amplifier = Integer.parseInt(effectValues[2]);
                    if (ForgeRegistries.POTIONS.containsValue(potionID) == null)
                    {
                        LoggingHandler.felog.warn("Invalid potion ID {}", potionID);
                        continue;
                    }
                    //player.addEffect(null);
                    player.addPotionEffect(new Effect(Potion.getPotionById(potionID), effectDuration * 20, amplifier));
                }
                catch (NumberFormatException e)
                {
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
    public static CompoundNBT getPersistedTag(PlayerEntity player, boolean createIfMissing)
    {
    	CompoundNBT tag = player.getEntityData().getCompoundTag(PlayerEntity.PERSISTED_NBT_TAG);
        if (createIfMissing)
            player.getEntityData().set(PlayerEntity.PERSISTED_NBT_TAG, tag);
        return tag;
    }

    /* ------------------------------------------------------------ */

    /**
     * Get player's looking-at spot.
     *
     * @param player
     * @return The position as a MovingObjectPosition Null if not existent.
     */
    public static RayTraceResult getPlayerLookingSpot(PlayerEntity player)
    {
        if (player instanceof PlayerEntity)
            return getPlayerLookingSpot(player, 5);//setting to 5 since i can't find reach distance for EntityPlayer
        else
            return getPlayerLookingSpot(player, 5);
    }

    /**
     * Get player's looking spot.
     *
     * @param player
     * @param maxDistance
     *            Keep max distance to 5.
     * @return The position as a MovingObjectPosition Null if not existent.
     */
    public static RayTraceResult getPlayerLookingSpot(PlayerEntity player, double maxDistance)
    {
        Vector3d lookAt = player.getViewVector(1);
        Vector3d playerPos = new Vector3d(player.position().x, player.position().y /*+ (player.getEyeHeight() - player.getEyeHeight())*/, player.position().z);
        Vector3d pos1 = playerPos.add(0, player.getEyeHeight(), 0);
        Vector3d pos2 = pos1.add(lookAt.x * maxDistance, lookAt.y * maxDistance, lookAt.z * maxDistance);
        return player.world.rayTraceBlocks(pos1, pos2);
    }

}
