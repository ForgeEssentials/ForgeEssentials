package com.ForgeEssentials.WorldBorder.Effects;

import com.ForgeEssentials.WorldBorder.ModuleWorldBorder;
import com.ForgeEssentials.util.vector.Vector2;

import net.minecraft.src.EntityLightningBolt;
import net.minecraft.src.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

public class smite implements IEffect
{
	
	@Override
	public void registerConfig(Configuration config, String category)
	{
		config.addCustomCategoryComment(category, "This effect has no option.");
	}

	@Override
	public void execute(EntityPlayerMP player) 
	{
		player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ));
	}
}
