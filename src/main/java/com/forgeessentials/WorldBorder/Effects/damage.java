package com.forgeessentials.worldborder.Effects;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.Configuration;

import com.forgeessentials.worldborder.WorldBorder;

public class damage implements IEffect
{
	private int	damage	= 1;

	@Override
	public void registerConfig(Configuration config, String category)
	{
		damage = config.get(category, "damage", damage, "Amount of damage in 1/2 hearts.").getInt();
	}

	@Override
	public void execute(WorldBorder wb, EntityPlayerMP player)
	{
		player.attackEntityFrom(DamageSource.generic, damage);
	}
}
