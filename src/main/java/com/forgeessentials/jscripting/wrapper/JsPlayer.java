package com.forgeessentials.jscripting.wrapper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class JsPlayer extends JsEntity<EntityPlayer>
{

    public JsPlayer(EntityPlayer entity)
    {
        super(entity);
    }

    public void setPosition(double x, double y, double z)
    {
        that.posX = x;
        that.posY = y;
        that.posZ = z;
        ((EntityPlayerMP) that).playerNetServerHandler.setPlayerLocation(x, y, z, that.cameraYaw, that.cameraPitch);
    }

    public void setPosition(double x, double y, double z, float yaw, float pitch)
    {
        that.posX = x;
        that.posY = y;
        that.posZ = z;
        ((EntityPlayerMP) that).playerNetServerHandler.setPlayerLocation(x, y, z, yaw, pitch);
    }

}
