package com.forgeessentials.jscripting.wrapper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

//classdef interface EntityPlayer extends EntityLivingBase, ICommandSender
public class JsEntityPlayer extends JsEntityLivingBase<EntityPlayer>
    protected JsInventory<?> inventory;

    private JsCommandSender commandSender;

    public JsEntityPlayer(EntityPlayer player)
    {
        super(player);
    }

    public JsEntityPlayer(EntityPlayer player, JsCommandSender commandSender)
    {
        super(player);
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

    public JsCommandSender getCommandSender()
    {
        if (commandSender != null || !(that instanceof EntityPlayer))
            return commandSender;
        return commandSender = new JsCommandSender(that, this);
    }

    public JsInventory<?> getInventory()
    {
        if (inventory == null)
            inventory = new JsInventory<>(that.inventory);
        return inventory;
    }
}
