package com.forgeessentials.jscripting.wrapper.mc.entity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.util.MappedList;

public class JsEntityPlayerList extends MappedList<EntityPlayer, JsEntityPlayer>
{

    public JsEntityPlayerList(List<EntityPlayer> that)
    {
        super(that);
    }

    @Override
    protected JsEntityPlayer map(EntityPlayer in)
    {
        return JsEntityPlayer.get(in);
    }

    @Override
    protected EntityPlayer unmap(JsEntityPlayer in)
    {
        return in.getThat();
    }

}