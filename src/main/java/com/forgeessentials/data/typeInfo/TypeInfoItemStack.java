package com.forgeessentials.data.typeInfo;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.data.api.ITypeInfo;
import com.forgeessentials.data.api.TypeData;

import cpw.mods.fml.common.registry.GameData;

public class TypeInfoItemStack implements ITypeInfo<ItemStack> {
    private static final String SIZE = "stackSize";
    private static final String ITEM = "itemID";
    private static final String DAMAGE = "damage";
    private static final String COMPOUND = "compound";

    public TypeInfoItemStack()
    {
    }

    @Override
    public void build()
    {
    }

    @Override
    public TypeData getTypeDataFromObject(ItemStack stack)
    {
        TypeData data = DataStorageManager.getDataForType(new ClassContainer(ItemStack.class));

        data.putField(SIZE, stack.stackSize);
        data.putField(ITEM, GameData.getItemRegistry().getNameForObject(stack.getItem()));
        data.putField(DAMAGE, stack.getItemDamage());
        data.putField(COMPOUND, stack.getTagCompound());

        return data;
    }

    @Override
    public ClassContainer getTypeOfField(String field)
    {
        if (field == null)
        {
            return null;
        }
        else if (field.equalsIgnoreCase(SIZE) || field.equalsIgnoreCase(DAMAGE))
        {
            return new ClassContainer(int.class);
        }
        else if (field.equalsIgnoreCase(ITEM))
        {
            return new ClassContainer(String.class);
        }
        else if (field.equalsIgnoreCase(COMPOUND))
        {
            return new ClassContainer(NBTTagCompound.class);
        }
        else
        {
            return null;
        }
    }

    @Override
    public String[] getFieldList()
    {
        return new String[] {
                SIZE,
                ITEM,
                DAMAGE,
                COMPOUND
        };
    }

    @Override
    public ItemStack reconstruct(IReconstructData data)
    {
        int size = (Integer) data.getFieldValue(SIZE);
        String item = (String) data.getFieldValue(ITEM);
        int damage = (Integer) data.getFieldValue(DAMAGE);

        ItemStack stack = new ItemStack((Item)GameData.getItemRegistry().getObject(item), size, damage);

        // TODO: when NBTs are saveable
        NBTTagCompound nbt = (NBTTagCompound) data.getFieldValue(COMPOUND);
        stack.setTagCompound(nbt);

        return stack;
    }

    @Override
    public boolean canSaveInline()
    {
        return true;
    }

    @Override
    public ClassContainer getType()
    {
        return new ClassContainer(ItemStack.class);
    }

    @Override
    public Class<?>[] getGenericTypes()
    {
        return null;
    }

    @Override
    public ITypeInfo<?> getInfoForField(String field)
    {
        return DataStorageManager.getInfoForType(getTypeOfField(field));
    }

}
