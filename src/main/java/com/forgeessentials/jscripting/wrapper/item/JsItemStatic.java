package com.forgeessentials.jscripting.wrapper.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.forgeessentials.jscripting.wrapper.world.JsBlock;

import cpw.mods.fml.common.registry.GameData;

public class JsItemStatic
{

    public JsItem getItem(String name)
    {
        Item item = GameData.getItemRegistry().getObject(name);
        return item == null ? null : JsItem.get(item);
    }

}
