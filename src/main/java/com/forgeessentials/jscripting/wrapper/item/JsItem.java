package com.forgeessentials.jscripting.wrapper.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;

import com.forgeessentials.jscripting.wrapper.JsWrapper;

import cpw.mods.fml.common.registry.GameData;

public class JsItem extends JsWrapper<Item>
{

    private static Map<Item, JsItem> cache = new HashMap<>();

    public static JsItem get(String name)
    {
        Item item = GameData.getItemRegistry().getObject(name);
        return item == null ? null : JsItem.get(item);
    }

    /**
     * @tsd.ignore
     */
    public static JsItem get(Item item)
    {
        if (!cache.containsKey(item))
            cache.put(item, new JsItem(item));
        return cache.get(item);
    }

    private JsItem(Item that)
    {
        super(that);
    }

    public String getName()
    {
        return GameData.getItemRegistry().getNameForObject(that);
    }

}
