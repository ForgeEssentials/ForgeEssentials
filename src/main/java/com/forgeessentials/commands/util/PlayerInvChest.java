package com.forgeessentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryBasic;

public class PlayerInvChest extends InventoryBasic
{
    public ServerPlayerEntity vieuwer;
    public ServerPlayerEntity owner;
    public boolean allowUpdate;

    public PlayerInvChest(ServerPlayerEntity owner, ServerPlayerEntity vieuwer)
    {
        super(owner.getName() + "'s inventory", false, owner.inventory.mainInventory.size());
        this.owner = owner;
        this.vieuwer = vieuwer;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
        CommandsEventHandler.register(this);
        allowUpdate = false;
        for (int id = 0; id < owner.inventory.mainInventory.size(); ++id)
        {
            setInventorySlotContents(id, owner.inventory.mainInventory.get(id));
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
            for (int id = 0; id < owner.inventory.mainInventory.size(); ++id)
            {
                owner.inventory.mainInventory.set(id, getStackInSlot(id));
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
            for (int id = 0; id < owner.inventory.mainInventory.size(); ++id)
            {
                owner.inventory.mainInventory.set(id, getStackInSlot(id));
            }
        }
    }

    public void update()
    {
        allowUpdate = false;
        for (int id = 0; id < owner.inventory.mainInventory.size(); ++id)
        {
            setInventorySlotContents(id, owner.inventory.mainInventory.get(id));
        }
        allowUpdate = true;
        markDirty();
    }
}
