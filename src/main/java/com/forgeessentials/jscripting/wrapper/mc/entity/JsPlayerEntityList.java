package com.forgeessentials.jscripting.wrapper.mc.entity;

import java.util.List;

import com.forgeessentials.util.MappedList;

import net.minecraft.world.entity.player.Player;

public class JsPlayerEntityList extends MappedList<Player, JsPlayerEntity>
{

    public JsPlayerEntityList(List<Player> that)
    {
        super(that);
    }

    @Override
    protected JsPlayerEntity map(Player in)
    {
        return JsPlayerEntity.get(in);
    }

    @Override
    protected Player unmap(JsPlayerEntity in)
    {
        return in.getThat();
    }

}