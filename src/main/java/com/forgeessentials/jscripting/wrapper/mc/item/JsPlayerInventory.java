package com.forgeessentials.jscripting.wrapper.mc.item;

import net.minecraft.world.entity.player.Inventory;

public class JsPlayerInventory<T extends Inventory> extends JsInventory<T>
{

    /**
     * @tsd.ignore
     */
    public static <T extends Inventory> JsPlayerInventory<T> get(T inventory)
    {
        return inventory == null ? null : new JsPlayerInventory<>(inventory);
    }

    protected JsPlayerInventory(T that)
    {
        super(that);
    }

}
