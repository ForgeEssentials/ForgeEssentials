package com.ForgeEssentials.WorldBorder.Effects;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.Configuration;

public interface IEffect
{
	void registerConfig(Configuration config, String category);
	
	void execute(EntityPlayerMP player);
}
