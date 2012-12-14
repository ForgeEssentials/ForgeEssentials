package com.ForgeEssentials.WorldBorder.Effects;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

public class damage implements IEffect
{	
	private int damage = 1;
	
	@Override
	public void registerConfig(Configuration config, String category)
	{
		damage = config.get(category, "damage", damage, "Amount of damage in 1/2 harts.").getInt();
	}

	@Override
	public void execute(EntityPlayerMP player) 
	{
		player.attackEntityFrom(DamageSource.generic, damage);
	}
}
