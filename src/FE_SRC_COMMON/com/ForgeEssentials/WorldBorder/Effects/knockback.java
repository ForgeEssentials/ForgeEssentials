package com.ForgeEssentials.WorldBorder.Effects;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.WorldBorder.ModuleWorldBorder;
import com.ForgeEssentials.util.vector.Vector2;

public class knockback implements IEffect
{

	@Override
	public void registerConfig(Configuration config, String category)
	{
		config.addCustomCategoryComment(category, "This effect has no option.");
	}

	@Override
	public void execute(EntityPlayerMP player)
	{
		Vector2 vecp = ModuleWorldBorder.getDirectionVector(player);
		vecp.multiply(ModuleWorldBorder.rad);
		vecp.add(new Vector2(ModuleWorldBorder.X, ModuleWorldBorder.Z));

		if (player.ridingEntity != null)
		{
			player.ridingEntity.setLocationAndAngles(vecp.x, player.ridingEntity.prevPosY, vecp.y, player.ridingEntity.rotationYaw,
					player.ridingEntity.rotationPitch);
			player.playerNetServerHandler.setPlayerLocation(vecp.x, player.prevPosY, vecp.y, player.rotationYaw, player.rotationPitch);
		}
		else
		{
			player.playerNetServerHandler.setPlayerLocation(vecp.x, player.prevPosY, vecp.y, player.rotationYaw, player.rotationPitch);
		}
	}
}
