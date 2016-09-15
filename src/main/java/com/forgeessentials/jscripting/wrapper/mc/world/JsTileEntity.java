package com.forgeessentials.jscripting.wrapper.mc.world;

import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.item.JsInventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

public class JsTileEntity<T extends TileEntity> extends JsWrapper<T>
{
    protected JsInventory<?> inventory;

    public JsTileEntity(T that)
    {
        super(that);
    }

    public JsInventory<?> getInventory()
    {
        if (!(that instanceof IInventory))
            return null;
        if (inventory == null)
            inventory = JsInventory.get((IInventory) that);
        return inventory;
    }

}
