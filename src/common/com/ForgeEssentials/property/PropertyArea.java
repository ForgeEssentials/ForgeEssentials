package com.ForgeEssentials.property;

import java.util.ArrayList;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;

import com.ForgeEssentials.AreaSelector.AreaBase;
import com.ForgeEssentials.AreaSelector.Point;

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
		ArrayList<ItemStack> addedStacks=new ArrayList<ItemStack>();
		for(ItemStack stack:price){
			if(owner.inventory.addItemStackToInventory(stack))
				addedStacks.add(stack);
			else {
				
			}
		}
	}
}
