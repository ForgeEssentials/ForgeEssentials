package com.forgeessentials.afterlife;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class InventoryGrave extends SimpleContainer
{

    private Grave grave;
    private String name;

    public InventoryGrave(Grave grave)
    {
        super(45);
        name = UserIdent.get(grave.owner).getUsername() + "'s grave.";
        this.grave = grave;
    }

    @Override
    public void startOpen(Player player)
    {
        grave.setOpen(true);
        for (int i = 0; i < getContainerSize(); i++)
            setItem(i, grave.inventory.size() > 0 ? grave.inventory.remove(0) : ItemStack.EMPTY);
        super.startOpen(player);
    }

    @Override
    public void stopOpen(Player player)
    {
        for (int i = 0; i < getContainerSize(); i++)
        {
            ItemStack is = getItem(i);
            if (is != ItemStack.EMPTY)
                grave.inventory.add(is);
        }
        grave.setOpen(false);
        super.stopOpen(player);
        if (grave.inventory.isEmpty())
        {
            WorldPoint point = grave.point;
            grave.remove(false);
            point.getWorld().removeBlock(point.getBlockPos(), false);
        }
    }

    public Component getDisplayName()
    {
        return new TextComponent(name);
    }

}
