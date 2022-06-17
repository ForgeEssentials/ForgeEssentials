package com.forgeessentials.jscripting.wrapper.mc.entity;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;

import com.forgeessentials.util.MappedList;

public class JsEntityPlayerList extends MappedList<PlayerEntity, JsEntityPlayer>
{

    public JsEntityPlayerList(List<PlayerEntity> that)
    {
        super(that);
    }

    @Override
    protected JsEntityPlayer map(PlayerEntity in)
    {
        return JsEntityPlayer.get(in);
    }

    @Override
    protected PlayerEntity unmap(JsEntityPlayer in)
    {
        return in.getThat();
    }

}