package com.forgeessentials.worldborder.Effects;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

import com.forgeessentials.util.vector.Vector2;
import com.forgeessentials.worldborder.ModuleWorldBorder;
import com.forgeessentials.worldborder.WorldBorder;

public class knockback implements IEffect
{
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

		if (player.ridingEntity != null)
		{
			player.ridingEntity.setLocationAndAngles(vecp.x, player.ridingEntity.prevPosY, vecp.y, player.ridingEntity.rotationYaw, player.ridingEntity.rotationPitch);
			player.playerNetServerHandler.setPlayerLocation(vecp.x, player.prevPosY, vecp.y, player.rotationYaw, player.rotationPitch);
		}
		else
		{
			player.playerNetServerHandler.setPlayerLocation(vecp.x, player.prevPosY, vecp.y, player.rotationYaw, player.rotationPitch);
		}
	}
}
