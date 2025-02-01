package com.forgeessentials.jscripting.wrapper.mc.item;

import net.minecraft.inventory.IInventory;

import com.forgeessentials.jscripting.wrapper.JsWrapper;

public class JsInventory<T extends IInventory> extends JsWrapper<T>
{

    /**
     * @tsd.ignore
     */
    public static <T extends IInventory> JsInventory<T> get(T inventory)
    {
        return inventory == null ? null : new JsInventory<>(inventory);
    }

    protected JsInventory(T that)
    {
        super(that);
    }

    public JsItemStack getStackInSlot(int slot)
    {
        return JsItemStack.get(that.getStackInSlot(slot));
    }

    public void setStackInSlot(int slot, JsItemStack stack)
    {
        that.setInventorySlotContents(slot, stack == null ? null : stack.getThat());
    }

    public boolean isStackValidForSlot(int slot, JsItemStack stack)
    {
        return that.isItemValidForSlot(slot, stack == null ? null : stack.getThat());
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
