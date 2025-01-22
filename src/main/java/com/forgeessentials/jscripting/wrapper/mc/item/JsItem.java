package com.forgeessentials.jscripting.wrapper.mc.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

import com.forgeessentials.jscripting.wrapper.JsWrapper;

/**
 * @tsd.static Item
 */
public class JsItem extends JsWrapper<Item>
{

    private static Map<Item, JsItem> cache = new HashMap<>();

    public static JsItem get(String name)
    {
        Item item = GameData.getItemRegistry().getObject(new ResourceLocation(name));
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
        return GameData.getItemRegistry().getNameForObject(that).toString();
    }

}
