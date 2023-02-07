package com.forgeessentials.commands.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SeenPlayerInventory implements IInventory
{
    public final PlayerEntity victim;

    public SeenPlayerInventory(PlayerEntity player) {
        victim = player;
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

    @Override
    public ItemStack getItem(int p_70301_1_)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemStack removeItem(int p_70298_1_, int p_70298_2_)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_70304_1_)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setItem(int p_70299_1_, ItemStack p_70299_2_)
    {
        // TODO Auto-generated method stub
        
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
