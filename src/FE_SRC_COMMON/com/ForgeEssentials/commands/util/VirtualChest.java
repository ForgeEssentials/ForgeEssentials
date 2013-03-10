package com.ForgeEssentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.ForgeEssentials.commands.CommandVirtualchest;

public class VirtualChest extends InventoryBasic
{
	private EntityPlayerMP	owner;

	public VirtualChest(EntityPlayerMP player)
	{
		super(CommandVirtualchest.name, false, CommandVirtualchest.size);
		owner = player;
	}

	@Override
	public void openChest()
	{
		loadInventoryFromNBT(owner.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getTagList("VirtualChestItems"));
		super.openChest();
	}

	@Override
	public void closeChest()
	{
		NBTTagCompound temp = owner.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		temp.setTag("VirtualChestItems", saveInventoryToNBT());
		owner.getEntityData().setCompoundTag(EntityPlayer.PERSISTED_NBT_TAG, temp);
		super.closeChest();
	}

	public void loadInventoryFromNBT(NBTTagList par1NBTTagList)
	{
		int var2;

		for (var2 = 0; var2 < getSizeInventory(); ++var2)
		{
			setInventorySlotContents(var2, (ItemStack) null);
		}

		for (var2 = 0; var2 < par1NBTTagList.tagCount(); ++var2)
		{
			NBTTagCompound var3 = (NBTTagCompound) par1NBTTagList.tagAt(var2);
			int var4 = var3.getByte("Slot") & 255;

			if (var4 >= 0 && var4 < getSizeInventory())
			{
				setInventorySlotContents(var4, ItemStack.loadItemStackFromNBT(var3));
			}
		}
	}

	public NBTTagList saveInventoryToNBT()
	{
		NBTTagList var1 = new NBTTagList("VirtualChestItems");

		for (int var2 = 0; var2 < getSizeInventory(); ++var2)
		{
			ItemStack var3 = getStackInSlot(var2);

			if (var3 != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var2);
				var3.writeToNBT(var4);
				var1.appendTag(var4);
			}
		}

		return var1;
	}
}
