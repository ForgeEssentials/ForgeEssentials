package com.forgeessentials.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallSignBlock;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.output.LoggingHandler;

public final class ItemUtil
{

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

    public static String getItemIdentifier(ItemStack itemStack)
    {
        String id = itemStack.getDescriptionId();
        int itemDamage = getItemDamage(itemStack);
        if (itemDamage == 0 || itemDamage == 32767)
            return id;
        else
            return id + ":" + itemDamage;
    }

    public static boolean isItemFrame(HangingEntity entity)
    {
        return entity instanceof ItemFrameEntity;
    }

    public static boolean isSign(Block block)
    {
        return block instanceof WallSignBlock;
    }

    public static ITextComponent[] getSignText(WorldPoint point)
    {
        TileEntity te = point.getTileEntity();
        if (te instanceof SignTileEntity)
        {
            SignTileEntity sign = (SignTileEntity) te;
            return sign.signText;
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

}
