package com.forgeessentials.jscripting.wrapper.mc.item;

import net.minecraft.entity.player.InventoryPlayer;

public class JsInventoryPlayer<T extends InventoryPlayer> extends JsInventory<T>
{

    /**
     * @tsd.ignore
     */
    public static <T extends InventoryPlayer> JsInventoryPlayer<T> get(T inventory)
    {
        return inventory == null ? null : new JsInventoryPlayer(inventory);
    }

    protected JsInventoryPlayer(T that)
    {
        super(that);
    }

    public JsItemStack getCurrentItem()
    {
        return JsItemStack.get(that.getCurrentItem());
    }

    public int getCurrentItemIndex()
    {
        return that.currentItem;
    }

    public void setCurrentItemIndex(int index)
    {
        that.currentItem = index;
    }

    public boolean addItemStackToInventory(JsItemStack itemStackIn) {
        return that.addItemStackToInventory(itemStackIn.getThat());
    }

    public boolean add(int index, final JsItemStack itemStack) {
        return that.add(index, itemStack.getThat());
    }

}
