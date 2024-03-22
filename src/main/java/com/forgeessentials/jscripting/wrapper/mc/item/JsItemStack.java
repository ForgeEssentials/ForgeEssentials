package com.forgeessentials.jscripting.wrapper.mc.item;

import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.world.JsBlock;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;

public class JsItemStack extends JsWrapper<ItemStack> // ItemStack is final
{

    protected JsItem item;

    /**
     * @tsd.ignore
     */
    public static JsItemStack get(ItemStack itemStack)
    {
        return itemStack == ItemStack.EMPTY ? EMPTY : new JsItemStack(itemStack, -1);
    }

    public static final JsItemStack EMPTY = new JsItemStack(ItemStack.EMPTY, -1);

    private JsItemStack(ItemStack that, int damage)
    {
        super(that);
        if (damage != -1)
        {
            setDamage(damage);
        }
        this.item = JsItem.get(that.getItem());
    }

    public JsItemStack(JsBlock block, int stackSize)
    {
        this(new ItemStack(block.getThat(), stackSize), -1);
    }

    public JsItemStack(JsBlock block, int stackSize, int damage)
    {
        this(new ItemStack(block.getThat(), stackSize), damage);
    }

    public JsItemStack(JsItem item, int stackSize)
    {
        this(new ItemStack(item.getThat(), stackSize), -1);
    }

    public JsItemStack(JsItem item, int stackSize, int damage)
    {
        this(new ItemStack(item.getThat(), stackSize), damage);
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
        return that.isDamageableItem();
    }

    public boolean isDamaged()
    {
        return that.isDamaged();
    }

    public int getDamage()
    {
        return that.getDamageValue();
    }

    public void setDamage(int damage)
    {
        that.setDamageValue(damage);
    }

    public int getMaxDamage()
    {
        return that.getMaxDamage();
    }

    public String getDisplayName()
    {
        return that.getDisplayName().toString();
    }

    public void setDisplayName(String name)
    {
        that.setHoverName(new TextComponent(name));
    }

    public boolean hasDisplayName()
    {
        return that.hasCustomHoverName();
    }

    public boolean isItemEnchanted()
    {
        return that.isEnchanted();
    }

    public int getRepairCost()
    {
        return that.getBaseRepairCost();
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
        return obj instanceof JsItemStack && that.equals(((JsItemStack) obj).getThat());
    }

    /**
     * @tsd.ignore
     */
    public String _getNbt()
    {
        return that.getTag() == null ? null : DataManager.toJson(that.getTag());
    }

    /**
     * @tsd.ignore
     */
    public void _setNbt(String value)
    {
        that.setTag(value == null ? null : DataManager.fromJson(value, CompoundTag.class));
    }

}
