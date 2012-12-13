package com.ForgeEssentials.WorldBorder.Penalties;

import net.minecraftforge.common.Configuration;

public class knockback implements IPenalty
{
	private int Strenght_XZ;
	private int Strenght_Y;
	
	@Override
	public void registerConfig(Configuration config, String category)
	{
		Strenght_XZ = config.get(category, "Strenght_XZ", 1, "The knockback strenght in the horizontal plane.").getInt();
		Strenght_Y = config.get(category, "Strenght_Y", 1, "The knockback strenght in the vertical plane. Pos numers are up, neg are down.").getInt();
	}
}
