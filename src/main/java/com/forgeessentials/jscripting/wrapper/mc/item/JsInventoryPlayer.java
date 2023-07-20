package com.forgeessentials.jscripting.wrapper.mc.item;

import net.minecraft.entity.player.PlayerInventory;

public class JsInventoryPlayer<T extends PlayerInventory> extends JsInventory<T>
{

    /**
     * @tsd.ignore
     */
    public static <T extends PlayerInventory> JsInventoryPlayer<T> get(T inventory)
    {
        return inventory == null ? null : new JsInventoryPlayer<>(inventory);
    }

    protected JsInventoryPlayer(T that)
    {
        super(that);
    }

}
