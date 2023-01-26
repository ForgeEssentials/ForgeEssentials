package com.forgeessentials.jscripting.wrapper.mc.item;

import net.minecraft.inventory.Inventory;

public class JsInventoryPlayer<T extends Inventory> extends JsInventory<T>
{

    /**
     * @tsd.ignore
     */
    public static <T extends Inventory> JsInventoryPlayer<T> get(T inventory)
    {
        return inventory == null ? null : new JsInventoryPlayer<T>(inventory);
    }

    protected JsInventoryPlayer(T that)
    {
        super(that);
    }

}
