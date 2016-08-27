package com.forgeessentials.jscripting.wrapper;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

public class JsPlayer
{

    private EntityPlayer that;

    public JsPlayer(EntityPlayer player)
    {
        this.that = player;
    }

    public EntityPlayer getThat()
    {
        return that;
    }

    public String getName()
    {
        return that.getCommandSenderName();
    }

    public String getId()
    {
        return that.getPersistentID().toString();
    }

    public UUID getUuid()
    {
        return that.getPersistentID();
    }

    public float getHealth()
    {
        return that.getHealth();
    }

    public float getMaxHealth()
    {
        return that.getMaxHealth();
    }

    public void setHealth(float value)
    {
        that.setHealth(value);
    }

}
