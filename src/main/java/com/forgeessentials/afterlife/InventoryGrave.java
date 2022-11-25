package com.forgeessentials.afterlife;

import com.forgeessentials.api.UserIdent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class InventoryGrave extends Inventory
{

    private Grave grave;
    private String name;
    public InventoryGrave(Grave grave)
    {
        super(Math.min(36, ((grave.inventory.size() - 1) / 9 + 1) * 9));
        name = UserIdent.get(grave.owner).getUsername() + "'s grave.";
        this.grave = grave;
    }

    @Override
    public void startOpen(PlayerEntity player)
    {
        grave.setOpen(true);
        for (int i = 0; i < getContainerSize(); i++)
            setItem(i, grave.inventory.size() > 0 ? grave.inventory.remove(0) : ItemStack.EMPTY);
        super.startOpen(player);
    }

    @Override
    public void stopOpen(PlayerEntity player)
    {
        for (int i = 0; i < getContainerSize(); i++)
        {
            ItemStack is = getItem(i);
            if (is != ItemStack.EMPTY)
                grave.inventory.add(is);
        }
        grave.setOpen(false);
        if (grave.inventory.isEmpty())
            grave.remove(false);
        super.stopOpen(player);
    }

    public ITextComponent getDisplayName()
    {
        return new StringTextComponent(name);
    }

}
