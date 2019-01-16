package com.forgeessentials.afterlife;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

import com.forgeessentials.api.UserIdent;

public class InventoryGrave extends InventoryBasic
{

    private Grave grave;

    public InventoryGrave(Grave grave)
    {
        super(UserIdent.get(grave.owner).getUsername() + "'s grave.", false, Math.min(36, ((grave.inventory.size() - 1) / 9 + 1) * 9));
        this.grave = grave;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
        grave.setOpen(true);
        for (int i = 0; i < getSizeInventory(); i++)
            setInventorySlotContents(i, grave.inventory.size() > 0 ? grave.inventory.remove(0) : ItemStack.EMPTY);
        super.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
        for (int i = 0; i < getSizeInventory(); i++)
        {
            ItemStack is = getStackInSlot(i);
            if (is != ItemStack.EMPTY)
                grave.inventory.add(is);
        }
        grave.setOpen(false);
        if (grave.inventory.isEmpty())
            grave.remove(false);
        super.closeInventory(player);
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return true;
    }

}
