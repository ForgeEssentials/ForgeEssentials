package com.forgeessentials.jscripting.wrapper.mc.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;

import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.jscripting.wrapper.mc.JsICommandSender;
import com.forgeessentials.jscripting.wrapper.mc.item.JsInventoryPlayer;
import com.forgeessentials.jscripting.fewrapper.fe.JsPoint;
import com.forgeessentials.jscripting.fewrapper.fe.JsWorldPoint;

public class JsEntityPlayer extends JsEntityLivingBase<EntityPlayer>
{
    protected JsInventoryPlayer<?> inventory;

    private JsICommandSender commandSender;

    public JsEntityPlayer(EntityPlayer that)
    {
        super(that);
    }

    public JsEntityPlayer(EntityPlayer that, JsICommandSender commandSender)
    {
        super(that);
        this.commandSender = commandSender;
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

    public JsICommandSender asCommandSender()
    {
        if (commandSender != null || !(that instanceof EntityPlayer))
            return commandSender;
        return commandSender = new JsICommandSender(that, this);
    }

    public JsInventoryPlayer<?> getInventory()
    {
        if (inventory == null)
            inventory = new JsInventoryPlayer<>(that.inventory);
        return inventory;
    }

    public JsPoint<?> getBedLocation(int dimension)
    {
        ChunkCoordinates coord = EntityPlayer.verifyRespawnCoordinates(that.worldObj, that.getBedLocation(dimension), false);
        return coord != null ? new JsWorldPoint<>(new WorldPoint(coord.posX, coord.posY, coord.posZ, dimension)) : null;
    }

}
