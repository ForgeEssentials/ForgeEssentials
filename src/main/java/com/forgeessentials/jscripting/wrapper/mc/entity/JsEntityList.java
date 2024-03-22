package com.forgeessentials.jscripting.wrapper.mc.entity;

import java.util.List;

import com.forgeessentials.util.MappedList;

import net.minecraft.world.entity.Entity;

public class JsEntityList extends MappedList<Entity, JsEntity<?>>
{

    public JsEntityList(List<Entity> that)
    {
        super(that);
    }

    @Override
    protected JsEntity<?> map(Entity in)
    {
        return JsEntity.get(in);
    }

    @Override
    protected Entity unmap(JsEntity<?> in)
    {
        return in.getThat();
    }

}