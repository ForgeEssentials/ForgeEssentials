package com.forgeessentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerCheatyWorkbench extends ContainerWorkbench
{
    public ContainerCheatyWorkbench(InventoryPlayer playerInventory, World world)
    {
        super(playerInventory, world, BlockPos.ORIGIN);
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return true;
    }
}
