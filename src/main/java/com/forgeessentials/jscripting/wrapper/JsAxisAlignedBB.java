package com.forgeessentials.jscripting.wrapper;

import net.minecraft.util.math.AxisAlignedBB;

public class JsAxisAlignedBB extends JsWrapper<AxisAlignedBB>
{

    public JsAxisAlignedBB(AxisAlignedBB that)
    {
        super(that);
    }

    public JsAxisAlignedBB setBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
    {
        that = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        return this;
    }

}
