package com.forgeessentials.jscripting.wrapper.mc.world;

import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.item.JsInventory;

import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;

public class JsTileEntity<T extends BlockEntity> extends JsWrapper<T>
{
    protected JsInventory<?> inventory;

    public JsTileEntity(T that)
    {
        super(that);
    }

    public JsInventory<?> getInventory()
    {
        if (!(that instanceof Container))
            return null;
        if (inventory == null)
            inventory = JsInventory.get((Container) that);
        return inventory;
    }

}
