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
        return victim.getInventory().isEmpty();
    }

    @Override
    public ItemStack getItem(int section)
    {
    	return victim.getInventory().getItem(section);
    }

    @Override
    public ItemStack removeItem(int section, int number)
    {
    	return victim.getInventory().removeItem(section, number);
    }

    @Override
    public ItemStack removeItemNoUpdate(int section)
    {
    	return victim.getInventory().removeItemNoUpdate(section);
    }

    @Override
    public void setItem(int section, ItemStack stack)
    {
    	victim.getInventory().setItem(section, stack);
    }

    @Override
    public int getMaxStackSize()
    {
        return victim.getInventory().getMaxStackSize();
    }

    @Override
    public void setChanged()
    {
        victim.getInventory().setChanged();
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
    	return victim.getInventory().canPlaceItem(section, stack);
    }

    @Override
    public void clearContent()
    {
        victim.getInventory().clearContent();
    }
}
