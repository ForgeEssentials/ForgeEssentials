package com.forgeessentials.jscripting.wrapper.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.util.MappedList;

public class JsEntityList extends MappedList<Entity, JsEntity<?>>
{

    public JsEntityList(List<Entity> list)
    {
        super(list);
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