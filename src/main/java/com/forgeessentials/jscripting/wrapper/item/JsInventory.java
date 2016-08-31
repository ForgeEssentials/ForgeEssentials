package com.forgeessentials.jscripting.wrapper.item;

import net.minecraft.inventory.IInventory;

import com.forgeessentials.jscripting.wrapper.JsWrapper;

public class JsInventory<T extends IInventory> extends JsWrapper<T>
{

    public JsInventory(T that)
    {
        super(that);
    }

    public JsItemStack getStackInSlot(int slot)
    {
        return new JsItemStack(that.getStackInSlot(slot));
    }

    public void setStackInSlot(int slot, JsItemStack stack)
    {
        that.setInventorySlotContents(slot, stack.getThat());
    }

    public boolean isStackValidForSlot(int slot, JsItemStack stack)
    {
        return that.isItemValidForSlot(slot, stack.getThat());
    }

    public int getSize()
    {
        return that.getSizeInventory();
    }

    public int getStackLimit()
    {
        return that.getInventoryStackLimit();
    }

    public String getName()
    {
        return that.getName();
    }

    public boolean hasCustomName()
    {
        return that.hasCustomName();
    }

}
