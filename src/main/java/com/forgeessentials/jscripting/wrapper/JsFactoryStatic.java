package com.forgeessentials.jscripting.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.jscripting.wrapper.item.JsItem;
import com.forgeessentials.jscripting.wrapper.item.JsItemStack;
import com.forgeessentials.jscripting.wrapper.world.JsBlock;
import com.forgeessentials.jscripting.wrapper.world.JsWorldPoint;

public class JsFactoryStatic
{

    public JsItemStack createItemStack(JsBlock block, int stackSize)
    {
        return new JsItemStack(new ItemStack(block.getThat(), stackSize));
    }

    public JsItemStack createItemStack(JsBlock block, int stackSize, int damage)
    {
        return new JsItemStack(new ItemStack(block.getThat(), stackSize, damage));
    }

    public JsItemStack createItemStack(JsItem item, int stackSize)
    {
        return new JsItemStack(new ItemStack(item.getThat(), stackSize));
    }

    public JsItemStack createItemStack(JsItem item, int stackSize, int damage)
    {
        return new JsItemStack(new ItemStack(item.getThat(), stackSize, damage));
    }

    public JsPoint<?> createPoint(int x, int y, int z)
    {
        return new JsPoint<>(new Point(x, y, z));
    }

    public JsWorldPoint<?> createWorldPoint(int dimension, int x, int y, int z)
    {
        return new JsWorldPoint<>(new WorldPoint(dimension, x, y, z));
    }

    public JsAxisAlignedBB createAxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        return new JsAxisAlignedBB(AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ));
    }
}
