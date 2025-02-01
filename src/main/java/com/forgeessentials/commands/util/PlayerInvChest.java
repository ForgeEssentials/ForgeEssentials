package com.forgeessentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;

public class PlayerInvChest extends InventoryBasic
{
    public EntityPlayerMP vieuwer;
    public EntityPlayerMP owner;
    public boolean allowUpdate;

    public PlayerInvChest(EntityPlayerMP owner, EntityPlayerMP vieuwer)
    {
        super(owner.getName() + "'s inventory", false, owner.inventory.mainInventory.length);
        this.owner = owner;
        this.vieuwer = vieuwer;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
        CommandsEventHandler.register(this);
        allowUpdate = false;
        for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
        {
            setInventorySlotContents(id, owner.inventory.mainInventory[id]);
        }
        allowUpdate = true;
        super.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
        CommandsEventHandler.remove(this);
        if (allowUpdate)
        {
            for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
            {
                owner.inventory.mainInventory[id] = getStackInSlot(id);
            }
        }
        markDirty();
        super.closeInventory(player);
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        if (allowUpdate)
        {
            for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
            {
                owner.inventory.mainInventory[id] = getStackInSlot(id);
            }
        }
    }

    public void update()
    {
        allowUpdate = false;
        for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
        {
            setInventorySlotContents(id, owner.inventory.mainInventory[id]);
        }
        allowUpdate = true;
        markDirty();
    }
}
