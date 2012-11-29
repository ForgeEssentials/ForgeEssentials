package com.ForgeEssentials.property;

import java.util.ArrayList;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

import com.ForgeEssentials.core.AreaSelector.AreaBase;
import com.ForgeEssentials.core.AreaSelector.Point;

public class PropertyArea extends AreaBase
{

	public EntityPlayer owner;
	public EntityPlayer buyer;
	public ItemStack[] price;

	public PropertyArea(EntityPlayer owner, Point start, Point end, ItemStack... price)
	{
		super(start, end);
		this.owner = owner;
		this.price = price;
	}

	public void sellProperty()
	{
		ArrayList<ItemStack> addedStacks = new ArrayList<ItemStack>();
		for (ItemStack stack : price)
		{
			if (owner.inventory.addItemStackToInventory(stack))
				addedStacks.add(stack);
			else
			{
				for (ItemStack addedStack : addedStacks)
					for (int i = 0; i < addedStack.stackSize; i++)
						owner.inventory.consumeInventoryItem(addedStack.itemID);// Abrar's PR: ,addedStack.getItemDamage());
				break;
			}
		}
		ArrayList<ItemStack> takenStacks = new ArrayList<ItemStack>();
		for (ItemStack stack : price)
		{
			ItemStack copyStack = stack.copy();
			copyStack.stackSize = 0;
			do
			{
				if (!buyer.inventory.consumeInventoryItem(stack.itemID))
					for (ItemStack takenStack : takenStacks)
						owner.inventory.addItemStackToInventory(copyStack);
				copyStack.stackSize++;
			} while (copyStack.stackSize < stack.stackSize);
			if (copyStack.stackSize == stack.stackSize)
				takenStacks.add(copyStack);
		}
	}
}
