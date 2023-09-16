package com.forgeessentials.jscripting.wrapper.mc.entity;

import java.util.List;

import com.forgeessentials.util.MappedList;

import net.minecraft.entity.player.PlayerEntity;

public class JsPlayerEntityList extends MappedList<PlayerEntity, JsPlayerEntity>
{

    public JsPlayerEntityList(List<PlayerEntity> that)
    {
        super(that);
    }

    @Override
    protected JsPlayerEntity map(PlayerEntity in)
    {
        return JsPlayerEntity.get(in);
    }

    @Override
    protected PlayerEntity unmap(JsPlayerEntity in)
    {
        return in.getThat();
    }

}