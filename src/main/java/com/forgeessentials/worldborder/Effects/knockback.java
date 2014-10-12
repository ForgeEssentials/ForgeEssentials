package com.forgeessentials.worldborder.Effects;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.vector.Vector2;
import com.forgeessentials.worldborder.ModuleWorldBorder;
import com.forgeessentials.worldborder.WorldBorder;

public class knockback implements IEffect {
    @Override
    public void registerConfig(Configuration config, String category)
    {
        config.addCustomCategoryComment(category, "This effect has no options.");
    }

    @Override
    public void execute(WorldBorder wb, EntityPlayerMP player)
    {
        Vector2 vecp = ModuleWorldBorder.getDirectionVector(wb.center, player);
        vecp.multiply(wb.rad);
        vecp.add(new Vector2(wb.center.getX(), wb.center.getZ()));

        double y = 0;
        double rideY = 0;

        if (player.worldObj.blockExists((int)vecp.getX(), (int)player.prevPosY, (int)vecp.getY()))
        {
            y = player.worldObj.getActualHeight();
            rideY = player.ridingEntity.posY;
            while (player.worldObj.blockExists((int)vecp.getX(), (int)y, (int)vecp.getY()))
            {
                y--;
                rideY--;
            }
            y = y + 1;
            OutputHandler.sendMessage(player, "Teleported.");
        }

        if (player.ridingEntity != null)
        {
            player.ridingEntity
                    .setLocationAndAngles(vecp.getX(), rideY, vecp.getY(), player.ridingEntity.rotationYaw, player.ridingEntity.rotationPitch);
            player.playerNetServerHandler.setPlayerLocation(vecp.getX(), y, vecp.getY(), player.rotationYaw, player.rotationPitch);
        }
        else
        {
            player.playerNetServerHandler.setPlayerLocation(vecp.getX(), y, vecp.getY(), player.rotationYaw, player.rotationPitch);
        }
    }
}
