package com.forgeessentials.jscripting.wrapper.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameData;

import com.forgeessentials.jscripting.wrapper.JsWrapper;

public class JsItem extends JsWrapper<Item>
{
    private static Map<Item, JsItem> cache = new HashMap<>();

    private JsItem(Item that)
    {
        super(that);
    }

    public String getName()
    {
        return GameData.getItemRegistry().getNameForObject(that).toString();
    }

    public static JsItem get(Item item)
    {
        if (!cache.containsKey(item))
            cache.put(item, new JsItem(item));
        return cache.get(item);
    }

}
