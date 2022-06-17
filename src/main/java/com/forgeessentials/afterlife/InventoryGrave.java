package com.forgeessentials.afterlife;

import com.forgeessentials.api.UserIdent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class InventoryGrave extends Inventory
{

    private Grave grave;

    public InventoryGrave(Grave grave)
    {
        super(UserIdent.get(grave.owner).getUsername() + "'s grave.", false, Math.min(36, ((grave.inventory.size() - 1) / 9 + 1) * 9));
        this.grave = grave;
    }

    @Override
    public void openInventory(PlayerEntity player)
    {
        grave.setOpen(true);
        for (int i = 0; i < getSizeInventory(); i++)
            setInventorySlotContents(i, grave.inventory.size() > 0 ? grave.inventory.remove(0) : ItemStack.EMPTY);
        super.openInventory(player);
    }

    @Override
    public void closeInventory(PlayerEntity player)
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
    public boolean isUsableByPlayer(PlayerEntity player)
    {
        return true;
    }

}
