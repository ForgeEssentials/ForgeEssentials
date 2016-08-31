package com.forgeessentials.jscripting.wrapper.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

public class JsItemStatic
{

    public JsItem getItem(String name)
    {
        Item item = GameData.getItemRegistry().getObject(new ResourceLocation(name));
        return item == null ? null : JsItem.get(item);
    }

}
