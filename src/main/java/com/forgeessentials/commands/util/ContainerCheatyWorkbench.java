package com.forgeessentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerCheatyWorkbench extends ContainerWorkbench
{

    public ContainerCheatyWorkbench(InventoryPlayer par1InventoryPlayer, World par2World)
    {
        super(par1InventoryPlayer, par2World, BlockPos.ORIGIN);
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return true;
    }

}
