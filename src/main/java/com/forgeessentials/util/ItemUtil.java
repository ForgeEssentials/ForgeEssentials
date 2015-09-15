package com.forgeessentials.util;

import net.minecraft.item.ItemStack;

import com.forgeessentials.util.output.LoggingHandler;

import cpw.mods.fml.common.registry.GameData;

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
        String id = GameData.getItemRegistry().getNameForObject(itemStack.getItem());
        int itemDamage = getItemDamage(itemStack);
        if (itemDamage == 0 || itemDamage == 32767)
            return id;
        else
            return id + ":" + itemDamage;
    }

}
