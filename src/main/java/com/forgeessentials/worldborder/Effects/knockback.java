package com.forgeessentials.worldborder.Effects;

import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.vector.Vector2;
import com.forgeessentials.worldborder.ModuleWorldBorder;
import com.forgeessentials.worldborder.WorldBorder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;

/**
 * @author UNKNOWN (Dries007?)
 * @author gnif
 */
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
                

        int x = (int)vecp.getX();
        int y = (int)player.posY;        
        int z = (int)vecp.getY();
        int rideY = 0;

        if (!player.worldObj.isAirBlock(x, y, z))
        {
            if (player.ridingEntity != null)
                rideY = player.ridingEntity.serverPosY;

            y = player.worldObj.getActualHeight();            
            while(player.worldObj.isAirBlock(x, y - 1, z))
            {            	
                --y;                
                if (player.isRiding())
                    --rideY;
            }
            
            OutputHandler.chatNotification(player, "Teleported.");
        }

        if (player.ridingEntity != null)
        {

            player.ridingEntity
                    .setLocationAndAngles(x, rideY, z, player.ridingEntity.rotationYaw, player.ridingEntity.rotationPitch);
            player.playerNetServerHandler.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
        }
        else
        {
            player.playerNetServerHandler.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
        }
    }
}
