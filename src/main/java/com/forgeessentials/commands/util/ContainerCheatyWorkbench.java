package com.forgeessentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerCheatyWorkbench extends ContainerWorkbench
{
    private World world;

    public ContainerCheatyWorkbench(InventoryPlayer playerInventory, World world)
    {
        super(playerInventory, world, BlockPos.ORIGIN);
        this.world = world;
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        final IRecipe recipe = CraftingManager.findMatchingRecipe(craftMatrix, world);
        craftResult.setInventorySlotContents(0, recipe != null ? recipe.getRecipeOutput() : ItemStack.EMPTY);
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);

        if (!world.isRemote)
        {
            for (int var2 = 0; var2 < 9; ++var2)
            {
                ItemStack var3 = craftMatrix.removeStackFromSlot(var2);

                if (var3 != ItemStack.EMPTY)
                {
                    par1EntityPlayer.dropItem(var3, true);
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack var3 = null;
        Slot var4 = inventorySlots.get(par2);

        if (var4 != null && var4.getHasStack())
        {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();

            if (par2 == 0)
            {
                if (!mergeItemStack(var5, 10, 46, true))
                {
                    return null;
                }

                var4.onSlotChange(var5, var3);
            }
            else if (par2 >= 10 && par2 < 37)
            {
                if (!mergeItemStack(var5, 37, 46, false))
                {
                    return null;
                }
            }
            else if (par2 >= 37 && par2 < 46)
            {
                if (!mergeItemStack(var5, 10, 37, false))
                {
                    return null;
                }
            }
            else if (!mergeItemStack(var5, 10, 46, false))
            {
                return null;
            }

            if (var5.isEmpty())
            {
                var4.putStack(ItemStack.EMPTY);
            }
            else
            {
                var4.onSlotChanged();
            }

            if (var5.getCount() == var3.getCount())
            {
                return null;
            }

            var4.onTake(par1EntityPlayer, var5);
        }

        return var3;
    }
}
