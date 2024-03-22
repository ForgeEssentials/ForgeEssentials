package com.forgeessentials.jscripting.wrapper.mc.item;

import java.util.HashMap;
import java.util.Map;

import com.forgeessentials.jscripting.wrapper.JsWrapper;

import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @tsd.static Item
 */
public class JsItem extends JsWrapper<Item>
{

    private static Map<Item, JsItem> cache = new HashMap<>();

    public static JsItem get(String name)
    {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
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
        return ForgeRegistries.ITEMS.getKey(that).toString();
    }

}
