package com.forgeessentials.commands.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SeeablePlayerInventory implements IInventory
{
    public final PlayerEntity victim;

    public SeeablePlayerInventory(PlayerEntity player)
    {
        victim = player;
    }

    public boolean shouldGetFromSlot(int section)
    {
        return section >= 4 && section < 8;
    }

    @Override
    public void clearContent()
    {
        victim.inventory.clearContent();
    }

    @Override
    public int getContainerSize()
    {
        return 45;
    }

    @Override
    public boolean isEmpty()
    {
        return victim.inventory.isEmpty();
    }

    public int getSlot(int section)
    {
        if (section == 8)
        {
            return 40;
        }
        else if (section >= 0 && section <= 3)
        {
            return 39 - section;
        }
        else if (section >= 9 && section <= 35)
        {
            return section;
        }
        else if (section >= 36 && section <= 44)
        {
            return section - 36;
        }

        return -1;
    }

    @Override
    public ItemStack getItem(int section)
    {
        if (shouldGetFromSlot(section))
        {
            return ItemStack.EMPTY;
        }
        return getSlot(section) == -1 ? ItemStack.EMPTY : victim.inventory.getItem(getSlot(section));
    }

    @Override
    public ItemStack removeItem(int section, int number)
    {
        if (shouldGetFromSlot(section))
        {
            return ItemStack.EMPTY;
        }
        return getSlot(section) == -1 ? ItemStack.EMPTY : victim.inventory.removeItem(getSlot(section), number);
    }

    @Override
    public ItemStack removeItemNoUpdate(int section)
    {
        if (shouldGetFromSlot(section))
        {
            return ItemStack.EMPTY;
        }
        return getSlot(section) == -1 ? ItemStack.EMPTY : victim.inventory.removeItemNoUpdate(getSlot(section));
    }

    @Override
    public void setItem(int section, ItemStack stack)
    {
        if (shouldGetFromSlot(section))
        {
            return;
        }
        if (getSlot(section) != -1)
        {
            victim.inventory.setItem(getSlot(section), stack);
            setChanged();
        }
    }

    @Override
    public boolean canPlaceItem(int section, ItemStack stack)
    {
        if (shouldGetFromSlot(section))
        {
            return false;
        }

        int slot = getSlot(section);
        return slot != -1 && victim.inventory.canPlaceItem(slot, stack);
    }

    @Override
    public int getMaxStackSize()
    {
        return victim.inventory.getMaxStackSize();
    }

    @Override
    public void setChanged()
    {
        victim.inventory.setChanged();
        victim.inventoryMenu.broadcastChanges();
    }

    @Override
    public boolean stillValid(PlayerEntity p_70300_1_)
    {
        return true;
    }

}
