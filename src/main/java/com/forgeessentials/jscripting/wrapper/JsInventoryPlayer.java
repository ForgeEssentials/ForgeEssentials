package com.forgeessentials.jscripting.wrapper;

import net.minecraft.entity.player.InventoryPlayer;

public class JsInventoryPlayer<T extends InventoryPlayer> extends JsInventory<T>
{

    public JsInventoryPlayer(T that)
    {
        super(that);
    }

    public JsItemStack getCurrentItem() {
        return new JsItemStack(that.getCurrentItem());
    }

    public int getCurrentItemIndex() {
        return that.currentItem;
    }

    public void setCurrentItemIndex(int index) {
        that.currentItem = index;
    }

}
