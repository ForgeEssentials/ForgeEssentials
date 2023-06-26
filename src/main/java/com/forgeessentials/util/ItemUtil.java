package com.forgeessentials.util;

import net.minecraft.block.Block;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.output.logger.LoggingHandler;

public final class ItemUtil
{
    public static ITextComponent[] getText(SignTileEntity sign) {
        ITextComponent[] signT = ObfuscationReflectionHelper.getPrivateValue(SignTileEntity.class, sign, "field_145915_a");
        return signT;
    }
    public static void setText(SignTileEntity sign, ITextComponent[] text) {
        ObfuscationReflectionHelper.setPrivateValue(SignTileEntity.class, sign, text, "field_145915_a");
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
                LoggingHandler.felog.error(String.format("Item %s threw exception on getItemDamage", stack.getItem().getClass().getName()));
            return 0;
        }
    }

    public static boolean isItemFrame(HangingEntity entity)
    {
        return entity instanceof ItemFrameEntity;
    }

    public static boolean isSign(Block block)
    {
        return block instanceof WallSignBlock || block instanceof StandingSignBlock;
    }

    public static ITextComponent[] getSignText(WorldPoint point)
    {
        TileEntity te = point.getTileEntity();
        if (te instanceof SignTileEntity)
        {
            SignTileEntity sign = (SignTileEntity) te;
            ITextComponent[] imessage = ItemUtil.getText(sign);
            return imessage;
        }
        return null;
    }

    public static CompoundNBT getTagCompound(ItemStack itemStack)
    {
        CompoundNBT tag = itemStack.getTag();
        if (tag == null)
        {
            tag = new CompoundNBT();
            itemStack.setTag(tag);
        }
        return tag;
    }

    public static CompoundNBT getCompoundTag(CompoundNBT tag, String side)
    {
        CompoundNBT subTag = tag.getCompound(side);
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
