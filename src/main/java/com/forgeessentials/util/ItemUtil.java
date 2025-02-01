package com.forgeessentials.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.registry.GameData;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.output.LoggingHandler;

public final class ItemUtil
{

    public static int getItemDamage(ItemStack stack)
    {
        try
        {
            return stack.getItemDamage();
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
        String id = GameData.getItemRegistry().getNameForObject(itemStack.getItem()).toString();
        int itemDamage = getItemDamage(itemStack);
        if (itemDamage == 0 || itemDamage == 32767)
            return id;
        else
            return id + ":" + itemDamage;
    }

    public static boolean isItemFrame(EntityHanging entity)
    {
        return entity instanceof EntityItemFrame;
    }

    public static boolean isSign(Block block)
    {
        return block == Blocks.wall_sign;
    }

    public static IChatComponent[] getSignText(WorldPoint point)
    {
        TileEntity te = point.getTileEntity();
        if (te instanceof TileEntitySign)
        {
            TileEntitySign sign = (TileEntitySign) te;
            return sign.signText;
        }
        return null;
    }

    public static NBTTagCompound getTagCompound(ItemStack itemStack)
    {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null)
        {
            tag = new NBTTagCompound();
            itemStack.setTagCompound(tag);
        }
        return tag;
    }

    
    public static NBTTagCompound getCompoundTag(NBTTagCompound tag, String side)
    {
        NBTTagCompound subTag = tag.getCompoundTag(side);
        tag.setTag(side, subTag);
        return subTag;
    }

}
