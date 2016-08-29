package com.forgeessentials.jscripting.wrapper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

public class JsItemStatic
{

    public JsItem getItem(String name)
    {
        Item item = GameData.getItemRegistry().getObject(new ResourceLocation(name));
        return item == null ? null : JsItem.get(item);
    }

    public JsItemStack createItemStack(JsBlock block, int stackSize)
    {
        return new JsItemStack(new ItemStack(block.getThat(), stackSize));
    }

    public JsItemStack createItemStack(JsBlock block, int stackSize, int damage)
    {
        return new JsItemStack(new ItemStack(block.getThat(), stackSize, damage));
    }

    public JsItemStack createItemStack(JsItem item, int stackSize)
    {
        return new JsItemStack(new ItemStack(item.getThat(), stackSize));
    }

    public JsItemStack createItemStack(JsItem item, int stackSize, int damage)
    {
        return new JsItemStack(new ItemStack(item.getThat(), stackSize, damage));
    }

}
