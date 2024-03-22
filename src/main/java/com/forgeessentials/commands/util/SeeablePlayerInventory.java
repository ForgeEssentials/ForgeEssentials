package com.forgeessentials.commands.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class SeeablePlayerInventory implements Container
{
    public final Player victim;

    public SeeablePlayerInventory(Player player)
    {
        victim = player;
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

    @Override
    public ItemStack getItem(int section)
    {
    	return victim.inventory.getItem(section);
    }

    @Override
    public ItemStack removeItem(int section, int number)
    {
    	return victim.inventory.removeItem(section, number);
    }

    @Override
    public ItemStack removeItemNoUpdate(int section)
    {
    	return victim.inventory.removeItemNoUpdate(section);
    }

    @Override
    public void setItem(int section, ItemStack stack)
    {
    	victim.inventory.setItem(section, stack);
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
    public boolean stillValid(Player p_70300_1_)
    {
        return true;
    }

    @Override
    public boolean canPlaceItem(int section, ItemStack stack)
    {
    	return victim.inventory.canPlaceItem(section, stack);
    }

    @Override
    public void clearContent()
    {
        victim.inventory.clearContent();
    }
}
