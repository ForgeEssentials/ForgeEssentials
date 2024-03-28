package com.forgeessentials.util;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;


public final class ItemUtil
{
    public static Component[] getText(SignBlockEntity sign)
    {
        return ObfuscationReflectionHelper.getPrivateValue(SignBlockEntity.class, sign,
                "f_59720_"); // messages
    }

    public static void setText(SignBlockEntity sign, Component[] text)
    {
        ObfuscationReflectionHelper.setPrivateValue(SignBlockEntity.class, sign, text, "f_59720_"); // messages
    }

    public static int getItemDamage(ItemStack stack)
    {
        try
        {
            return stack.getDamageValue();
        }
        catch (Exception e)
        {
            if (stack.getItem() == null)
                LoggingHandler.felog.error("ItemStack item is null when checking getItemDamage");
            else
                LoggingHandler.felog.error(String.format("Item %s threw exception on getItemDamage",
                        stack.getItem().getClass().getName()));
            return 0;
        }
    }

    public static boolean isItemFrame(HangingEntity entity)
    {
        return entity instanceof ItemFrame;
    }

    public static boolean isSign(Block block)
    {
        return block instanceof WallSignBlock || block instanceof StandingSignBlock;
    }

    public static Component[] getSignText(WorldPoint point)
    {
        BlockEntity te = point.getTileEntity();
        if (te instanceof SignBlockEntity)
        {
            SignBlockEntity sign = (SignBlockEntity) te;
            return ItemUtil.getText(sign);
        }
        return null;
    }

    public static CompoundTag getTagCompound(ItemStack itemStack)
    {
        CompoundTag tag = itemStack.getTag();
        if (tag == null)
        {
            tag = new CompoundTag();
            itemStack.setTag(tag);
        }
        return tag;
    }

    public static CompoundTag getCompoundTag(CompoundTag tag, String side)
    {
        CompoundTag subTag = tag.getCompound(side);
        tag.put(side, subTag);
        return subTag;
    }

    public static String getItemName(Item item)
    {
        return ForgeRegistries.ITEMS.getKey(item).toString();
    }

    public static String getItemName(ItemStack itemstack)
    {
        return getItemName(itemstack.getItem());
    }
}
