package com.forgeessentials.jscripting.wrapper.mc.util;

import com.forgeessentials.jscripting.wrapper.JsWrapper;

import net.minecraft.world.phys.AABB;

public class JsAxisAlignedBB extends JsWrapper<AABB>
{

    public JsAxisAlignedBB(AABB that)
    {
        super(that);
    }

    public JsAxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        this(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
    }

    public JsAxisAlignedBB setBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        that = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        return this;
    }

}
