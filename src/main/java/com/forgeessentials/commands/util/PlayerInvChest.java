package com.forgeessentials.commands.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class PlayerInvChest extends InventoryBasic
{
    public ServerPlayerEntity vieuwer;
    public ServerPlayerEntity owner;
    public boolean allowUpdate;

    public PlayerInvChest(ServerPlayerEntity owner, ServerPlayerEntity vieuwer)
    {
        super(owner.getName() + "'s inventory", false, owner.inventory.items.size());
        this.owner = owner;
        this.vieuwer = vieuwer;
    }

    @Override
    public void openInventory(PlayerEntity player)
    {
        CommandsEventHandler.register(this);
        allowUpdate = false;
        for (int id = 0; id < owner.inventory.items.size(); ++id)
        {
            setInventorySlotContents(id, owner.inventory.items.get(id));
        }
        allowUpdate = true;
        super.openInventory(player);
    }

    @Override
    public void closeInventory(PlayerEntity player)
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
