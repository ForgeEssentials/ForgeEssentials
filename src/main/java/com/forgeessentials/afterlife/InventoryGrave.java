package com.forgeessentials.afterlife;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;

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
        super(45);
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
        super.stopOpen(player);
        if (grave.inventory.isEmpty())
        {
            WorldPoint point = grave.point;
            grave.remove(false);
            point.getWorld().removeBlock(point.getBlockPos(), false);
        }
    }

    public ITextComponent getDisplayName()
    {
        return new StringTextComponent(name);
    }

}
