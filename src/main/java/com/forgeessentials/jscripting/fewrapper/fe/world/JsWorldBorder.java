package com.forgeessentials.jscripting.fewrapper.fe.world;

import java.util.WeakHashMap;

import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.jscripting.fewrapper.fe.JsPoint;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.JsAreaBase;
import com.forgeessentials.jscripting.wrapper.mc.world.JsWorld;
import com.forgeessentials.worldborder.ModuleWorldBorder;
import com.forgeessentials.worldborder.WorldBorder;

public class JsWorldBorder extends JsWrapper<WorldBorder>
{
    public JsWorldBorder(WorldBorder that)
    {
        super(that);
    }

    public static JsWorldBorder get(JsWorld<?> world) {
        return new JsWorldBorder(ModuleWorldBorder.getInstance().getBorder(world.getThat()));
    }

    public boolean isEnabled()
    {
        return that.isEnabled();
    }

    public void setEnabled(boolean enabled)
    {
        that.setEnabled(enabled);
    }

    public JsPoint<?> getCenter()
    {
        return new JsPoint<>(that.getCenter());
    }

    public void setCenter(JsPoint<?> center)
    {
        that.setCenter(center.getThat());
    }

    public JsPoint<?> getSize()
    {
        return new JsPoint<>(that.getSize());
    }

    public void setSize(JsPoint<?> size)
    {
        that.setSize(size.getThat());
    }

    public AreaShape getShape()
    {
        return that.getShape();
    }

    public void setShape(AreaShape shape)
    {
        that.setShape(shape);
    }

    public JsAreaBase<?> getArea()
    {
        return new JsAreaBase<>(that.getArea());
    }

}
