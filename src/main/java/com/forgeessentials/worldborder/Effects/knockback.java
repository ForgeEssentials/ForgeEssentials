package com.forgeessentials.worldborder.Effects;

import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.vector.Vector2;
import com.forgeessentials.worldborder.ModuleWorldBorder;
import com.forgeessentials.worldborder.WorldBorder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

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
        vecp.add(new Vector2(wb.center.x, wb.center.z));

        double y = 0;
        double rideY = 0;

        /* blockExists doesn't work as expected, disabled for now.
        if (player.worldObj.blockExists((int)vecp.x, (int)player.prevPosY, (int)vecp.y))
        {
            y = player.worldObj.getActualHeight();
            if (player.ridingEntity != null)
            {
                rideY = player.ridingEntity.posY;
            }
            while (player.worldObj.blockExists((int)vecp.x, (int)y, (int)vecp.y))
            {
                y--;
                rideY--;
            }
            y = y + 1;
            ChatUtils.sendMessage(player, "Teleported.");
        }
        */

        if (player.ridingEntity != null)
        {
            player.ridingEntity
                    .setLocationAndAngles(vecp.x, player.ridingEntity.posY, vecp.y, player.ridingEntity.rotationYaw, player.ridingEntity.rotationPitch);
            player.playerNetServerHandler.setPlayerLocation(vecp.x, player.prevPosY, vecp.y, player.rotationYaw, player.rotationPitch);
        }
        else
        {
            player.playerNetServerHandler.setPlayerLocation(vecp.x, player.prevPosY, vecp.y, player.rotationYaw, player.rotationPitch);
        }
    }
}
