package com.ForgeEssentials.WorldBorder.Effects;

import com.ForgeEssentials.WorldBorder.WorldBorder;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

public class smite implements IEffect
{

	@Override
	public void registerConfig(Configuration config, String category)
	{
		config.addCustomCategoryComment(category, "This effect has no option.");
	}

	@Override
	public void execute(WorldBorder wb, EntityPlayerMP player)
	{
		player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ));
	}
}
