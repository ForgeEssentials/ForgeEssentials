package com.forgeessentials.jscripting.wrapper.mc.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.world.JsBlock;

public class JsItemStack extends JsWrapper<ItemStack> // ItemStack is final
{

    protected JsItem item;

    /**
     * @tsd.ignore
     */
    public static JsItemStack get(ItemStack itemStack)
    {
        return itemStack == null ? null : new JsItemStack(itemStack);
    }

    private JsItemStack(ItemStack that)
    {
        super(that);
        this.item = JsItem.get(that.getItem());
    }

    public JsItemStack(JsBlock block, int stackSize)
    {
        this(new ItemStack(block.getThat(), stackSize));
    }

    public JsItemStack(JsBlock block, int stackSize, int damage)
    {
        this(new ItemStack(block.getThat(), stackSize, damage));
    }

    public JsItemStack(JsItem item, int stackSize)
    {
        this(new ItemStack(item.getThat(), stackSize));
    }

    public JsItemStack(JsItem item, int stackSize, int damage)
    {
        this(new ItemStack(item.getThat(), stackSize, damage));
    }

    public JsItem getItem()
    {
        return item;
    }

    public int getStackSize()
    {
        return that.getCount();
    }

    public void setStackSize(int size)
    {
        that.setCount(size);
    }

    public int getMaxStackSize()
    {
        return that.getMaxStackSize();
    }

    public boolean isStackable()
    {
        return that.isStackable();
    }

    public boolean isDamageable()
    {
        return that.isItemStackDamageable();
    }

    public boolean isDamaged()
    {
        return that.isItemDamaged();
    }

    public int getDamage()
    {
        return that.getItemDamage();
    }

    public void setDamage(int damage)
    {
        that.setItemDamage(damage);
    }

    public int getMaxDamage()
    {
        return that.getMaxDamage();
    }

    public String getDisplayName()
    {
        return that.getDisplayName();
    }

    public void setDisplayName(String name)
    {
        that.setStackDisplayName(name);
    }

    public boolean hasDisplayName()
    {
        return that.hasDisplayName();
    }

    public boolean isItemEnchanted()
    {
        return that.isItemEnchanted();
    }

    public int getRepairCost()
    {
        return that.getRepairCost();
    }

    public void setRepairCost(int cost)
    {
        that.setRepairCost(cost);
    }

    /**
     * @tsd.ignore
     */
    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof JsItemStack ? that.isItemEqual(((JsItemStack) obj).getThat()) : false;
    }

    /**
     * @tsd.ignore
     */
    public String _getNbt()
    {
        return that.getTagCompound() == null ? null : DataManager.toJson(that.getTagCompound());
    }

    /**
     * @tsd.ignore
     */
    public void _setNbt(String value)
    {
        that.setTagCompound(value == null ? null : DataManager.fromJson(value, NBTTagCompound.class));
    }

}
