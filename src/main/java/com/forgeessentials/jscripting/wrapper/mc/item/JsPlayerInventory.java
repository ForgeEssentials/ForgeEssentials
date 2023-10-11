package com.forgeessentials.jscripting.wrapper.mc.item;

import net.minecraft.entity.player.PlayerInventory;

public class JsPlayerInventory<T extends PlayerInventory> extends JsInventory<T>
{

    /**
     * @tsd.ignore
     */
    public static <T extends PlayerInventory> JsPlayerInventory<T> get(T inventory)
    {
        return inventory == null ? null : new JsPlayerInventory<>(inventory);
    }

    protected JsPlayerInventory(T that)
    {
        super(that);
    }

}
