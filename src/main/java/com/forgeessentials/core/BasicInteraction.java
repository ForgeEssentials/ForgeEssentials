package com.forgeessentials.core;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.world.IInteractionObject;

public abstract class BasicInteraction extends InventoryBasic implements IInteractionObject
{
    public BasicInteraction(String p_i1561_1_, boolean p_i1561_2_, IInventory source)
    {
        super(p_i1561_1_, p_i1561_2_, ((source.getSizeInventory() - 1) / 9 + 1) * 9);
        for (int i = 0; i < source.getSizeInventory(); i++)
        {
            this.setInventorySlotContents(i, source.getStackInSlot(i));
        }
    }
}
