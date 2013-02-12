package com.ForgeEssentials.data.typeInfo;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.ForgeEssentials.api.data.AbstractTypeData;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.ITypeInfo;
import com.ForgeEssentials.data.SavedField;
import com.ForgeEssentials.data.TypeData;

public class TypeInfoItemStack implements ITypeInfo<ItemStack>
{

	private static final String	SIZE		= "stackSize";
	private static final String	ITEM		= "itemID";
	private static final String	DAMAGE		= "damage";
	private static final String	COMPOUND	= "compound";
	
	@Override
	public void build(HashMap<String, Class> map)
	{
		map.put(SIZE, int.class);
		map.put(ITEM, int.class);
		map.put(DAMAGE, int.class);
		//map.put(COMPOUND, NBTTagCompound.class);
		// TODO: when NBT's are saveable..
	}

	@Override
	public AbstractTypeData getTypeDataFromObject(ItemStack stack)
	{
		AbstractTypeData data = DataStorageManager.getDataForType(ItemStack.class);
		
		SavedField field = new SavedField(SIZE, stack.stackSize);
		data.putField(SIZE, stack.stackSize);
		
		field = new SavedField(ITEM, stack.itemID);
		data.putField(ITEM, stack.itemID);
		
		field = new SavedField(DAMAGE, stack.getItemDamage());
		data.putField(DAMAGE, stack.getItemDamage());
		
		//field = new SavedField(COMPOUND, stack.getTagCompound());
		//data.addField(field);
		// TODO: when NBTs are saveab;e

		return data;
	}

	@Override
	public ItemStack reconstruct(IReconstructData data)
	{
		int size = (Integer) data.getFieldValue(SIZE);
		int item = (Integer) data.getFieldValue(ITEM);
		int damage = (Integer) data.getFieldValue(DAMAGE);

		ItemStack stack = new ItemStack(item, size, damage);

		// TODO: when NBTs are saveable
		//NBTTagCompound nbt = (NBTTagCompound) data.getFieldValue(COMPOUND);
		//stack.setTagCompound(nbt);

		return stack;
	}

	@Override
	public boolean canSaveInline()
	{
		return true;
	}

	@Override
	public Class<? extends ItemStack> getType()
	{
		return ItemStack.class;
	}

}
