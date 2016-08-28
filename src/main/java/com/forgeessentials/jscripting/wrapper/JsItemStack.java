package com.forgeessentials.jscripting.wrapper;

import net.minecraft.item.ItemStack;

public class JsItemStack extends JsWrapper<ItemStack> // ItemStack is final
{

    protected JsItem item;

    public JsItemStack(ItemStack that)
    {
        super(that);
        this.item = JsItem.get(that.getItem());
    }

    public JsItem getItem()
    {
        return item;
    }

    public int getStackSize()
    {
        return that.stackSize;
    }

    public void setStackSize(int size)
    {
        that.stackSize = size;
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

    // TODO: implement these
    /*public NBTTagList getEnchantmentTagList() // tsgen ignore
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void addEnchantment(JsEnchantment<?> enchantment, int level) // tsgen ignore
    {
        throw new UnsupportedOperationException("not implemented yet");
    }*/

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

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof JsItemStack ? that.isItemEqual(((JsItemStack)obj).getThat()) : false;
    }

}
