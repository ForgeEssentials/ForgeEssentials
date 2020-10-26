package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

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
    public static List<ItemStack> swapInventory(EntityPlayerMP player, List<ItemStack> newItems)
    {
        List<ItemStack> oldItems = new ArrayList<>();
        for (int slotIdx = 0; slotIdx < player.inventory.getSizeInventory(); slotIdx++)
        {
            oldItems.add(player.inventory.getStackInSlot(slotIdx));
            if (newItems != null && slotIdx < newItems.size())
                player.inventory.setInventorySlotContents(slotIdx, newItems.get(slotIdx));
            else
                player.inventory.setInventorySlotContents(slotIdx, ItemStack.EMPTY);
        }
        return oldItems;
    }

    /**
     * Give player the item stack or drop it if his inventory is full
     * 
     * @param player
     * @param item
     */
    public static void give(EntityPlayer player, ItemStack item)
    {
        EntityItem entityitem = player.dropItem(item, false);
        entityitem.setNoPickupDelay();
        entityitem.setOwner(player.getName());
    }

    /**
     * Apply potion effects to the player
     * 
     * @param player
     * @param effectString
     *            Comma separated list of id:duration:amplifier or id:duration tuples
     */
    public static void applyPotionEffects(EntityPlayer player, String effectString)
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
                    if (Potion.REGISTRY.getObjectById(potionID) == null)
                    {
                        LoggingHandler.felog.warn("Invalid potion ID {}", potionID);
                        continue;
                    }
                    player.addPotionEffect(new net.minecraft.potion.PotionEffect(Potion.getPotionById(potionID), effectDuration * 20, amplifier));
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
    public static NBTTagCompound getPersistedTag(EntityPlayer player, boolean createIfMissing)
    {
        NBTTagCompound tag = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (createIfMissing)
            player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, tag);
        return tag;
    }

    /* ------------------------------------------------------------ */

    /**
     * Get player's looking-at spot.
     *
     * @param player
     * @return The position as a MovingObjectPosition Null if not existent.
     */
    public static RayTraceResult getPlayerLookingSpot(EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP)
            return getPlayerLookingSpot(player, ((EntityPlayerMP) player).interactionManager.getBlockReachDistance());
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
    public static RayTraceResult getPlayerLookingSpot(EntityPlayer player, double maxDistance)
    {
        Vec3d lookAt = player.getLook(1);
        Vec3d playerPos = new Vec3d(player.posX, player.posY + (player.getEyeHeight() - player.getDefaultEyeHeight()), player.posZ);
        Vec3d pos1 = playerPos.addVector(0, player.getEyeHeight(), 0);
        Vec3d pos2 = pos1.addVector(lookAt.x * maxDistance, lookAt.y * maxDistance, lookAt.z * maxDistance);
        return player.world.rayTraceBlocks(pos1, pos2);
    }

}
