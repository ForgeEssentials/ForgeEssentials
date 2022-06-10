package com.forgeessentials.commands.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import com.forgeessentials.commands.item.CommandVirtualchest;
import com.forgeessentials.util.PlayerUtil;

public class VirtualChest extends InventoryBasic
{

    public static final String VIRTUALCHEST_TAG = "VirtualChestItems";

    private ServerPlayerEntity owner;

    public VirtualChest(ServerPlayerEntity player)
    {
        super(CommandVirtualchest.name, false, CommandVirtualchest.size);
        owner = player;
    }

    @Override
    public void openInventory(PlayerEntity player)
    {
        loadInventoryFromNBT(PlayerUtil.getPersistedTag(owner, false).getTagList(VIRTUALCHEST_TAG, 10));
        super.openInventory(player);
    }

    @Override
    public void closeInventory(PlayerEntity player)
    {
        PlayerUtil.getPersistedTag(owner, true).setTag(VIRTUALCHEST_TAG, saveInventoryToNBT());
        super.closeInventory(player);
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        PlayerUtil.getPersistedTag(owner, true).setTag(VIRTUALCHEST_TAG, saveInventoryToNBT());
    }

    public void loadInventoryFromNBT(ListNBT tag)
    {
        for (int slotIndex = 0; slotIndex < getSizeInventory(); ++slotIndex)
            setInventorySlotContents(slotIndex, (ItemStack) ItemStack.EMPTY);
        for (int tagIndex = 0; tagIndex < tag.tagCount(); ++tagIndex)
        {
        	CompoundNBT tagSlot = tag.getCompound(tagIndex);
            int var4 = tagSlot.getByte("Slot") & 255;
            if (var4 >= 0 && var4 < getSizeInventory())
                setInventorySlotContents(var4, new ItemStack(tagSlot));
        }
    }

    public ListNBT  saveInventoryToNBT()
    {
    	ListNBT var1 = new ListNBT();

        for (int var2 = 0; var2 < getSizeInventory(); ++var2)
        {
            ItemStack var3 = getStackInSlot(var2);

            if (var3 != null)
            {
            	CompoundNBT var4 = new CompoundNBT();
                var4.setByte("Slot", (byte) var2);
                var3.writeToNBT(var4);
                var1.appendTag(var4);
            }
        }

        return var1;
    }

}
